package onethreeseven.spm;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.ProtoMiner;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.SequenceGraph;

import java.io.File;
import java.util.Collection;

/**
 * Use {@link onethreeseven.spm.algorithm.ProtoMiner} to mine SPMF database.
 * @author Luke Bermingham
 */
public class ProtoMineSPMF {

    private static final String filename = "kosarak";
    private static final File inFile = new File(FileUtil.makeAppDir("seq"), filename + ".txt");
    private static final ProtoMiner algo = new ProtoMiner();
    private static final int minSup = 20;
    private static final int minLen = -1;

    public static void main(String[] args) {

        SPMFParser parser = new SPMFParser();
        int[][] seqDB = parser.parse(inFile, 1);

        System.out.println("Making sequence graph...");
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(seqDB);

        int i = 0;
        for (SequenceGraph graph : graphs) {
            if(graph.nodes.size() > 2){
                String outFileName = filename + "_minsup_" + minSup + "_set_" + i + ".txt";
                System.out.println("Beginning mining....");
                File outFile = new File(FileUtil.makeAppDir("contig_patterns"), outFileName);
                algo.run(graph, seqDB, minSup, minLen, outFile);
                System.out.println("Collect patterns at: " + outFile.getAbsolutePath());
                i++;
            }
        }

    }

}
