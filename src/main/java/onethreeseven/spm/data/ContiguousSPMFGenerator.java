package onethreeseven.spm.data;

import onethreeseven.collections.IntArray;
import java.io.File;
import java.util.Random;

/**
 * Generates SPMF sequences with contiguous sequential patterns.
 * @author Luke Bermingham
 */
public class ContiguousSPMFGenerator {

    private int nSequences = 100;
    private int sequenceLength = 100;
    private int nDistinctItems = 20;

    public void setnSequences(int nSequences) {
        this.nSequences = nSequences;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public void setnDistinctItems(int nDistinctItems) {
        this.nDistinctItems = nDistinctItems;
    }

    public int[][] generate(File outputFile){
        int[][] sequences = generateSequences();
        SPMFWriter writer = new SPMFWriter();
        writer.write(outputFile, sequences);
        return sequences;
    }

    public int[][] generateSequences(){
        int[][] patterns = generatePatterns();
        int[][] sequences = new int[nSequences][];
        Random rand = new Random();

        for (int i = 0; i < nSequences; i++) {
            IntArray sequence = new IntArray(sequenceLength, false);
            while(sequence.size() < sequenceLength){
                int[] toAdd = patterns[rand.nextInt(patterns.length)];
                for (int item : toAdd) {
                    sequence.add(item);
                    if(sequence.size() == sequenceLength){
                        break;
                    }
                }
            }
            sequences[i] = sequence.getArray();
        }
        return sequences;
    }

    private int[][] generatePatterns(){
        int nPatterns = Math.max(1, Math.round(sequenceLength/2f))+1;
        int[][] patterns = new int[nPatterns][];
        int maxPatternLength = Math.max(1, Math.round(sequenceLength/4f));
        int[] universe = generateItemUniverse();
        Random rand = new Random();

        for (int i = 0; i < patterns.length; i++) {
            int patternLength = rand.nextInt(maxPatternLength+1);
            int[] pattern = new int[patternLength];
            for (int j = 0; j < patternLength; j++) {
                pattern[j] = universe[rand.nextInt(universe.length)];
            }
            patterns[i] = pattern;
        }
        return patterns;
    }

    private int[] generateItemUniverse(){
        int[] items = new int[nDistinctItems];
        for (int i = 0; i < nDistinctItems; i++) {
            items[i] = i+1;
        }
        return items;
    }
}
