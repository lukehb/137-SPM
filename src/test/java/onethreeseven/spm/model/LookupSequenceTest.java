package onethreeseven.spm.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link LookupSequence}.
 * @author Luke Bermingham
 */
public class LookupSequenceTest {

    @Test
    public void testClearAndGet() throws Exception {
        LookupSequence ls = new LookupSequence(new int[]{1,3,7,1,3,7,1,3,7});
        ls.clear(3);
        int[] expected = new int[]{1,7,1,7,1,7};
        Assert.assertArrayEquals(expected, ls.getActiveSequence());
    }

    @Test
    public void testClearContiguousSubSequence() throws Exception {
        LookupSequence ls = new LookupSequence(new int[]{1,3,7,1,3,7,1,3,7});
        int[] toClear = new int[]{1,3,7};
        Assert.assertTrue(ls.clear(toClear));
        int[] result1 = ls.getActiveSequence();
        int[] expected = new int[]{1,3,7,1,3,7};
        Assert.assertArrayEquals(expected, result1);
        while(true){
            if(!ls.clear(toClear)){
                break;
            }
        }
        Assert.assertTrue(ls.getActiveSequence().length == 0);
    }

}