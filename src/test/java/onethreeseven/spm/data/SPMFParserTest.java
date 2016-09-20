package onethreeseven.spm.data;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * Test {@link SPMFParser}
 * @author Luke Bermingham
 */
public class SPMFParserTest {

    private static final String mockSPMF = "182657 -1 182658 -1 182659 -1 182660 -1 #SUP: 25\n" +
                                           "0 -1 #SUP: 137\n" +
                                           "177952 -1 154537 -1 154535 -1 57972 -1 #SUP: 25";

    @Test
    public void testParseSPMFOutput() throws Exception {

        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(mockSPMF.getBytes()));

        SPMFParser parser = new SPMFParser();
        int[][] output = parser.parse(new BufferedReader(isr), 2);

        Assert.assertArrayEquals(output[0], new int[]{182657, 182658, 182659, 182660});
        Assert.assertArrayEquals(output[1], new int[]{177952, 154537, 154535, 57972});

    }
}