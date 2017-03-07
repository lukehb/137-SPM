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
        nDistinctItems = distinctItems.size();
        avgSequenceLength = totalItems/(double)totalSequences;
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

    public void printStats(){
        //output stats
        System.out.println("#Sequences: " + totalSequences);
        System.out.println("#Items: " + totalItems);
        System.out.println("Avg sequence length: " + avgSequenceLength);
        System.out.println("#Distinct items: " + nDistinctItems);
    }

}
