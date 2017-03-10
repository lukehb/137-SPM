package onethreeseven.spm.algorithm;

import onethreeseven.collections.Range;
import onethreeseven.spm.data.SequentialPatternWriter;
import onethreeseven.spm.model.*;
import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * Using a sequence graph this algorithm mines a set of contiguous, non-overlapping, sequential patterns.
 * Each pattern it mines tries to maximise pattern cover whilst maintaining a minimum specified support.
 * @author Luke Bermingham
 */
public class ProtoMiner {

    private static final Logger log = Logger.getLogger(ProtoMiner.class.getSimpleName());

    /**
     * Mines the patterns and delegates what to do with them to a {@link PatternProcessor}.
     * @param seqDb The sequence database to mine.
     * @param minSup The minimum required support to be considered a pattern.
     * @param processor The pattern processor.
     */
    private void run(SequenceGraph g, int[][] seqDb, int minSup, int minLen, PatternProcessor processor){

        BitSet processedEdges = new BitSet();

        final Comparator<CoveredSequence> comparator = (o1, o2) -> o1.getCover() - o2.getCover();

        //get a list of pattern chunks from the raw sequences (splitting on supported edges)
        List<CoveredSequence> sequences = toPatterns(g, seqDb, minSup, minLen);
        Collections.sort(sequences, comparator);

        while(!sequences.isEmpty()){

            CandidatePattern pattern = getNextPattern(sequences, minSup, minLen);
            if(pattern != null){
                if(pattern.getSequence().size()+1 >= minLen){
                    //do something with the pattern, i.e write to file or store in memory
                    processor.process(pattern);
                }

                //add the pattern to the list of processed edges
                for (SequenceEdge sequenceEdge : pattern.getSequence()) {
                    processedEdges.set(sequenceEdge.id);
                }
                //go through sequences and split based on edges that are now processed
                refineSequence(sequences, processedEdges);
                Collections.sort(sequences, comparator);
            }

        }
    }

    /**
     * Finds the patterns in the sequence graph and writes them to a file.
     * @param seqDb The sequence database to mine.
     * @param minSup The minimum number of times that a contiguous sub-sequence must occur in the database
     *               to be considered a "pattern".
     * @param outFile The file to write the patterns to.
     */
    public void run(int[][] seqDb, int minSup, int minLen, @Nonnull File outFile){

        //set-up file writing
        final SequentialPatternWriter writer = new SequentialPatternWriter(outFile);
        //setup pattern processor
        PatternProcessor writeToFileProcessor = new PatternProcessor() {
            @Override
            void process(CandidatePattern pattern) {
                writer.write(pattern.toSequentialPattern());
            }
        };
        //run the actual algo
        log.info("Extracting sequence graphs.");
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(seqDb);
        log.info("Mining patterns.");
        for (SequenceGraph g : graphs) {
            run(g, seqDb, minSup, minLen, writeToFileProcessor);
        }
        writer.close();
    }


    /**
     * Mines the patterns and stores them in memory.
     * @param seqDb The sequence database to mine.
     * @param minSup The minimum required support to be a pattern.
     * @param minLen The minimum length a pattern can be.
     * @return The patterns that were discovered.
     */
    public Collection<CandidatePattern> run(int[][] seqDb, int minSup, int minLen){

        final ArrayList<CandidatePattern> patterns = new ArrayList<>();

        PatternProcessor storeInMemoryProcessor = new PatternProcessor() {
            @Override
            void process(CandidatePattern pattern) {
                patterns.add(pattern);
            }
        };

        log.info("Extracting sequence graphs.");
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(seqDb);
        log.info("Mining patterns.");
        for (SequenceGraph g : graphs) {
            run(g, seqDb, minSup, minLen, storeInMemoryProcessor);
        }
        return patterns;
    }

    private void refineSequence(List<CoveredSequence> sequences, BitSet processedEdges){

        //refine by removing any processed edges from existing sequences

        ArrayList<CoveredSequence> newSequences = new ArrayList<>();

        Iterator<CoveredSequence> iter = sequences.iterator();
        while(iter.hasNext()){
            CoveredSequence curSeq = iter.next();
            int lastGoodIdx = -1;
            List<SequenceEdge> sequence = curSeq.getSequence();
            int seqSize = sequence.size();
            boolean splitSequence = false;
            for (int i = 0; i < seqSize; i++) {
                SequenceEdge curEdge = sequence.get(i);
                boolean edgeProcessed = processedEdges.get(curEdge.id);
                //the current edge is already processed, try to make a sub-sequence
                if ( edgeProcessed && lastGoodIdx != -1){
                    newSequences.add(new CoveredSequence(new ArrayList<>(sequence.subList(lastGoodIdx, i))));
                    lastGoodIdx = -1;
                    splitSequence = true;
                }
                //the current edge is okay, but we have no reference for a good edge, so use this one
                else if(!edgeProcessed && lastGoodIdx == -1){
                    lastGoodIdx = i;
                }
                //last index, the edge is okay, but the last good edge is not the start of the sequence
                if(!edgeProcessed && lastGoodIdx > 0 && i == seqSize - 1){
                    newSequences.add(new CoveredSequence(new ArrayList<>(sequence.subList(lastGoodIdx, i+1))));
                    splitSequence = true;
                }
            }
            //we split the sequence, so remove the original
            if(splitSequence || lastGoodIdx == -1){
                iter.remove();
            }
        }

        if(!newSequences.isEmpty()){
            sequences.addAll(newSequences);
        }
    }

