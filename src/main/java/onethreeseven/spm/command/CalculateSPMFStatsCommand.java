package onethreeseven.spm.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.jclimod.CLICommand;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.algorithm.SequenceDbStatsCalculator;
import onethreeseven.common.util.FileUtil;

import java.io.File;

/**
 * Gets the stats of an SPMF sequence database.
 * @author Luke Bermingham
 */
public class CalculateSPMFStatsCommand extends CLICommand{

    @Parameter(names = {"-i", "--in"}, description= "The input SPMF file.", converter = FileConverter.class)
    private File in;

    @Override
    protected String getUsage() {
        return "spmf-stats -i mysequences.txt";
    }

    @Override
    protected boolean parametersValid() {

        if(!FileUtil.fileOkayToRead(in)){
            System.err.println("Could not read input spmf file: " + in);
            return false;
        }

        return true;
    }

    @Override
    protected boolean runImpl() {
        int[][] sdb = new SPMFParser().parseSequences(in);
        System.out.println("Loaded SPMF db: " + in.getName());
        SequenceDbStatsCalculator calc = new SequenceDbStatsCalculator();
        calc.calculate(sdb);
        calc.printStats();
        return true;
    }

    @Override
    public boolean shouldStoreRerunAlias() {
        return false;
    }

    @Override
    public String generateRerunAliasBasedOnParams() {
        return null;
    }

    @Override
    public String getCategory() {
        return "Stats";
    }

    @Override
    public String getCommandName() {
        return "spmf-stats";
    }

    @Override
    public String[] getOtherCommandNames() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Calculate the stats of an SPMF sequence database file.";
    }
}
