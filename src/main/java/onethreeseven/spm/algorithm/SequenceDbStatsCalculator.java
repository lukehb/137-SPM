package onethreeseven.spm.algorithm;

import java.util.BitSet;

/**
 * Calculate statistics for sequence databases in the format int[][].
 * @author Luke Bermingham
 */
public class SequenceDbStatsCalculator {

    private int totalSequences;
    private int totalItems;
    private int nDistinctItems;
    private double avgSequenceLength;
    private double redundancy;

    public void calculate(int[][] seqDb){
        totalItems = 0;
        totalSequences = seqDb.length;

        BitSet distinctItems = new BitSet();

        for (int[] sequence : seqDb) {

            totalItems += sequence.length;

            if(sequence.length == 0){
                continue;
            }

            for (int item : sequence) {
                distinctItems.set(item);
            }
        }
        nDistinctItems = distinctItems.cardinality();
        avgSequenceLength = totalItems/(double)totalSequences;
        redundancy = new RedundancyCalculator().run(seqDb);
    }

    public int getTotalSequences() {
        return totalSequences;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getnDistinctItems() {
        return nDistinctItems;
    }

    public double getAvgSequenceLength() {
        return avgSequenceLength;
    }

    public double getRedundancy() {
        return redundancy;
    }

    public void printStats(){
        System.out.println("#Sequences, #Items, Average Sequence Length, #Distinct items, Redundancy(%)");
        System.out.println(totalSequences + ", " + totalItems + ", "
                + avgSequenceLength + ", " + nDistinctItems + ", " + redundancy);
    }

}
