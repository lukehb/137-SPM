package onethreeseven.spm.model;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import onethreeseven.collections.IntRangeSet;
import onethreeseven.collections.Range;

/**
 * Tracks sequence visitation and the indices of the sequence that made the visit.
 * @author Luke Bermingham
 */
public class Visitations {

    //<SequenceId, Visit indices>
    private final TIntObjectHashMap<IntRangeSet> visitorMap;

    public Visitations(Visitations toCopy){
        this.visitorMap = new TIntObjectHashMap<>(toCopy.visitorMap.size());
        //copy all elements in the map (note, have to actually clone the value map)
        TIntObjectIterator<IntRangeSet> iter = toCopy.visitorMap.iterator();
        while(iter.hasNext()){
            iter.advance();
            int seqId = iter.key();
            IntRangeSet visitorsCopy = iter.value();
            this.visitorMap.put(seqId, new IntRangeSet(visitorsCopy));
        }
    }

    public Visitations(){
        this.visitorMap = new TIntObjectHashMap<>();
    }

    void addVisitor(int id, Range indices){
        IntRangeSet visitorIndices = visitorMap.get(id);
        if(visitorIndices == null){
            visitorIndices = new IntRangeSet();
            visitorMap.put(id, visitorIndices);
        }
        visitorIndices.add(indices);
    }

    public int getNumberOfVisitors(){
        return visitorMap.size();
    }

    public int getTotalVisitedIndices(){
        int cover = 0;
        TIntObjectIterator<IntRangeSet> visitors = visitorMap.iterator();
        while(visitors.hasNext()){
            visitors.advance();
            cover += visitors.value().getCover();
        }
        return cover;
    }

    public void addComplement(Visitations other){
        TIntObjectIterator<IntRangeSet> iter = other.visitorMap.iterator();
        while(iter.hasNext()){
            iter.advance();
            int seqId = iter.key();
            IntRangeSet existing =  this.visitorMap.get(seqId);
            //case: should not add ranges that are already contained
            if(existing != null){
                IntRangeSet otherRanges = iter.value();
                outerloop:
                for (Range otherRange : otherRanges) {
                    for (Range existingRange : existing) {
                        if(existingRange.contains(otherRange)){
                            continue outerloop;
                        }
                    }
                    //made it this far, means we don't contain it
                    existing.add(otherRange);
                }
            }
        }
    }

    /**
     * Add all the ranges from the "other" visitations, regardless of gaps etc.
     * @param other The other ranges to add into this one.
     */
    public void union(Visitations other){
        TIntObjectIterator<IntRangeSet> iter = other.visitorMap.iterator();
        while(iter.hasNext()){
            iter.advance();
            int seqId = iter.key();
            IntRangeSet existing =  this.visitorMap.get(seqId);
            if(existing == null){
                existing = new IntRangeSet();
                //add the ranges from the "other" to the "existing"
                for (Range otherRange : iter.value()) {
                    existing.add(otherRange);
                }
                this.visitorMap.put(seqId, existing);
            }
            //case: should not add ranges that are already contained
            else{
                IntRangeSet otherRanges = iter.value();
                outerloop:
                for (Range otherRange : otherRanges) {
                    for (Range existingRange : existing) {
                        if(existingRange.contains(otherRange)){
                            continue outerloop;
                        }
                    }
                    existing.add(otherRange);
                }
            }

        }
    }

    public Visitations minus(Visitations other){
        Visitations minus = new Visitations();

        TIntObjectIterator<IntRangeSet> curIter = this.visitorMap.iterator();
        while(curIter.hasNext()){
            curIter.advance();
            int seqId = curIter.key();
            IntRangeSet curSeqIndices = curIter.value();
            IntRangeSet otherSeqIndices = other.visitorMap.get(seqId);
            //case: did not contain any indices from "other" for this sequence id
            if(otherSeqIndices == null){
                minus.visitorMap.put(seqId, new IntRangeSet(curSeqIndices));
                continue;
            }
            //case: there is some ranges, check if they intersect/contain at all
            IntRangeSet toAdd = new IntRangeSet();
            for (Range curRange : curSeqIndices) {
                for (Range otherRange : otherSeqIndices) {
                    Range[] ranges = curRange.minus(otherRange);
                    if(ranges == null){continue;}
                    for (Range range : ranges) {
                        if(range.getRange() > 0){
                            toAdd.add(range);
                        }
                    }
                }
            }

            if(!toAdd.isEmpty()){
                minus.visitorMap.put(seqId, toAdd);
            }
        }
        return minus;
    }

