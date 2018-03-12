package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.SequentialPattern;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collection;

/**
 * Test {@link DCSpan}.
 * @author Luke Bermingham
 */
public class DCSpanTest {

    private static int[][] seqDb = new int[][]{
            new int[]{1,2,3,4,5},
            new int[]{1,2,3,4,5},
            new int[]{1,2,3,4,6},
            new int[]{1,2,3,4,6},
            new int[]{1,2,3,4,7},
            new int[]{1,2,3,4,7},
            new int[]{1,2,3,4,8},
            new int[]{1,2,3,4,8}
    };


    @Test
    public void run() throws Exception {

        final int minSup = 2;

        SPMParameters params = new SPMParameters(seqDb, minSup);
        params.setMaxRedund(0);

        Collection<SequentialPattern> subset = new DCSpan().run(params);

        System.out.println("Subset patterns...");

        for (SequentialPattern sequentialPattern : subset) {
            System.out.println(sequentialPattern.toString());
        }

        double redundancy = new RedundancyCalculator().run(subset);
        Assert.assertEquals(0, redundancy, 1e-06);


    }

}