package onethreeseven.spm.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import onethreeseven.spm.data.GephiWriter;
import onethreeseven.spm.data.SPMFParserIterator;
import onethreeseven.common.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Command to convert a SPMF file to the gephi format.
 * @author Luke Bermingham
 */
@Parameters(commandNames = "spmf2gephi", commandDescription = "Converts SPMF file to csv for reading by Gephi.")
public class SPMFToGephiCommand extends AbstractCommand {

    @Parameter(names = {"-i", "--in"}, description= "The input SPMF file.", converter = FileConverter.class)
    private File in;

    @Parameter(names = {"-o", "--out"}, description= "The output gephi csv.", converter = FileConverter.class)
    private File out;

    @Override
    protected void resetCommandParameters() {
        in = null;
        out = null;
    }

    @Override
    public void run() {

        if(!FileUtil.fileOkayToRead(in)){
            System.out.println("Input file cannot be read.");
            return;
        }
        if(out == null){
            System.out.println("Output file is null.");
            return;
        }
        if(out.exists()){
            System.out.println("File, " + out.getAbsolutePath() + "already exists. " +
                    "Delete it first to make a new one.");
            return;
        }

        //do the actual conversion and writing
        try {
            SPMFParserIterator iter = new SPMFParserIterator(in);
            new GephiWriter().write(out, iter);
            System.out.println("Collect output gephi csv at: " + out.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