    private CandidatePattern getNextPattern(List<CoveredSequence> sequences, int minSup, int minLen){

        int bestCover = 0;
        final ArrayList<CandidatePattern> tiedPatterns = new ArrayList<>();

        for (int i = sequences.size() - 1; i >= 0; i--) {
            CoveredSequence candidate = sequences.get(i);
            //skip over candidate if we already have better cover pattern found
            if(candidate.getCover() < bestCover){
                continue;
            }

            Collection<CandidatePattern> patterns = getMostCoveredSubseq(candidate.getSequence(), minSup, minLen);
            if(patterns == null){
                continue;
            }

            //get cover scores from each new pattern
            for (CandidatePattern pattern : patterns) {
                int patternCover = pattern.getCover();
                if(patternCover > bestCover){
                    bestCover = pattern.getCover();
                    tiedPatterns.clear();
                    tiedPatterns.add(pattern);
                }else if(patternCover == bestCover){
                    tiedPatterns.add(pattern);
                }
            }

            //check whether we have a pattern with sufficient cover yet
            if(i > 0){
                int coverToBeat = sequences.get(i-1).getCover();
                if(bestCover > coverToBeat){
                    //we have a pattern with better cover, stop searching
                    //return pickMostCovered(tiedPatterns);
                }
            }
        }
        //todo: fix or delete
        return null;
        //pickMostCovered(tiedPatterns);
    }

    private List<CoveredSequence> toPatterns(SequenceGraph g, int[][] seqDb, int minSup, int minLen){

        ArrayList<CoveredSequence> coveredCandidates = new ArrayList<>(seqDb.length);

        for (int[] sequence : seqDb) {
            //skip sequence shorter than this
            if(sequence.length < minLen){
                continue;
            }

            SequenceNode prevNode = null;
            ArrayList<SequenceEdge> sequenceEdges = new ArrayList<>();

            for (int nodeId : sequence) {
                SequenceNode curNode = g.nodes.get(nodeId);
                if (curNode != null && prevNode != null) {
                    SequenceEdge curEdge = prevNode.getOutEdge(nodeId);
                    boolean edgeSupported = curEdge.getSupport() >= minSup;
                    if(!edgeSupported && !sequenceEdges.isEmpty()){
                        coveredCandidates.add(new CoveredSequence(sequenceEdges));
                        sequenceEdges = new ArrayList<>();
                    }else if(edgeSupported){
                        sequenceEdges.add(curEdge);
                    }
                } else if (curNode == null && !sequenceEdges.isEmpty()) {
                    coveredCandidates.add(new CoveredSequence(sequenceEdges));
                    sequenceEdges = new ArrayList<>();
                }
                prevNode = curNode;
            }
            if(!sequenceEdges.isEmpty()){
                coveredCandidates.add(new CoveredSequence(sequenceEdges));
            }
        }
        return coveredCandidates;
    }

//    private CandidatePattern pickMostCovered(Collection<CandidatePattern> candidatePatterns){
//
//        if(candidatePatterns.isEmpty()){
//            return null;
//        }
//        if(candidatePatterns.size() == 1){
//            return candidatePatterns.iterator().next();
//        }
//
//        int cover = 0;
//        int sup = 0;
//        int length = 0;
//
//        Set<CandidatePattern> bestCandidates = new HashSet<>();
//
//        //compare cover, then support, then length, then first id
//        for (CandidatePattern candidatePattern : candidatePatterns) {
//            int curCover = candidatePattern.getCover();
//            int curSup = candidatePattern.sequenceVisits.getSupport();
//            int curLength = candidatePattern.size();
//            boolean replacePattern = false;
//            if(curCover > cover){
//                replacePattern = true;
//            }
//            //now check support
//            else if(curCover == cover && curSup > sup){
//                replacePattern = true;
//            }
//            //now check length
//            else if(curCover == cover && curSup == sup && curLength > length){
//                replacePattern = true;
//            }
//            //we have an equal contender for best pattern
//            else if(curCover == cover && curSup == sup && curLength == length
//                    && !bestCandidates.isEmpty()){
//                bestCandidates.add(candidatePattern);
//            }
//
//            if(replacePattern){
//                bestCandidates.clear();
//                bestCandidates.add(candidatePattern);
//                cover = curCover;
//                sup = curSup;
//                length = curLength;
//            }
//        }
//
//        if(bestCandidates.size() == 1){
//            return bestCandidates.iterator().next();
//        }
//        //have to cull set down to one choice
//        else{
//
//            //check for case all length-1
//            boolean allLength1 = true;
//            for (CandidatePattern bestCandidate : bestCandidates) {
//                allLength1 = bestCandidate.size() == 1;
//                if(!allLength1){
//                    break;
//                }
//            }
//
//            if(allLength1){
//                return bestCandidates.iterator().next();
//            }
//
//            //remove head/tail (whichever is less covered)
//            HashSet<CandidatePattern> toCull = new HashSet<>();
//            final EdgeCoverComparator comparator = new EdgeCoverComparator();
//            for (CandidatePattern candidatePattern : bestCandidates) {
//                SequenceEdge head = candidatePattern.get(0);
//                SequenceEdge tail = candidatePattern.get(candidatePattern.size()-1);
//                boolean removeHead = comparator.compare(head, tail) < 0;
//                if(removeHead){
//                    candidatePattern.remove(0);
//                }else{
//                    candidatePattern.remove(candidatePattern.size()-1);
//                }
//
//                //rebuild candidate pattern with one edge removed
//                Iterator<SequenceEdge> edges = candidatePattern.getSequence().iterator();
//                SequenceEdge firstEdge = edges.next();
//                Visitations sequenceVisits = new Visitations(firstEdge.getVisitors());
//                while(edges.hasNext()){
//                    SequenceEdge curEdge = edges.next();
//                    sequenceVisits = Visitations.tryConnect(sequenceVisits, curEdge.getVisitors(), 1, 0);
//                }
//                toCull.add(new CandidatePattern(candidatePattern.getSequence(), sequenceVisits));
//            }
//            return pickMostCovered(toCull);
//        }
//    }

