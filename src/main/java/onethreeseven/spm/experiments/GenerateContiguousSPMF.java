package onethreeseven.spm.experiments;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.data.ContiguousSPMFGenerator;
import java.io.File;

/**
 * Generate SPMF sequence database made of small contiguous sequences.
 * @author Luke Bermingham
 */
public class GenerateContiguousSPMF {

    private static final int nSequences = 1000;
    private static final int nDistinctItems = 10;
    private static final int sequenceLength = 10;

    private static final File outputFile = new File(FileUtil.makeAppDir("spmf-files"),
            "synthetic_" + nSequences * sequenceLength + ".txt");

    private static final ContiguousSPMFGenerator gen = new ContiguousSPMFGenerator();

    public static void main(String[] args) {
        gen.setnDistinctItems(nDistinctItems);
        gen.setnSequences(nSequences);
        gen.setSequenceLength(sequenceLength);
        gen.generate(outputFile);
        System.out.println("Collect contiguous spmf db at: " + outputFile.getAbsolutePath());
    }

}
