package onethreeseven.spm.algorithm;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.data.ChunkedSequenceGraphWriter;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.SequenceGraph;
import java.io.File;
import java.util.Collection;

/**
 * Test the graph chunker
 * @author Luke Bermingham
 */
public class FindChunks {

    private static final String filename = "synth_50";
    private static final File inFile = new File(FileUtil.makeAppDir("seq"), filename + ".txt");
    private static final int minSup = 10;

    public static void main(String[] args) {
        final SPMFParser parser = new SPMFParser();
        System.out.println("Loading sequences");
        final int[][] db = parser.parse(inFile, 1);
        System.out.println("Transforming into graphs");
        final Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(db);
        final Chunkarizer algo = new Chunkarizer();
        final ChunkedSequenceGraphWriter writer = new ChunkedSequenceGraphWriter();

        int i = 0;
        for (SequenceGraph graph : graphs) {
            System.out.println("Chunking....");
            algo.run(graph, minSup);
            System.out.println("Writing...");
            File outFile = new File(FileUtil.makeAppDir("seq_out"), filename + i + "_chunks.txt");
            writer.write(outFile, graph);
            System.out.println("Collect file at: " + outFile.getAbsolutePath());
            i++;
        }
    }

}
