package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.Trie;
import onethreeseven.spm.model.TrieIterator;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

/**
 * Test for {@link CCSpan}
 * @author Luke Bermingham
 */
public class CCSpanTest {

    @Test
    public void testFindPatterns(){

        //Example from: CCSpan: Mining closed contiguous sequential patterns.
        //Table 1 & 2.

        int[][] db = new int[][]{
            new int[]{3,1,1,2,3},
            new int[]{1,2,3,2},
            new int[]{3,1,2,3},
            new int[]{1,2,2,3,1},
        };

        CCSpan algo = new CCSpan();
        Trie<Integer> t = algo.run(db, 0.5f);

        Assert.assertTrue(t.getFrequencyOf(new Integer[]{3}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{1}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{2}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{3,1}) == 3);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{1,2}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{2,3}) == 4);
        Assert.assertTrue(t.getFrequencyOf(new Integer[]{1,2,3}) == 3);

        List<Map.Entry<List<Integer>, Boolean>> expected = new ArrayList<>();
        expected.add(new AbstractMap.SimpleEntry<>(Collections.singletonList(3), true)); //errata in paper, this is closed
        expected.add(new AbstractMap.SimpleEntry<>(Collections.singletonList(1), false));
        expected.add(new AbstractMap.SimpleEntry<>(Collections.singletonList(2), false));
        expected.add(new AbstractMap.SimpleEntry<>(Arrays.asList(3,1), true));
        expected.add(new AbstractMap.SimpleEntry<>(Arrays.asList(1,2), true));
        expected.add(new AbstractMap.SimpleEntry<>(Arrays.asList(2,3), true));
        expected.add(new AbstractMap.SimpleEntry<>(Arrays.asList(1,2,3), true));

        TrieIterator<Integer> patternIter = t.getPatternIterator(true);
        while(patternIter.hasNext()){
            List<Integer> pattern = patternIter.next();
            System.out.println((patternIter.isMarked() ? "|closed" : "|opened")
                    + "|sup:" + patternIter.getCount()
                    + "| " +  Arrays.toString(pattern.toArray()));

            Assert.assertTrue(expected.contains(new AbstractMap.SimpleEntry<>(pattern, patternIter.isMarked())));
        }

    }

}