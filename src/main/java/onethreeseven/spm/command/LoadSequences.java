package onethreeseven.spm.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.common.util.FileUtil;
import onethreeseven.jclimod.CLICommand;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
import onethreeseven.trajsuitePlugin.transaction.AddEntitiesTransaction;

import java.io.File;
import java.util.ServiceLoader;

/**
 * Load sequences file in the SPMF format into the entity layers
 * @author Luke Bermingham
 */
public class LoadSequences extends CLICommand {

    @Parameter(names = {"-i", "--in"}, description = "The spmf sequences file to load.", converter = FileConverter.class)
    private File inputFile;

    @Override
    protected String getUsage() {
        return "loadSequences -i sequences.txt";
    }

    @Override
    protected boolean parametersValid() {
        return FileUtil.fileOkayToRead(inputFile);
    }

    @Override
    protected boolean runImpl() {

        //load sequences into memory
        SPMFParser parser = new SPMFParser();
        int[][] seqs = parser.parseSequences(inputFile);

        //add them to layers in this transaction
        AddEntitiesTransaction transaction = new AddEntitiesTransaction();
        final String layername = "Sequences databases";

        transaction.add(layername, generateRerunAliasBasedOnParams(), seqs, true);

        ServiceLoader<TransactionProcessor> services = ServiceLoader.load(TransactionProcessor.class);
        for (TransactionProcessor service : services) {
            service.process(transaction);
        }

        return true;
    }

    @Override
    public boolean shouldStoreRerunAlias() {
        return true;
    }

    @Override
    public String generateRerunAliasBasedOnParams() {
        return FileUtil.getFilenameOnly(inputFile);
    }

    @Override
    public String getCategory() {
        return "Input";
    }

    @Override
    public String getCommandName() {
        return "loadSequences";
    }

    @Override
    public String[] getOtherCommandNames() {
        return new String[]{"loadSeqs", "loadSPMF", "loadspmf", "lseqs"};
    }

    @Override
    public String getDescription() {
        return "Load sequences file in the SPMF format into the entity layers.";
    }
}
