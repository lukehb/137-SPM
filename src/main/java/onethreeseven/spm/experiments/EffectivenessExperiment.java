package onethreeseven.spm.experiments;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.PatternsLossinessCalculator;

import java.io.File;

/**
 * Compare the representativeness of a pattern closure by
 * comparing its output to that of entire pattern set (no closure).
 * @author Luke Bermingham
 */
public class EffectivenessExperiment {

    private static final File supersetSPMFFile = new File(
            FileUtil.makeAppDir("contig_patterns/tdrive"), "tdrive_ALL_minsup_1171.txt");

    private static final File subsetSPMFFile = new File(
            FileUtil.makeAppDir("contig_patterns/tdrive"), "tdrive_DISTINCT_minsup_1171redund_25.txt");


    public static void main(String[] args) {
//        double percentageRedundant = new RedundancyCalculator().run(subsetSPMFFile);
//        System.out.println(percentageRedundant + " of the pattern output was redundant.");


        double lossiness = new PatternsLossinessCalculator().run(subsetSPMFFile, supersetSPMFFile);

        System.out.println("Subset is has lossiness of: " + lossiness);





    }

}
