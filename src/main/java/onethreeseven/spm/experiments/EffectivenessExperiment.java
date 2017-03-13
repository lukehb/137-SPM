package onethreeseven.spm.experiments;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.PatternContainmentCalculator;
import onethreeseven.spm.algorithm.RedundancyCalculator;
import onethreeseven.spm.algorithm.SequenceDbStatsCalculator;

import java.io.File;

/**
 * Compare the representativeness of a pattern closure by
 * comparing its output to that of entire pattern set (no closure).
 * @author Luke Bermingham
 */
public class EffectivenessExperiment {

    private static final File supersetSPMFFile = new File(
            FileUtil.makeAppDir("contig_patterns/tdrive"), "tdrive_ALL_minsup_20.txt");

    private static final File subsetSPMFFile = new File(
            FileUtil.makeAppDir("contig_patterns/tdrive"), "tdrive_DISTINCT_minsup_20_maxredund_090.txt");


    public static void main(String[] args) {
        double percentageRedundant = new RedundancyCalculator().run(subsetSPMFFile);
        System.out.println(percentageRedundant + " of the pattern output was redundant.");


        double percentageRepresented = new PatternContainmentCalculator().run(subsetSPMFFile, supersetSPMFFile);
        System.out.println(percentageRepresented + " of the total patterns were represented by the smaller subset of patterns.");





    }

}
