package onethreeseven.spm.data;

import onethreeseven.common.util.FileUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Test {@link SPMFItemsetWriter}.
 * @author Luke Bermingham
 */
public class SPMFItemsetWriterTest {

    private static File testFile =
            new File(FileUtil.makeAppDir("test"), String.valueOf(System.currentTimeMillis()) + ".tmp");

    private static final int[][] mockSequences = new int[][]{
            new int[]{1,2,3,4,5},
            new int[]{1,2,4,5,6},
            new int[]{1,3,7}
    };

    @BeforeClass
    public static void setup(){
        if(testFile.exists() && testFile.delete()){
            testFile = new File(FileUtil.makeAppDir("test"), String.valueOf(System.currentTimeMillis()) + ".tmp");
        }
    }

    public static void tearDown(){
        if(testFile.exists() && testFile.delete()){
            System.out.println("Deleted test item-set file: " + testFile.getAbsolutePath());
        }
    }

    @Test
    public void write() throws Exception {


        SPMFItemsetWriter writer = new SPMFItemsetWriter();
        writer.write(testFile, mockSequences);

        SPMFParser parser = new SPMFParser();
        int[][] readSequences = parser.parseSequences(testFile);

        for (int i = 0; i < readSequences.length; i++) {
            int[] actual = readSequences[i];
            int[] expected = mockSequences[i];
            Assert.assertArrayEquals(expected, actual);
        }

    }

}