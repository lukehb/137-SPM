package onethreeseven.spm.algorithm;

import onethreeseven.spm.data.SequentialPatternWriter;
import onethreeseven.spm.model.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The basis for contiguous sequential pattern mining
 * using a {@link onethreeseven.spm.model.Trie} to do support
 * and closure checking.
 *
 * Skeleton of the algorithm based on:
 * "CCSpan: Mining closed contiguous sequential patterns."
 *
 * @author Luke Bermingham
 */
public abstract class AbstractContiguousSPM {

    /////////////////
    //INTERNAL METHODS
    /////////////////

    /**
     * In the paper this section is referred to as Algorithm 1.
     * @param db the sequences to process.
     * @param minSupAbs the absolute minimum support of a contiguous sequential pattern.
     */
    private Trie<Integer> runImpl(int[][] db, int minSupAbs){
        final Trie<Integer> f = new Trie<>();
        final IPatternClosure patternClosure = getPatternClosure();

        int k = 1;
        //This loop is lines 1-10 in paper - keep generating closed patterns until you can't
        while(addLengthKPatterns(f, k, minSupAbs, db, patternClosure) > 0){
            k++;
        }
        return f;
    }

    /**
     * Given sequences find all 1-patterns that meet the minimum support.
     * @param f The Trie used to store/check support of the patterns.
     * @param k The size of the patterns that should be generated from the sequences.
     * @param minSup The minimum support.
     * @param db The sequences to split.
     * @param patternClosure The pattern closure to use.
     * @return the number of length-k potential contiguous sub-sequences added.
     */
    private int addLengthKPatterns(Trie<Integer> f, int k, int minSup, int[][] db, IPatternClosure patternClosure){

        ArrayList<Integer[]> candidates = new ArrayList<>();

        for (int[] sequence : db) {
            if(sequence.length < k){
                continue;
            }

            ContiguousSubSeqIterator iter = new ContiguousSubSeqIterator(k, sequence);
            while(iter.hasNext()){
                //add each sub-sequence locked and marked (ensures no repeats from same sequence)
                Integer[] candidate = iter.nextBoxed();

                //check for post-sequence and try to add new candidate if found
                if(k > 1){
                    Integer[] post = new Integer[k-1];
                    System.arraycopy(candidate, 1, post, 0, k-1);
                    if(f.getFrequencyOf(post) > 0){
                        if(f.add(candidate, 1, true, true)){
                            candidates.add(candidate);
                        }
                    }
                }
                //a length-1 pattern, no need for pre-post check
                else{
                    if(f.add(candidate, k, true, true)){
                        candidates.add(candidate);
                    }
                }
            }
            //as patterns are added they are locked so that the same pattern can't be counted twice for
            //the same sequence, however, once a sequence has been processed we unlock the the patterns
            //so that new sequences can increase the count for that pattern
            f.unlockAll();
        }

        //now check that candidates are valid (and closed) using minimum support count
        int potentialPatterns = 0;
        for (Integer[] candidate : candidates) {
            if(f.supersede(candidate, minSup, patternClosure)){
                potentialPatterns++;
            }
        }

        return potentialPatterns;
    }

    //////////////////
    //ABSTRACT METHODS
    //////////////////

    protected abstract IPatternClosure getPatternClosure();
    protected abstract boolean addToOutput(ArrayList<Integer> pattern, TrieIterator<Integer> patternIter);

    //////////////////
    //PUBLIC METHODS
    //////////////////

    public Trie<Integer> populateTrie(int[][] sequences, int minSupAbs){
        if(sequences.length == 0){
            throw new IllegalArgumentException(
                    "Cannot mine patterns from empty sequence database.");
        }
        return runImpl(sequences, minSupAbs);
    }

    /**
     * Run CCSpan and write a list.
     * @param sequences The sequence database
     * @param minSupAbs The minimum absolute support.
     */
    public List<SequentialPattern> run(int[][] sequences, int minSupAbs){
        final Trie<Integer> patterns = populateTrie(sequences, minSupAbs);
        final TrieIterator<Integer> iter = patterns.getPatternIterator(true);
        final List<SequentialPattern> output = new ArrayList<>(sequences.length);

        while(iter.hasNext()){
            ArrayList<Integer> pattern = iter.next();
            int support = iter.getCount();
            if(!addToOutput(pattern, iter)){
                continue;
            }
            output.add(new SequentialPattern(pattern, support));
        }
        return output;
    }

    /**
     * Run CCSpan and write patterns to a file.
     * @param sequences The sequence database
     * @param minSupAbs The absolute minimum support.
     * @param outputFile The file to write to.
     */
    public void run(int[][] sequences, int minSupAbs, File outputFile){
        final Trie<Integer> patterns = populateTrie(sequences, minSupAbs);
        final TrieIterator<Integer> iter = patterns.getPatternIterator(true);
        final SequentialPatternWriter writer = new SequentialPatternWriter(outputFile);

        while(iter.hasNext()){
            ArrayList<Integer> pattern = iter.next();
            int support = iter.getCount();
            if(!addToOutput(pattern, iter)){
                continue;
            }
            writer.write(new SequentialPattern(pattern, support));
        }
        writer.close();
    }

}
