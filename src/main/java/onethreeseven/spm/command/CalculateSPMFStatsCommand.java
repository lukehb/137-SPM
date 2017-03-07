package onethreeseven.spm.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.algorithm.SequenceDbStatsCalculator;
import onethreeseven.common.util.FileUtil;

import java.io.File;

/**
 * Gets the stats of an SPMF sequence database.
 * @author Luke Bermingham
 */
@Parameters(commandNames = "getstats", commandDescription = "Calculate the stats of an SPMF database.")
public class CalculateSPMFStatsCommand extends AbstractCommand{

    @Parameter(names = {"-i", "--in"}, description= "The input SPMF file.", converter = FileConverter.class)
    private File in;

    @Override
    protected void resetCommandParameters() {
        in = null;
    }

    @Override
    public void run() {
        if(!FileUtil.fileOkayToRead(in)){
            System.out.println("Input file cannot be read.");
            return;
        }
        int[][] sdb = new SPMFParser().parse(in, 0);
        System.out.println("Loaded SPMF db: " + in.getName());
        SequenceDbStatsCalculator calc = new SequenceDbStatsCalculator();
        calc.calculate(sdb);
        calc.printStats();
    }
}
