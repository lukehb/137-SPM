package onethreeseven.spm.algorithm;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.trajsuitePlugin.algorithm.BaseAlgorithmParams;
import java.io.File;

/**
 * Parameters for {@link SPMAlgorithm}
 * @author Luke Bermingham
 */
public class SPMParameters extends BaseAlgorithmParams {

    private final int[][] sequences;
    private int minSup;
    private double maxRedund;
    private int topK;

    private File outFile = null;

    public SPMParameters(int[][] sequences, int minSup) {
        this.sequences = sequences;
        this.minSup = minSup;
        this.maxRedund = 0;
        this.topK = 10;
    }

    public SPMParameters(File spmfFile, int minSup){
        if(!FileUtil.fileOkayToRead(spmfFile)){
            throw new IllegalArgumentException("Cannot read this spmf file: " + spmfFile);
        }
        SPMFParser parser = new SPMFParser();
        this.sequences = parser.parseSequences(spmfFile);

        this.minSup = minSup;
        this.maxRedund = 0;
        this.topK = 10;
    }

    /**
     * Gets the sequences.
     * @return The sequences to be passed as a parameter.
     */
    public int[][] getSequences() {
        return sequences;
    }

    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }

    public void setMaxRedund(double maxRedund) {
        this.maxRedund = maxRedund;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public int getMinSup() {
        return minSup;
    }

    public double getMaxRedund() {
        return maxRedund;
    }

    public double getMinSupRelative(){
        return minSup / (double)this.sequences.length;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    @Override
    public boolean areParametersValid() {
        if(topK < 1){
            System.err.println("Top-k must be greater than 0.");
            return false;
        }
        if(minSup < 1){
            System.err.println("Support must be greater than 0.");
            return false;
        }
        //if we have a file check if it is okay to read
        if(sequences == null || sequences.length < 1){
            System.err.println("Input sequences must be non-null and non-empty.");
            return false;
        }
        if(maxRedund < 0 || maxRedund > 1){
            System.err.println("Maximum redundancy must be between 0 and 1.");
            return false;
        }
        return true;
    }
}
