package onethreeseven.spm.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link ContiguousSubSeqIterator}
 * @author Luke Bermingham
 */
public class ContiguousSubSeqIteratorTest {

    @Test
    public void testSubSize2() throws Exception {
        int subSize = 2;
        ContiguousSubSeqIterator iter = new ContiguousSubSeqIterator(subSize, new int[]{1,2,3,4,5});
        Assert.assertArrayEquals(new int[]{1,2}, iter.next());
        Assert.assertArrayEquals(new int[]{2,3}, iter.next());
        Assert.assertArrayEquals(new int[]{3,4}, iter.next());
        Assert.assertArrayEquals(new int[]{4,5}, iter.next());
        Assert.assertTrue(!iter.hasNext());
    }

    @Test
    public void testSubSize5() throws Exception {
        int subSize = 5;
        ContiguousSubSeqIterator iter = new ContiguousSubSeqIterator(subSize, new int[]{1,2,3,4,5});
        Assert.assertArrayEquals(new int[]{1,2,3,4,5}, iter.next());
        Assert.assertTrue(!iter.hasNext());
    }

}