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

    private static final String filename = "synthetic_1000000";
    private static final File inFile = new File(FileUtil.makeAppDir("spmf-files"), filename + ".txt");
    private static final int minSupAbs = 10;
    private static final int minLen = -1;
    private static final int topk = 8;
    private static final SPClosure selectedPatternClosure = SPClosure.MAX;

    private enum SPClosure {
        ALL, CLOSED, MAX, DISTINCT, TOPK, REP
    }

    private static File makeOutFile(SPClosure closure){
        String outFileName = filename + "_" + closure.name() +
                ((closure == SPClosure.TOPK && topk > 0) ? "_" + topk : "_minsup_" + minSupAbs) + ".txt";
        return new File(FileUtil.makeAppDir("contig_patterns/" + filename), outFileName);
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Loading spmf file");
        SPMFParser parser = new SPMFParser();
        int[][] seqDB = parser.parse(inFile, 1);


        File outFile = makeOutFile(selectedPatternClosure);

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
                System.out.println("Running new GraspMiner");
                new CoverMiner().run(seqDB, minSupAbs, outFile);
                break;
            case TOPK:
                System.out.println("Running TKS");
                AlgoTKS algo = new AlgoTKS();
                algo.setMaxGap(1);
                algo.runAlgorithm(inFile.getAbsolutePath(), outFile.getAbsolutePath(), topk);
                algo.writeResultTofile(outFile.getAbsolutePath());
                break;
            case REP:
                System.out.println("Running old grasp miner");
                new GraspMiner().run(seqDB, minSupAbs, 1, outFile);
                break;
        }

        System.out.println("Done, collect patterns at: " + outFile.getAbsolutePath());
    }

}
