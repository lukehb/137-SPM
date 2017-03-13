package onethreeseven.spm.algorithm;

import onethreeseven.collections.Range;
import onethreeseven.spm.data.SPMFParser;

import java.io.File;
import java.util.HashSet;

/**
 * Calculates redundancy from a sequence database as the number
 * of repeated pairs.
 * @author Luke Bermingham
 */
public class RedundancyCalculator {

    public double run(File spmfFile){
        return run(new SPMFParser().parseSequences(spmfFile));
    }

    /**
     * Using an array of sequences find the percentage of repeated pairs.
     * @param sequences The sequences to analyse.
     * @return The percentage of redundant pairs in the sequences.
     */
    public double run(int[][] sequences){

        HashSet<Range> pairs = new HashSet<>();
        int totalPairs = 0;
        int redundantPairs = 0;

        for (int[] sequence : sequences) {
            int lastIdx = sequence.length - 1;
            for (int j = 0; j < lastIdx; j++) {
                int itemA = sequence[j];
                int itemB = sequence[j+1];
                Range r = new Range(itemA, itemB);
                boolean newPair = pairs.add(r);
                if(!newPair){
                    redundantPairs++;
                }
                totalPairs++;
            }
        }

        return redundantPairs/(double)totalPairs;

    }

}
