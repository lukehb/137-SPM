package onethreeseven.spm.experiments;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM;
import onethreeseven.common.util.FileUtil;
import java.io.File;
import java.io.IOException;

/**
 * Mines patterns using {@link ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM}
 * @author Luke Bermingham
 */
public class MinePatternsCMSpam {

    private static final AlgoCMSPAM algo = new AlgoCMSPAM();
    private static final int maxGap = 1; //contiguous
    private static final String filename = "synthetic_10000";
    private static final int minSup = 10;
    private static final double minSupRel = 0.01;
    private static final File inputSPMFFile = new File(FileUtil.makeAppDir("spmf-files"), filename + ".txt");
    private static final File outputSPMFFile = new File(FileUtil.makeAppDir("contig_patterns"),
            "cmspam_" + filename + "_all_minsup_" + minSup + "_set_0.txt");

    public static void main(String[] args) throws IOException {
        algo.setMaxGap(maxGap);
        algo.runAlgorithm(inputSPMFFile.getAbsolutePath(), outputSPMFFile.getAbsolutePath(), minSupRel, false);
    }

}