    @Override
    public String toString() {
        return visitorMap.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visitations that = (Visitations) o;
        return visitorMap.equals(that.visitorMap);
    }

    @Override
    public int hashCode() {
        return visitorMap.hashCode();
    }


    /**
     * For each sequence id, try to connect the last range in pre indices with the any range in post indices.
     * @param pre visitations
     * @param post visitations
     * @param maxGap the maximum allowed gap between ranges
     * @return The visitations of pre expanded by one post range (where possible).
     */
    public static Visitations tryConnect(Visitations pre, Visitations post, int maxGap, int minSup){

        //get the intersection of sequence ids from pre and post
        TIntSet seqIds = new TIntHashSet(pre.visitorMap.keySet());
        seqIds.retainAll(post.visitorMap.keySet());
        Visitations connected = new Visitations();

        int maxPossibleSup = seqIds.size();

        if(maxPossibleSup < minSup){
            return connected;
        }

        TIntIterator seqIdIter = seqIds.iterator();

        while(seqIdIter.hasNext()){
            int seqId = seqIdIter.next();
            IntRangeSet postSeqIndices = post.visitorMap.get(seqId);
            IntRangeSet preSeqIndices = pre.visitorMap.get(seqId);

            IntRangeSet connectedSeqIndices = new IntRangeSet();

            //only join last range in "pre" with a single range in "post"
            Range preRange = preSeqIndices.getHighest();

            //find any ranges that are within maxGap
            for (Range postRange : postSeqIndices) {
                if(postRange.isAfter(preRange)){
                    //postRange is sorted, so if it can't connect to this one,
                    //it can't connect to the next one either, so break
                    if(!preRange.isConnectibleBefore(postRange, maxGap)){
                        break;
                    }
                    connectedSeqIndices.add(preRange);
                    connectedSeqIndices.add(postRange);
                    break;
                }
            }
            //case: we have some connected sequence indices
            if(!connectedSeqIndices.isEmpty()){
                connected.visitorMap.put(seqId, connectedSeqIndices);
            }
            //case: we could not connect the ranges
            else{
                maxPossibleSup--;
                //case: failed to connect too many times, support cannot be met now
                if(maxPossibleSup < minSup){
                    return connected;
                }
            }
        }
        return connected;
    }

    /**
     * For each sequence, merge all possible pre-ranges with post-ranges.
     * @param pre Visitations
     * @param post Visitations
     * @return The combined visitations each sequence made along pre and post (where possible).
     */
    public static Visitations tryConnectTouching(Visitations pre, Visitations post){
        TIntObjectIterator<IntRangeSet> preIter = pre.visitorMap.iterator();

        Visitations connected = new Visitations();

        while(preIter.hasNext()){
            preIter.advance();
            int seqId = preIter.key();
            //check if "post" has any indices for this sequence id
            IntRangeSet postSeqIndices = post.visitorMap.get(seqId);
            if(postSeqIndices == null){
                continue;
            }
            //find any ranges that are within maxGap
            IntRangeSet preSeqIndices = preIter.value();

            IntRangeSet connectedSeqIndices = new IntRangeSet();

            //join as many pre and post as possible
            for (Range preRange : preSeqIndices) {
                for (Range postRange : postSeqIndices) {
                    if(!preRange.isConnectibleBefore(postRange, 1)){continue;}
                    connectedSeqIndices.add(preRange);
                    connectedSeqIndices.add(postRange);
                }
            }

            //case: we have some connected sequence indices
            if(!connectedSeqIndices.isEmpty()){
                connected.visitorMap.put(seqId, connectedSeqIndices);
            }
        }
        return connected;
    }

}
