package onethreeseven.spm.data;

import onethreeseven.spm.model.CoveredSequentialPattern;
import onethreeseven.spm.model.SequentialPattern;
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

    private static final String testLine = "42241 4217 42249 5479 4218 5509  #SUP:24 #COVER:120";

    private static final String testLineNoSup = "42241 4217 42249 5479 4218 5509";

    private static final String testLineWithSpaces = "42241 4217 42249 5479 4218 5509  #SUP: 24 #COVER: 120";

    @Test
    public void testParseSPMFOutput() throws Exception {

        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(mockSPMF.getBytes()));

        SPMFParser parser = new SPMFParser();
        int[][] output = parser.parseSequences(new BufferedReader(isr));

        Assert.assertArrayEquals(output[0], new int[]{182657, 182658, 182659, 182660});
        Assert.assertArrayEquals(output[1], new int[]{0});
        Assert.assertArrayEquals(output[2], new int[]{177952, 154537, 154535, 57972});

    }

    @Test
    public void testParsePatternNoSup() throws Exception{
        SequentialPattern pattern = SPMFParser.parsePattern(testLineNoSup, " ");
        int[] expected = new int[]{42241,4217,42249,5479,4218,5509};
        Assert.assertArrayEquals(expected,pattern.getSequence());
    }

    @Test
    public void testParseSupportedPattern() throws Exception{
        SequentialPattern pattern = SPMFParser.parsePattern(testLine, " ");
        int[] expected = new int[]{42241,4217,42249,5479,4218,5509};
        Assert.assertArrayEquals(expected,pattern.getSequence());
        Assert.assertTrue(pattern.getSupport() == 24);
    }

    @Test
    public void testParseSupportedPatternWithSpaces() throws Exception{
        SequentialPattern pattern = SPMFParser.parsePattern(testLineWithSpaces, " ");
        int[] expected = new int[]{42241,4217,42249,5479,4218,5509};
        Assert.assertArrayEquals(expected,pattern.getSequence());
        Assert.assertTrue(pattern.getSupport() == 24);
    }

    @Test
    public void testParseCoveredPattern() throws Exception{
        SequentialPattern pattern = SPMFParser.parsePattern(testLine, " ");
        Assert.assertTrue(pattern instanceof CoveredSequentialPattern);
        int[] expected = new int[]{42241,4217,42249,5479,4218,5509};
        Assert.assertArrayEquals(expected,pattern.getSequence());
        Assert.assertTrue(pattern.getSupport() == 24);
        Assert.assertTrue( ((CoveredSequentialPattern)pattern).getCover() == 120);
    }

}