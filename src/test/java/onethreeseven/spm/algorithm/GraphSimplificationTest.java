package onethreeseven.spm.algorithm;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test our {@link GraphSimplification} algorithms.
 * @author Luke Bermingham
 */
public class GraphSimplificationTest {

    @Test
    public void runLossless() throws Exception {

        final int minSup = 3;

        int[][] sdb = new int[][]{
                new int[]{1,2,3,4,5,6},
                new int[]{1,1,2,6,5,3},
                new int[]{6,1,2,3,3}
        };

        GraphSimplification algo = new GraphSimplification();
        int[][] simplifiedDb = algo.runLossless(sdb, minSup);

        Assert.assertTrue(simplifiedDb.length == sdb.length);
        Assert.assertArrayEquals(new int[]{1,2,3,6}, simplifiedDb[0]);
        Assert.assertArrayEquals(new int[]{1,1,2,6,3}, simplifiedDb[1]);
        Assert.assertArrayEquals(new int[]{6,1,2,3,3}, simplifiedDb[2]);

    }



}