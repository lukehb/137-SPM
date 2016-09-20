package onethreeseven.spm.command;

import onethreeseven.spm.algorithm.GraspMiner;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.spm.data.RepSeqWriter;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.RepSeq;
import onethreeseven.spm.model.SequenceGraph;
import onethreeseven.common.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The command to run {@link GraspMiner}
 * @author Luke Bermingham
 */
@Parameters(commandNames = "graspminer", commandDescription = "Runs GraspMiner algorithm.")
public class GraspMinerCommand extends AbstractCommand {

    @Parameter(names = {"-s", "--minsup"}, description = "The minimum absolute support.")
    private int minSup;

    @Parameter(names = {"-g", "--maxgap"}, description = "The maximum gap to allow in patterns.")
    private int maxGap;

    @Parameter(names = {"-i", "--in"}, description= "The input SPMF file.", converter = FileConverter.class)
    private File in;

    @Parameter(names = {"-o", "--out"}, description= "The output sequential pattern.", required = false, converter = FileConverter.class)
    private File out;

    @Override
    public void resetCommandParameters() {
        this.minSup = 1;
        this.maxGap = 1;
        this.in = null;
        this.out = null;
    }

    @Override
    public void run() {

        if(minSup < 1){
            System.out.println("Support must be greater than 0.");
            return;
        }
        if(maxGap < 1){
            System.out.println("Max gap must be 1 (contiguous) or greater.");
            return;
        }
        if(!FileUtil.fileOkayToRead(in)){
            System.out.println("Input file cannot be read.");
            return;
        }
        boolean writingOutput = out != null;

        System.out.println("Begin GraspMiner algorithm...\n" +
                "minsup= " + minSup + "\n" +
                "maxgap= " + maxGap + "\n" +
                "input SPMF file= " + in.getAbsolutePath() + "\n" +
                "output file= " + ((writingOutput) ? out.getAbsolutePath() : "none"));

        //loading in
        SPMFParser parser = new SPMFParser();
        int[][] sdb = parser.parse(in, 0);


        //the algorithm
        long startTime = System.currentTimeMillis();
        int nPatterns = 0;
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(sdb);
        ArrayList<RepSeq> patterns = new ArrayList<>();
        GraspMiner algo = new GraspMiner();
        for (SequenceGraph graph : graphs) {
            Collection<RepSeq> p = algo.run(graph, sdb, minSup, maxGap);
            nPatterns += p.size();
            if(writingOutput){
                patterns.addAll(p);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("GraspMiner algorithm finished in: " + (endTime-startTime) + "ms");
        System.out.println("GraspMiner found " + nPatterns + " patterns");

        //writing
        if(writingOutput){
            RepSeqWriter writer = new RepSeqWriter();
            writer.write(out, patterns);
            System.out.println("GraspMiner pattern output at: " + out.getAbsolutePath());
        }



    }
}
