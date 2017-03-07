package onethreeseven.spm.command;

import onethreeseven.spm.algorithm.CCSpan;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.Trie;
import onethreeseven.spm.model.TrieIterator;
import onethreeseven.common.util.FileUtil;

import java.io.File;

/**
 * Command to populateTrie the CC-Span algorithm.
 * @author Luke Bermingham
 */
@Parameters(commandNames = "ccspan", commandDescription = "CC-Span algorithm.")
public class CCSpanCommand extends AbstractCommand {

    @Parameter(names = {"-s", "--minsup"}, description = "The minimum absolute support.")
    private int minSup;

    @Parameter(names = {"-i", "--in"}, description= "The input SPMF file.", converter = FileConverter.class)
    private File in;

    @Parameter(names = {"-o", "--out"}, description= "The output sequential pattern.", required = false, converter = FileConverter.class)
    private File out;


    @Override
    protected void resetCommandParameters() {
        minSup = 1;
        in = null;
        out = null;
    }

    @Override
    public void run() {

        if(minSup < 1){
            System.out.println("Support must be greater than 0.");
            return;
        }
        if(!FileUtil.fileOkayToRead(in)){
            System.out.println("Input file cannot be read.");
            return;
        }
        boolean writingOutput = out != null;

        System.out.println("Begin CC-Span algorithm...\n" +
                "minsup= " + minSup + "\n" +
                "input SPMF file= " + in.getAbsolutePath() + "\n" +
                "output file= " + ((writingOutput) ? out.getAbsolutePath() : "none"));

        //loading in
        SPMFParser parser = new SPMFParser();
        int[][] sdb = parser.parse(in, 0);

        long startTime = System.currentTimeMillis();
        long endTime;
        CCSpan algo = new CCSpan();

        if(writingOutput){
            algo.run(sdb, minSup, out);
            endTime = System.currentTimeMillis();
            System.out.println("GraspMiner pattern output at: " + out.getAbsolutePath());
        }
        else{
            Trie<Integer> t = algo.populateTrie(sdb, minSup);
            endTime = System.currentTimeMillis();
            TrieIterator<Integer> iter = t.getPatternIterator(false);
            int nPatterns = 0;
            while(iter.hasNext()){
                nPatterns++;
                iter.next();
            }
            System.out.println("CC-Span found " + nPatterns + " patterns");
        }
        System.out.println("CC-Span algorithm finished in: " + (endTime-startTime) + "ms");
    }

}
