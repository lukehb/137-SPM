package onethreeseven.spm.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.algorithm.GraspMiner;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.RepSeq;
import java.io.File;
import java.util.Collection;

/**
 * The command to populateTrie {@link GraspMiner}
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
        int[][] sdb = parser.parseSequences(in);


        //the algorithm
        long startTime = System.currentTimeMillis();
        int nPatterns = 0;

        if(writingOutput){
            new GraspMiner().run(sdb, minSup, maxGap, out);
            System.out.println("GraspMiner pattern output at: " + out.getAbsolutePath());
        }else{
            Collection<RepSeq> patterns = new GraspMiner().run(sdb, minSup, maxGap);
            nPatterns = patterns.size();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("GraspMiner algorithm finished in: " + (endTime-startTime) + "ms");
        System.out.println("GraspMiner found " + nPatterns + " patterns");
    }
}
