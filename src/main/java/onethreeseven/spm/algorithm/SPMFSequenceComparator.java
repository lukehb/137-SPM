package onethreeseven.spm.algorithm;

import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.Trie;
import java.io.File;
import java.util.Arrays;

/**
 * Compares how many sequences from 'a' are found in 'b'.
 * @author Luke Bermingham
 */
public class SPMFSequenceComparator {

    /**
     * @param spmfA file A
     * @param spmfB file B
     * @return What percentage of sequences in A are found in B.
     */
    public double run(File spmfA, File spmfB){
        return run(new SPMFParser().parse(spmfA, 0), new SPMFParser().parse(spmfB, 0));
    }

    /**
     * @param sdbA sequence database of A
     * @param sdbB sequence database of B
     * @return What percentage of sequences in A are found in B.
     */
    public double run(int[][] sdbA, int[][] sdbB){
        //populate Trie with all patterns from B
        Trie<Integer> trieOfB = makeTrieOf(sdbB);

        //count how many 'A' sequences are found in Trie of 'B'

        int patternsFound = 0;
        int totalPatterns = 0;
        for (int[] sequenceA : sdbA) {
            Integer[] boxed = Arrays.stream(sequenceA).boxed().toArray(Integer[]::new);
            if(trieOfB.getFrequencyOf(boxed) > 0){
                patternsFound++;
            }
            totalPatterns++;
        }
        return (double)patternsFound/totalPatterns;
    }

    private Trie<Integer> makeTrieOf(int[][] sdb){
        Trie<Integer> t = new Trie<>();
        for (int[] seq : sdb) {
            Integer[] boxed = Arrays.stream(seq).boxed().toArray(Integer[]::new);
            t.add(boxed);
        }
        return t;
    }

}
