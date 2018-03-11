package onethreeseven.spm.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.common.util.FileUtil;
import onethreeseven.jclimod.CLICommand;
import onethreeseven.spm.algorithm.*;
import onethreeseven.spm.model.SequentialPattern;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
import onethreeseven.trajsuitePlugin.model.WrappedEntity;
import onethreeseven.trajsuitePlugin.transaction.AddEntitiesTransaction;
import onethreeseven.trajsuitePlugin.util.IdGenerator;
import java.io.File;
import java.util.*;

/**
 * Command to populateTrie the CC-Span algorithm.
 * @author Luke Bermingham
 */
public class MineSequentialPatterns extends CLICommand {

    private static final Map<String, SPMAlgorithm> supportedAlgos = new HashMap<>();
    static {
        supportedAlgos.put("cmspam", new CMSpamWrapper());
        supportedAlgos.put("cmspade", new CMSpadeWrapper());
        supportedAlgos.put("vmsp", new VMSPWrapper());
        supportedAlgos.put("prefixspan", new PrefixSpanWrapper());
        supportedAlgos.put("acspan", new ACSpan());
        supportedAlgos.put("mcspan", new MCSpan());
        supportedAlgos.put("ccspan", new CCSpan());
        supportedAlgos.put("dcspan", new DCSpan());
        supportedAlgos.put("clospan", new CloSpanWrapper());
        supportedAlgos.put("cmclasp", new CMClaspWrapper());
        supportedAlgos.put("tks", new TKSWrapper());
    }

    @Parameter(names = {"-k", "--topK"}, description = "The top-k patterns to keep. Only relevant if using -a tks")
    private int topK = 10;

    @Parameter(names = {"-s", "--minsup"}, description = "The minimum absolute support.")
    private int minSup = 10;

    @Parameter(names = {"-i", "--in"}, description= "The input SPMF file.", converter = FileConverter.class)
    private File in;

    @Parameter(names = {"-o", "--out"}, description= "The output sequential pattern.", converter = FileConverter.class)
    private File out;

    @Parameter(names = {"-a", "--algo"}, description = "The chosen sequential pattern mining algorithm, valid options include: cmspam, cmspade, vmsp, prefixspan, acspan, ccspan, mcspan, dcspan, clospan, cmclasp, tks.")
    private String algoName = null;
    private SPMAlgorithm algo = null;
    private SPMParameters params = null;

    @Parameter(names = {"-q", "--quiet"}, description = "If true, outputs some extra information like total running time.")
    private boolean quiet = false;

    @Parameter(names = {"-r", "--maxRedund"}, description = "If using DCSPAN, this is the maximum allowable redundancy " +
            "in each output pattern, between 0 and 1. Note, for other algorithms this parameter has no effect.")
    private double maxRedundancy = 0.5;

    @Override
    protected String getUsage() {
        return "spm --minsup 10 --algo prefixspan -i sequences.txt";
    }

    @Override
    protected boolean parametersValid() {

        if(topK < 1){
            System.err.println("Top-k must be greater than 0.");
            return false;
        }
        if(minSup < 1){
            System.err.println("Support must be greater than 0.");
            return false;
        }
        //if we have a file check if it is okay to read
        if(in != null && !FileUtil.fileOkayToRead(in)){
            System.err.println("Input sequences file cannot be read.");
            return false;
        }
        if(maxRedundancy < 0 || maxRedundancy > 1){
            System.err.println("Maximum redundancy must be between 0 and 1.");
            return false;
        }
        if(algoName == null || algoName.isEmpty()){
            System.err.println("SPM algorithm name must be non-null, try -a ccspan");
            return false;
        }else if(!supportedAlgos.containsKey(algoName)){
            System.err.println("Unsupported spm algorithm, was passed: " + algoName);
            System.err.println("The following algorithm names are supported as input parameters: ");
            for (String spmAlgorithm : supportedAlgos.keySet()) {
                System.err.println(spmAlgorithm);
            }
            return false;
        }

        algo = supportedAlgos.get(algoName);
        int[][] seqDb = (in == null) ? getSelectedSequences() : null;
        if(seqDb == null){
            System.err.println("There was no selected sequences database of integers. Please select a int[][] next time.");
            return false;
        }

        if(in != null){
            params = new SPMParameters(in, minSup);
        }else{
            params = new SPMParameters(seqDb, minSup);
        }
        params.setMaxRedund(maxRedundancy);
        params.setTopK(topK);
        
        return true;
    }

    protected int[][] getSelectedSequences(){

        int[][] seqs = null;

        ServiceLoader<EntitySupplier> loader = ServiceLoader.load(EntitySupplier.class);

        for (EntitySupplier entitySupplier : loader) {
            Map<String, WrappedEntity> selectedSeqs = entitySupplier.supplyAllMatching(wrappedEntity -> {
                if(!wrappedEntity.isSelectedProperty().get()){
                    return false;
                }
                Object model = wrappedEntity.getModel();
                return model instanceof int[][];
            });

            for (WrappedEntity wrappedEntity : selectedSeqs.values()) {
                Object model = wrappedEntity.getModel();
                if(model instanceof int[][]){
                    seqs = (int[][]) model;
                    //just take the first selected int[][]
                    break;
                }
            }

        }

        return seqs;
    }

    @Override
    protected void resetParametersAfterRun(Class clazz) {
        super.resetParametersAfterRun(clazz);
        minSup = 10;
        topK = 10;
        maxRedundancy = 0.5;
        quiet = false;
    }

    @Override
    protected boolean runImpl() {

        long startTime = System.currentTimeMillis();

        if(algo != null && params != null){

            boolean writingOutput = out != null;

            //write patterns to file
            if(writingOutput){
                algo.run(params, out);
            }
            //storing patterns using a service
            else{

                //run it and keep patterns in memory
                Collection<SequentialPattern> patterns = algo.run(params);

                //make an add entities transactions
                AddEntitiesTransaction transaction = new AddEntitiesTransaction();
                String layername = algo.toString() + "_" + patterns.size() + "patterns";

                for (SequentialPattern pattern : patterns) {
                    transaction.add(layername, IdGenerator.nextId(), pattern);
                }

                ServiceLoader<TransactionProcessor> services = ServiceLoader.load(TransactionProcessor.class);
                for (TransactionProcessor service : services) {
                    service.process(transaction);
                }

            }

            long runningTime = System.currentTimeMillis() - startTime;
            if(!quiet){
                System.out.println("Running SPM algo, " + algo + " took " + runningTime + "ms");
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean shouldStoreRerunAlias() {
        return in != null && in.exists();
    }

    @Override
    public String generateRerunAliasBasedOnParams() {
        return FileUtil.getFilenameOnly(in);
    }

    @Override
    public String getCategory() {
        return "Mining";
    }

    @Override
    public String getCommandName() {
        return "mineSeqPat";
    }

    @Override
    public String[] getOtherCommandNames() {
        return new String[]{"spm"};
    }

    @Override
    public String getDescription() {
        return "Mine sequential patterns from any inputs sequential patterns file or from selected sequences in the entity layers.";
    }
}
