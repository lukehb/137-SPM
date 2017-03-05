package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;
import onethreeseven.spm.model.SequentialPattern;
import onethreeseven.spm.model.Trie;
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
        AbstractContigousSPM algo = new AbstractContigousSPM() {
            @Override
            protected IPatternClosure getPatternClosure() {
                return (supA, supB) -> true;
            }

            @Override
            protected boolean addToOutput(ArrayList<Integer> pattern, int support, boolean marked) {
                return false;
            }

            @Override
            protected String getPatternClosureSuffix() {
                return null;
            }
        };

        Trie<Integer> t = algo.populateTrie(db, 0.5f);

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
        final List<SequentialPattern> actualPatterns = algo.run(db, 0.5f);
        final List<SequentialPattern> expectedPatterns = new ArrayList<>();

        expectedPatterns.add(new SequentialPattern(new int[]{3,1}, 3));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{2,3}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2,3}, 3));

        List<SequentialPattern> actual = algo.run(db, 0.5f);

        for (SequentialPattern actualPattern : actual) {
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
        final List<SequentialPattern> actualPatterns = algo.run(db, 0.5f);
        final List<SequentialPattern> expectedPatterns = new ArrayList<>();

        expectedPatterns.add(new SequentialPattern(new int[]{3,1}, 3));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2,3}, 3));

        List<SequentialPattern> actual = algo.run(db, 0.5f);

        for (SequentialPattern actualPattern : actual) {
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
        final List<SequentialPattern> actualPatterns = algo.run(db, 0.5f);
        final List<SequentialPattern> expectedPatterns = new ArrayList<>();

        expectedPatterns.add(new SequentialPattern(new int[]{3}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{1}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{2}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{3,1}, 3));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{2,3}, 4));
        expectedPatterns.add(new SequentialPattern(new int[]{1,2,3}, 3));

        List<SequentialPattern> actual = algo.run(db, 0.5f);

        for (SequentialPattern actualPattern : actual) {
            System.out.println(actualPattern.toString());
            Assert.assertTrue(expectedPatterns.contains(actualPattern));
        }

        //now check expected as well if case one actual is missing
        for (SequentialPattern expectedPattern : expectedPatterns) {
            Assert.assertTrue(actualPatterns.contains(expectedPattern));
        }
    }

}