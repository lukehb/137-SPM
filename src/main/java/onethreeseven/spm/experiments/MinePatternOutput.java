package onethreeseven.spm.experiments;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.OutMiner;
import onethreeseven.spm.data.SPMFParser;
import java.io.File;

/**
 * Mines patterns from all sequential patterns.
 * @author Luke Bermingham
 */
public class MinePatternOutput {

    private static final double maxRedundancy = 0.0d;

    private static final File rawSPMFFile = new File(
            FileUtil.makeAppDir("spmf-files"), "tdrive.txt");

    private static final File inputPatternFile = new File(
            FileUtil.makeAppDir("contig_patterns/tdrive"), "tdrive_ALL_minsup_20.txt");

    private static final File outFile = new File(
            FileUtil.makeAppDir("contig_patterns/tdrive"), "tdrive_DISTINCT_minsup_20_maxredund_000.txt");

    private static final SPMFParser parser = new SPMFParser();

    public static void main(String[] args) {
        new OutMiner().run(
                parser.parseSequences(rawSPMFFile),
                parser.parsePatterns(inputPatternFile),
                maxRedundancy, outFile);
    }

}
