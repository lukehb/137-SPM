package onethreeseven.spm.algorithm;

import onethreeseven.collections.Range;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.SequentialPattern;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

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
        AtomicInteger totalPairs = new AtomicInteger(0);
        AtomicInteger redundantPairs = new AtomicInteger(0);

        for (int[] sequence : sequences) {
            countPairs(sequence, totalPairs, redundantPairs, pairs);
        }

        return redundantPairs.get()/(double)totalPairs.get();

    }


    public double run(Collection<SequentialPattern> patterns){
        HashSet<Range> pairs = new HashSet<>();
        AtomicInteger totalPairs = new AtomicInteger(0);
        AtomicInteger redundantPairs = new AtomicInteger(0);

        for (SequentialPattern pattern : patterns) {
            countPairs(pattern.getSequence(), totalPairs, redundantPairs, pairs);
        }

        return redundantPairs.get()/(double)totalPairs.get();
    }

    private void countPairs(int[] sequence,
                            AtomicInteger totalPairs,
                            AtomicInteger redundantPairs,
                            HashSet<Range> processedPairs){
        int lastIdx = sequence.length - 1;
        for (int j = 0; j < lastIdx; j++) {
            int itemA = sequence[j];
            int itemB = sequence[j+1];
            Range r = new Range(itemA, itemB);
            boolean newPair = processedPairs.add(r);
            if(!newPair){
                redundantPairs.incrementAndGet();
            }
            totalPairs.incrementAndGet();
        }
    }

}
