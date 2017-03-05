package onethreeseven.spm.algorithm;

import onethreeseven.collections.Range;
import onethreeseven.spm.model.*;
import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Using a sequence graph this algorithm mines a set of contiguous, non-overlapping, sequential patterns.
 * Each pattern it mines tries to maximise pattern cover whilst maintaining a minimum specified support.
 * @author Luke Bermingham
 */
public class ProtoMiner {

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
     * @param g The sequence graph to use.
     * @param seqDb The sequence database to mine.
     * @param minSup The minimum number of times that a contiguous sub-sequence must occur in the database
     *               to be considered a "pattern".
     * @param outFile The file to write the patterns to.
     */
    public void run(SequenceGraph g, int[][] seqDb, int minSup, int minLen, @Nonnull File outFile){

        //set-up file writing
        final BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(outFile));

            PatternProcessor writeToFileProcessor = new PatternProcessor() {
                @Override
                void process(CandidatePattern pattern) {
                    try {
                        bw.write(pattern.toString());
                        bw.newLine();
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            //populateTrie the actual algorithm
            run(g, seqDb, minSup, minLen, writeToFileProcessor);

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Mines the patterns and stores them in memory.
     * @param g The sequence graph to use.
     * @param seqDb The sequence database to mine.
     * @param minSup The minimum required support to be a pattern.
     * @return The patterns that were discovered.
     */
    public Collection<CandidatePattern> run(SequenceGraph g, int[][] seqDb, int minSup, int minLen){

        final ArrayList<CandidatePattern> patterns = new ArrayList<>();

        PatternProcessor storeInMemoryProcessor = new PatternProcessor() {
            @Override
            void process(CandidatePattern pattern) {
                patterns.add(pattern);
            }
        };

        run(g, seqDb, minSup, minLen, storeInMemoryProcessor);

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
        final ArrayList<CoveredSequence> subSequences = new ArrayList<>();

        Iterator<CoveredSequence> iter = sequences.iterator();

        while(iter.hasNext()){
            CoveredSequence candidate = sequences.remove(sequences.size()-1);
            List<CoveredSequence> patterns = getLargestSubPatterns(candidate.getSequence(), minSup, minLen);
            if(patterns == null){
                continue;
            }

            //get cover scores from each new pattern
            for (CoveredSequence pattern : patterns) {
                if(pattern instanceof CandidatePattern){
                    int curCover = pattern.getCover();
                    if(curCover > bestCover){
                        bestCover = curCover;
                        tiedPatterns.clear();
                        tiedPatterns.add((CandidatePattern) pattern);
                    }else if(curCover == bestCover){
                        tiedPatterns.add((CandidatePattern) pattern);
                    }
                }
            }
            subSequences.addAll(patterns);

            //check whether we have a pattern with sufficient cover yet
            if(!sequences.isEmpty()){
                int coverToBeat = sequences.get(sequences.size()-1).getCover();
                if(bestCover > coverToBeat){
                    break;
                }
            }
        }
        sequences.addAll(subSequences);

        return pickMostCovered(tiedPatterns);
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

    private CandidatePattern pickMostCovered(Collection<CandidatePattern> candidatePatterns){

        if(candidatePatterns.isEmpty()){
            return null;
        }
        if(candidatePatterns.size() == 1){
            return candidatePatterns.iterator().next();
        }

        int cover = 0;
        int sup = 0;
        int length = 0;

        Set<CandidatePattern> bestCandidates = new HashSet<>();

        //compare cover, then support, then length, then first id
        for (CandidatePattern candidatePattern : candidatePatterns) {
            int curCover = candidatePattern.getCover();
            int curSup = candidatePattern.sequenceVisits.getSupport();
            int curLength = candidatePattern.size();
            boolean replacePattern = false;
            if(curCover > cover){
                replacePattern = true;
            }
            //now check support
            else if(curCover == cover && curSup > sup){
                replacePattern = true;
            }
            //now check length
            else if(curCover == cover && curSup == sup && curLength > length){
                replacePattern = true;
            }
            //we have an equal contender for best pattern
            else if(curCover == cover && curSup == sup && curLength == length
                    && !bestCandidates.isEmpty()){
                bestCandidates.add(candidatePattern);
            }

            if(replacePattern){
                bestCandidates.clear();
                bestCandidates.add(candidatePattern);
                cover = curCover;
                sup = curSup;
                length = curLength;
            }
        }

        if(bestCandidates.size() == 1){
            return bestCandidates.iterator().next();
        }
        //have to cull set down to one choice
        else{

            //check for case all length-1
            boolean allLength1 = true;
            for (CandidatePattern bestCandidate : bestCandidates) {
                allLength1 = bestCandidate.size() == 1;
                if(!allLength1){
                    break;
                }
            }

            if(allLength1){
                return bestCandidates.iterator().next();
            }

            //remove head/tail (whichever is less covered)
            HashSet<CandidatePattern> toCull = new HashSet<>();
            final EdgeCoverComparator comparator = new EdgeCoverComparator();
            for (CandidatePattern candidatePattern : bestCandidates) {
                SequenceEdge head = candidatePattern.get(0);
                SequenceEdge tail = candidatePattern.get(candidatePattern.size()-1);
                boolean removeHead = comparator.compare(head, tail) < 0;
                if(removeHead){
                    candidatePattern.remove(0);
                }else{
                    candidatePattern.remove(candidatePattern.size()-1);
                }

                //rebuild candidate pattern with one edge removed
                Iterator<SequenceEdge> edges = candidatePattern.getSequence().iterator();
                SequenceEdge firstEdge = edges.next();
                Visitations sequenceVisits = new Visitations(firstEdge.getVisitors());
                while(edges.hasNext()){
                    SequenceEdge curEdge = edges.next();
                    sequenceVisits = Visitations.tryConnect(sequenceVisits, curEdge.getVisitors(), 1, 0);
                }
                toCull.add(new CandidatePattern(candidatePattern.getSequence(), sequenceVisits));
            }
            return pickMostCovered(toCull);
        }
    }

    private ArrayList<CoveredSequence> getLargestSubPatterns(List<SequenceEdge> sequenceEdges, int minSup, int minLen){
        //start with a window size equal to the size of the collection and shrink by 1 each time
        final int lastIdx = sequenceEdges.size() - 1;
        int windowSize = sequenceEdges.size();
        final ArrayList<CoveredSequence> patterns = new ArrayList<>(1);
        final int smallestWindow = Math.max(0,minLen-1);

        //store the indices that did not become candidate patterns this time
        ArrayList<Range> otherIndices = new ArrayList<>();
        otherIndices.add(new Range(0, lastIdx));

        while(patterns.isEmpty() && windowSize >= smallestWindow){

            int subStartIdx = 0;
            int subEndIdx = (subStartIdx + windowSize);

            while( (subEndIdx-1) <= lastIdx){

                //build up pattern by appending visitors so long as a minimum support is maintained
                Visitations sequenceVisits = null;
                boolean storeSequence = true;

                for (int i = subStartIdx; i < subEndIdx; i++) {
                    SequenceEdge curEdge = sequenceEdges.get(i);
                    if(sequenceVisits == null){
                        sequenceVisits = sequenceEdges.get(i).getVisitors();
                    }else{
                        Visitations potentialVisits = Visitations.tryConnect(sequenceVisits, curEdge.getVisitors(), 1, minSup);
                        if(potentialVisits.getSupport() < minSup){
                            storeSequence = false;
                            break;
                        }else{
                            sequenceVisits = potentialVisits;
                        }
                    }
                }

                //store pattern and update other indices
                if(storeSequence && sequenceVisits != null){
                    patterns.add(new CandidatePattern(new ArrayList<>(sequenceEdges.subList(subStartIdx, subEndIdx)), sequenceVisits));
                    //build up a list of indices remaining after this pattern is removed
                    ArrayList<Range> complementRanges = new ArrayList<>();
                    final Range patternRange = new Range(subStartIdx, subEndIdx-1);
                    for (Range untouchedRange : otherIndices) {
                        Range[] complement = untouchedRange.minus(patternRange);
                        if(complement == null){
                            complementRanges.add(untouchedRange);
                        }else{
                            for (Range range : complement) {
                                if(range.getRange() >= Math.max(1, minLen-1)){
                                    complementRanges.add(range);
                                }
                            }
                        }
                    }
                    otherIndices = complementRanges;
                }

                //slide the window along
                subStartIdx++;
                subEndIdx++;
            }
            //decrease window size, and try to make smaller patterns
            windowSize--;
        }

        if(patterns.isEmpty()){
            return null;
        }

        //update patterns using the untouched ranges
        for (Range otherRange : otherIndices) {
            patterns.add(new CoveredSequence(
                    new ArrayList<>(sequenceEdges.subList(otherRange.lowerBound, otherRange.upperBound))));
        }

        return patterns;
    }

    private abstract class PatternProcessor{
        abstract void process(CandidatePattern pattern);
    }

}