    private Collection<CandidatePattern> getMostCoveredSubseq(List<SequenceEdge> sequenceEdges, int minSup, int minLen){

        int bestCover = 0;
        Set<CandidatePattern> fullyExpanded = new HashSet<>();

        final Map<Range, CandidatePattern> toGrow = new HashMap<>();
        final int lastIdx = sequenceEdges.size()-1;
        //add all length-1 patterns
        for (int i = 0; i < sequenceEdges.size(); i++) {
            SequenceEdge edge = sequenceEdges.get(i);
            ArrayList<SequenceEdge> singleEdfe = new ArrayList<>();
            singleEdfe.add(edge);
            toGrow.put(new Range(i,i), new CandidatePattern(singleEdfe, edge.getVisitors()));
        }

        while(!toGrow.isEmpty()){
            //get an entry to grow
            Range rangeToGrow;
            CandidatePattern patternToGrow;
            {
                Iterator<Map.Entry<Range, CandidatePattern>> iter = toGrow.entrySet().iterator();
                Map.Entry<Range, CandidatePattern> entryToGrow = iter.next();
                rangeToGrow = entryToGrow.getKey();
                patternToGrow = entryToGrow.getValue();
                iter.remove();
            }
            boolean grewPattern = false;
            //expand both sides of the range if possible
            for (int i = 0; i < 2; i++) {
                boolean growHead = i == 0;
                int edgeIdx = (growHead) ? rangeToGrow.lowerBound - 1 : rangeToGrow.upperBound + 1;
                //make sure edge is within bounds
                if(edgeIdx < 0 || edgeIdx > lastIdx){
                    continue;
                }
                SequenceEdge expansionEdge = sequenceEdges.get(edgeIdx);
                Visitations expandedVisits = growHead ?
                        Visitations.tryConnect(expansionEdge.getVisitors(), patternToGrow.sequenceVisits, 1, minSup) :
                        Visitations.tryConnect(patternToGrow.sequenceVisits, expansionEdge.getVisitors(), 1, minSup);
                //min support must be maintained when adding a new edge
                if(expandedVisits.getSupport() < minSup){
                    continue;
                }
                CandidatePattern extended = CandidatePattern.extend(patternToGrow, expansionEdge, expandedVisits, growHead);
                Range extendedRange = growHead ? new Range(edgeIdx, rangeToGrow.upperBound) : new Range(rangeToGrow.lowerBound, edgeIdx);
                toGrow.put(extendedRange, extended);
                grewPattern = true;
            }
            //was unable to grow the pattern any further, it is time to
            //pick the pattern and see whether it meets our criteria
            if(!grewPattern){
                int patternCover = patternToGrow.getCover();
                if(patternCover > bestCover){
                    fullyExpanded.clear();
                    bestCover = patternCover;
                    fullyExpanded.add(patternToGrow);
                }
                else if(patternCover == bestCover){
                    fullyExpanded.add(patternToGrow);
                }
            }
        }

        if(fullyExpanded.isEmpty()){
            return null;
        }

        return fullyExpanded;
    }


    private abstract class PatternProcessor{
        abstract void process(CandidatePattern pattern);
    }

}
