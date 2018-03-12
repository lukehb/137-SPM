package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;
import onethreeseven.spm.model.SequentialPattern;
import onethreeseven.spm.model.Trie;
import onethreeseven.spm.model.TrieIterator;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

/**
 * Test for {@link CCSpan}
 * @author Luke Bermingham
 */
public class ContiguousPatternMiningTest {

    //Example from: CCSpan: Mining closed contiguous sequential patterns.
    //Table 1 & 2.
    private final static int[][] db = new int[][]{
            new int[]{3,1,1,2,3},
            new int[]{1,2,3,2},
            new int[]{3,1,2,3},
            new int[]{1,2,2,3,1},
    };

    @Test
    public void testTrie(){
        AbstractContiguousSPM algo = new AbstractContiguousSPM() {

            @Override
            public Trie<Integer> populateTrie(int[][] sequences, int minSupAbs) {
                isRunning.set(true);
                return super.populateTrie(sequences, minSupAbs);
            }

            @Override
            public String getSimpleName() {
                return "test";
            }

            @Override
            public String getPatternType() {
                return "test";
            }

            @Override
            protected IPatternClosure getPatternClosure() {
                return (supA, supB) -> true;
            }

            @Override
            protected boolean addToOutput(ArrayList<Integer> pattern, TrieIterator<Integer> patternIter) {
                return false;
            }
        };

        Trie<Integer> t = algo.populateTrie(db, 2);

        Assert.assertTrue(t.getFrequencyOf(new Integer[]{3}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{1}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{2}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{3,1}) == 3);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{1,2}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{2,3}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{1,2,3}) == 3);
    }

    @Test
    public void testFindClosedPatterns(){
        System.out.println("Closed patterns");

        final CCSpan algo = new CCSpan();

        SPMParameters params = new SPMParameters(db, 2);
        final Collection<SequentialPattern> actualPatterns = algo.run(params);

        final List<SequentialPattern> expectedPatterns = new ArrayList<>();

        expectedPatterns.add(new SequentialPattern(new int[]{3,1}, 3));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{2,3}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2,3}, 3));

        for (SequentialPattern actualPattern : actualPatterns) {
            System.out.println(actualPattern.toString());
            Assert.assertTrue(expectedPatterns.contains(actualPattern));
        }

        //now check expected as well if case one actual is missing
        for (SequentialPattern expectedPattern : expectedPatterns) {
            Assert.assertTrue(actualPatterns.contains(expectedPattern));
        }
    }

    @Test
    public void testFindMaxPatterns(){
        System.out.println("Max patterns");

        final MCSpan algo = new MCSpan();

        SPMParameters params = new SPMParameters(db, 2);
        final Collection<SequentialPattern> actualPatterns = algo.run(params);
        final List<SequentialPattern> expectedPatterns = new ArrayList<>();

        expectedPatterns.add(new SequentialPattern(new int[]{3,1}, 3));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2,3}, 3));

        for (SequentialPattern actualPattern : actualPatterns) {
            System.out.println(actualPattern.toString());
            Assert.assertTrue(expectedPatterns.contains(actualPattern));
        }

        //now check expected as well if case one actual is missing
        for (SequentialPattern expectedPattern : expectedPatterns) {
            Assert.assertTrue(actualPatterns.contains(expectedPattern));
        }
    }

    @Test
    public void testFindAllPatterns(){
        System.out.println("All patterns");

        final ACSpan algo = new ACSpan();


        SPMParameters params = new SPMParameters(db, 2);
        final Collection<SequentialPattern> actualPatterns = algo.run(params);
        final List<SequentialPattern> expectedPatterns = new ArrayList<>();

        expectedPatterns.add(new SequentialPattern(new int[]{3}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{1}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{2}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{3,1}, 3));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{2,3}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2,3}, 3));

        for (SequentialPattern actualPattern : actualPatterns) {
            System.out.println(actualPattern.toString());
            Assert.assertTrue(expectedPatterns.contains(actualPattern));
        }

        //now check expected as well if case one actual is missing
        for (SequentialPattern expectedPattern : expectedPatterns) {
            Assert.assertTrue(actualPatterns.contains(expectedPattern));
        }
    }

}