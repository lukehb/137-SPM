package onethreeseven.spm.algorithm;

import onethreeseven.spm.data.ContiguousSPMFGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link SPMFSequenceComparator}
 * @author Luke Bermingham
 */
public class SPMFSequenceComparatorTest {

    private static final int[][] spmfFile = new ContiguousSPMFGenerator().generateSequences();

    @Test
    public void testContainsAllPatterns() throws Exception {
        SPMFSequenceComparator algo = new SPMFSequenceComparator();
        double percentageMatched = algo.run(spmfFile, spmfFile);
        Assert.assertEquals(1.0, percentageMatched, 1e-7);
    }

    @Test
    public void testContainsMostPatterns() throws Exception {
        SPMFSequenceComparator algo = new SPMFSequenceComparator();

        int[][] a = new int[][]{
                new int[]{1,2,3,4,5},
                new int[]{1,2,3,5},
                new int[]{2,4,4},
                new int[]{2,3,4}
        };
        //missing "2,3,4"
        int[][] b = new int[][]{
                new int[]{1,2,3,4,5},
                new int[]{1,2,3,5},
                new int[]{2,4,4}
        };

        double percentageMatched = algo.run(a, b);
        Assert.assertEquals(0.75, percentageMatched, 1e-7);
    }
}