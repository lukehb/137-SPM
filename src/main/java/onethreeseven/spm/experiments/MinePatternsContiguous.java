package onethreeseven.spm.experiments;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.ACSpan;
import onethreeseven.spm.algorithm.CCSpan;
import onethreeseven.spm.algorithm.MCSpan;
import onethreeseven.spm.algorithm.ProtoMiner;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.SequenceGraph;
import java.io.File;
import java.util.Collection;

/**
 * Use {@link onethreeseven.spm.algorithm.ProtoMiner} to mine SPMF database.
 * @author Luke Bermingham
 */
public class MinePatternsContiguous {

    private static final String filename = "table1";
    private static final File inFile = new File(FileUtil.makeAppDir("spmf-files"), filename + ".txt");
    private static final ProtoMiner algo = new ProtoMiner();
    private static final int minSupAbs = 2;
    private static final int minLen = -1;
    private static final SPClosure selectedPatternClosure = SPClosure.MAX;

    private enum SPClosure {
        ALL, CLOSED, MAX, DISTINCT
    }

    private static File makeOutFile(String algoName, String closure, int suffix){
        String outFileName = algoName + "_" + filename + "_" + closure + "_minsup_"
                + minSupAbs + "_set_" + suffix + ".txt";
        return new File(FileUtil.makeAppDir("contig_patterns"), outFileName);
    }

    public static void main(String[] args) {

        System.out.println("Loading spmf file");
        SPMFParser parser = new SPMFParser();
        int[][] seqDB = parser.parse(inFile, 1);


        File outFile = null;

        switch (selectedPatternClosure){
            case ALL:
                System.out.println("Running AC-Span");
                outFile = makeOutFile("acspan", "all", 0);
                new ACSpan().run(seqDB, minSupAbs, outFile);
                break;
            case CLOSED:
                System.out.println("Running CC-Span");
                outFile = makeOutFile("ccspan", "closed", 0);
                new CCSpan().run(seqDB, minSupAbs, outFile);
                break;
            case MAX:
                System.out.println("Running MC-Span");
                outFile = makeOutFile("mcspan", "max", 0);
                new MCSpan().run(seqDB, minSupAbs, outFile);
                break;
            case DISTINCT:
                System.out.println("Running GraspMiner");
                System.out.println("Making sequence graph...");
                Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(seqDB);
                int i = 0;
                for (SequenceGraph graph : graphs) {
                    if(graph.nodes.size() > 2){
                        System.out.println("Beginning mining distinct patterns....");
                        outFile = makeOutFile("graspminer", "distinct", i);
                        algo.run(graph, seqDB, minSupAbs, minLen, outFile);
                        i++;
                    }
                }
                break;
        }

        if(outFile != null){
            System.out.println("Done, collect patterns at: " + outFile.getAbsolutePath());
        }
    }

}
