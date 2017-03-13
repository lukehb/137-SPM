package onethreeseven.spm.experiments;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoTKS;
import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.*;
import onethreeseven.spm.data.SPMFParser;
import java.io.File;
import java.io.IOException;

/**
 * Use {@link onethreeseven.spm.algorithm.ProtoMiner} to mine SPMF database.
 * @author Luke Bermingham
 */
public class MinePatternsContiguous {

    private static final String filename = "tdrive";
    private static final File inFile = new File(FileUtil.makeAppDir("spmf-files"), filename + ".txt");
    private static final int minSupAbs = 180;
    private static final double maxRedundancy = 0;
    private static final int topk = 797;
    private static final SPClosure selectedPatternClosure = SPClosure.DISTINCT;

    private enum SPClosure {
        ALL, CLOSED, MAX, DISTINCT, TOPK
    }

    private static File makeOutFile(SPClosure closure){

        int redund = (int) (maxRedundancy * 100);

        String outFileName = filename + "_" + closure.name() +
                ((closure == SPClosure.TOPK && topk > 0) ? "_" + topk : "_minsup_" + minSupAbs) +
                (closure == SPClosure.DISTINCT ? "redund_" + redund : "") +  ".txt";
        return new File(FileUtil.makeAppDir("contig_patterns/" + filename), outFileName);
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Loading spmf file");
        SPMFParser parser = new SPMFParser();

        int[][] seqDB = parser.parseSequences(inFile);


        File outFile = makeOutFile(selectedPatternClosure);

        long startTime = System.currentTimeMillis();

        switch (selectedPatternClosure){
            case ALL:
                System.out.println("Running AC-Span");
                new ACSpan().run(seqDB, minSupAbs, outFile);
                break;
            case CLOSED:
                System.out.println("Running CC-Span");
                new CCSpan().run(seqDB, minSupAbs, outFile);
                break;
            case MAX:
                System.out.println("Running MC-Span");
                new MCSpan().run(seqDB, minSupAbs, outFile);
                break;
            case DISTINCT:
                System.out.println("Running OutMiner");
                File allPatternsFile = makeOutFile(SPClosure.ALL);
                if(!allPatternsFile.exists()){
                    System.out.println("Need output to mine, run AC-SPAN first.");
                }else{
                    new OutMiner().run(seqDB, new SPMFParser().parsePatterns(allPatternsFile), maxRedundancy, outFile);
                }
                break;
            case TOPK:
                System.out.println("Running TKS");
                AlgoTKS algo = new AlgoTKS();
                algo.setMaxGap(1);
                algo.runAlgorithm(inFile.getAbsolutePath(), outFile.getAbsolutePath(), topk);
                algo.writeResultTofile(outFile.getAbsolutePath());
                break;
        }

        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println("Done, collect patterns at: " + outFile.getAbsolutePath());

        System.out.println("#Sequences, #Items, Average Sequence Length, #Distinct items, Redundancy(%), Running Time(ms)");

        SequenceDbStatsCalculator stats = new SequenceDbStatsCalculator();
        stats.calculate(new SPMFParser().parseSequences(outFile));

        System.out.println(
                stats.getTotalSequences() + ", " +
                stats.getTotalItems() + ", " +
                stats.getAvgSequenceLength() + ", " +
                stats.getnDistinctItems() + ", " +
                stats.getRedundancy() + ", " +
                runningTime);

    }

}
