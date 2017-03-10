package onethreeseven.spm.model;

import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A sequence of {@link SequenceEdge} with cover computed during construction.
 * @author Luke Bermingham
 */
public class CoveredSequence {

    private static final String coverStrPrefix = " #COVER:";

    int cover;
    final List<SequenceEdge> sequence;

    public CoveredSequence(List<SequenceEdge> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("Sequence cannot be empty or null to make a cover pattern.");
        }

        //check for duplicates
        int totalCover = 0;
        {
            TIntHashSet edgesIds = new TIntHashSet();
            for (SequenceEdge edge : sequence) {
                boolean newEdge = edgesIds.add(edge.id);
                if (newEdge) {
                    totalCover += edge.getCover();
                }
            }
        }
        this.cover = totalCover;
        this.sequence = sequence;
    }

    public List<SequenceEdge> getSequence(){
        return sequence;
    }

    public int getCover(){
        return this.cover;
    }

    public int size(){
        return this.sequence.size();
    }

    public SequenceEdge get(int i){
        return this.sequence.get(i);
    }

    public SequenceEdge remove(int i){
        return this.sequence.remove(i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Iterator<SequenceEdge> iter = sequence.iterator();
        if(iter.hasNext()){
            SequenceEdge curEdge = iter.next();
            sb.append(curEdge.source.id).append(" ");
            sb.append(curEdge.destination.id).append(" ");
        }
        while(iter.hasNext()){
            sb.append(iter.next().destination.id).append(" ");
        }
        sb.append(coverStrPrefix).append(this.cover);
        return sb.toString();
    }

    int[] getNodeIds(){
        int[] ids = new int[sequence.size()+1];
        Iterator<SequenceEdge> iter = sequence.iterator();
        ids[0] = iter.next().source.id;
        int i = 1;
        while(iter.hasNext()){
            ids[i] = iter.next().destination.id;
            i++;
        }
        return ids;
    }

    private int[] getEdgeIds(){
        int[] ids = new int[sequence.size()];
        int i = 0;
        for (SequenceEdge sequenceEdge : sequence) {
            ids[i] = sequenceEdge.id;
            i++;
        }
        return ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof CoveredSequence)) return false;
        CoveredSequence that = (CoveredSequence) o;
        return cover == that.cover && Arrays.equals(this.getEdgeIds(), that.getEdgeIds());

    }

    @Override
    public int hashCode() {
        int result = cover;
        result = 31 * result + Arrays.hashCode(getEdgeIds());
        return result;
    }

}
