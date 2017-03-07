package onethreeseven.spm.experiments;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.PatternContainmentCalculator;
import java.io.File;

/**
 * Compare the representativeness of a pattern closure by
 * comparing its output to that of entire pattern set (no closure).
 * @author Luke Bermingham
 */
public class RepresentativenessExperiment {

    private static final File subsetSPMFFile =
            new File(FileUtil.makeAppDir("contig_patterns"), "tdrive_distinct_minsup_20_set_0.txt");

    private static final File supersetSPMFFile =
            new File(FileUtil.makeAppDir("contig_patterns"), "tdrive_all_minsup_20_set_0.txt");


    public static void main(String[] args) {
        double percentageRepresented = new PatternContainmentCalculator().run(subsetSPMFFile, supersetSPMFFile);
        System.out.println(percentageRepresented + " of the total patterns were represented by the smaller subset of patterns.");
    }

}
