package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.Trie;
import onethreeseven.spm.model.TrieIterator;
import onethreeseven.spm.model.ContiguousSubSeqIterator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * CCSpan: Mining closed contiguous sequential patterns.
 * By Jingsong Zhang, Yinglin Wang and Dingyu Yang.
 * This is our implementation of their algorithm, we use Trie
 * to speed up the pattern searching.
 * @author Luke Bermingham
 */
public class CCSpan {

    /**
     * Run CCSpan and write patterns to a file.
     * @param sequences The sequence database
     * @param minSupRelative The relative support 0...1
     * @param outputSubPatterns Set to true to get all contiguous patterns (not just closed).
     * @param outputFile The file to write to.
     */
    public void run(int[][] sequences, float minSupRelative, boolean outputSubPatterns, File outputFile){
        final Trie<Integer> patterns = run(sequences, minSupRelative);
        TrieIterator<Integer> iter = patterns.getPatternIterator(outputSubPatterns);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
            final StringBuilder sb = new StringBuilder();
            while(iter.hasNext()){
                ArrayList<Integer> pattern = iter.next();
                for (Integer symbol : pattern) {
                    sb.append(symbol).append(" ");
                }
                sb.append("#SUP:").append(iter.getCount());
                if(iter.isMarked()){
                    sb.append(" #CLOSED");
                }
                else{
                    sb.append(" #OPEN");
                }
                bw.write(sb.toString());
                bw.newLine();
                sb.setLength(0);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Trie<Integer> run(int[][] sequences, float minSupRelative){
        if(sequences.length == 0){
            throw new IllegalArgumentException(
                    "Cannot mine patterns from empty sequence database.");
        }
        if(minSupRelative < 0 || minSupRelative > 1){
            throw new IllegalArgumentException("Support must be in the range [0,1]");
        }
        return runImpl(sequences, minSupRelative);
    }

    /**
     * In the paper this section is referred to as Algorithm 1.
     * @param db the sequences to process.
     * @param support the support of a contiguous sequential pattern.
     */
    private Trie<Integer> runImpl(int[][] db, float support){
        final int minSup = Math.round(db.length * support);

        int k = 1;
        Trie<Integer> f = new Trie<>();
        //This loop is lines 1-10 in paper - keep generating closed patterns until you can't
        while(addLengthKPatterns(f, k, minSup, db) > 0){
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
     * @return the number of length-k potential contiguous sub-sequences added.
     */
    private int addLengthKPatterns(Trie<Integer> f, int k, int minSup, int[][] db){

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
            if(f.supersede(candidate, minSup)){
                potentialPatterns++;
            }
        }

        return potentialPatterns;
    }



}
