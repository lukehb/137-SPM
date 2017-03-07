package onethreeseven.spm.experiments;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoVMSP;
import onethreeseven.common.util.FileUtil;
import java.io.File;
import java.io.IOException;

/**
 * Runs the {@link AlgoVMSP} sequential pattern mining algorithm.
 * @author Luke Bermingham
 */
public class MinePatternsVMSP {

    private static final AlgoVMSP algo = new AlgoVMSP();
    private static final int maxGap = 1; //contiguous
    private static final String filename = "table1";
    private static final int minSup = 2;
    private static final double minSupRel = 0.5;
    private static final File inputSPMFFile = new File(FileUtil.makeAppDir("spmf-files"), filename + ".txt");
    private static final File outputSPMFFile = new File(FileUtil.makeAppDir("contig_patterns"),
            "vmsp_" + filename + "_max_minsup_" + minSup + "_set_0.txt");

    public static void main(String[] args) throws IOException {
        algo.setMaxGap(maxGap);
        algo.runAlgorithm(inputSPMFFile.getAbsolutePath(), outputSPMFFile.getAbsolutePath(), minSupRel);
    }

}
