package onethreeseven.spm.algorithm;

import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.ContiguousSubSeqIterator;
import onethreeseven.spm.model.Trie;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Given two pattern outputs, A and B, where A is a subset of B
 * find the percentage of B that intersects with A.
 * @author Luke Bermingham
 */
public class PatternContainmentCalculator {

    private static final Logger log = Logger.getLogger(PatternContainmentCalculator.class.getSimpleName());

    /**
     *
     * @param subsetPatterns A is a subset of B.
     * @param supersetPatterns B is the superset of A.
     * @return The percentage of B that intersects with A.
     */
    public double run(File subsetPatterns, File supersetPatterns){

        log.info("Loading subset patterns.");
        //build trie of subset patterns
        int[][] subset = new SPMFParser().parseSequences(subsetPatterns);
        log.info("Building trie from subset patterns.");
        Trie<Integer> t = new Trie<>();
        //populate trie with sequences from the subset pattern output
        for (int[] sequence : subset) {
            int k = sequence.length;
            while(k > 0){
                ContiguousSubSeqIterator iter = new ContiguousSubSeqIterator(k, sequence);
                while(iter.hasNext()){
                    t.add(iter.nextBoxed());
                }
                k--;
            }
        }
        subset = null;

        //check how many patterns in superset are contained in the trie
        log.info("Loading superset patterns.");
        int[][] superset = new SPMFParser().parseSequences(supersetPatterns);
        int nContained = 0;
        log.info("Testing superset patterns for containment in the subset trie.");
        for (int[] sequence : superset) {
            Integer[] pattern = Arrays.stream(sequence).boxed().toArray(Integer[]::new);
            if(t.getFrequencyOf(pattern) > 0){
                nContained++;
            }
        }

        return (double)nContained/superset.length;
    }

}
