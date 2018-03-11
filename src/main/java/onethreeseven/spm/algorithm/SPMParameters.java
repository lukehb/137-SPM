package onethreeseven.spm.algorithm;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.data.SPMFWriter;

import java.io.File;

/**
 * Parameters for {@link SPMAlgorithm}
 * @author Luke Bermingham
 */
public class SPMParameters {
    private final File spmfFile;
    private final int[][] sequences;
    private int minSup;
    private double maxRedund;
    private int topK;

    public SPMParameters(int[][] sequences, int minSup) {
        this.sequences = sequences;
        this.minSup = minSup;
        this.maxRedund = 0;
        this.spmfFile = null;
    }

    public SPMParameters(File spmfFile, int minSup){
        if(!FileUtil.fileOkayToRead(spmfFile)){
            throw new IllegalArgumentException("Cannot read this spmf file: " + spmfFile);
        }
        this.spmfFile = spmfFile;
        this.minSup = minSup;
        this.maxRedund = 0;
        this.sequences = null;
    }

    /**
     * Gets the sequences (may require a file read depending on which constructor was used).
     * @return The sequences to be passed as a parameter.
     */
    public int[][] getSequences() {
        if(sequences == null && spmfFile != null){
            SPMFParser parser = new SPMFParser();
            return parser.parseSequences(spmfFile);
        }
        return sequences;
    }

    /**
     * Gets the spmf file for the input sequences (may require a file write depending on which constructor was used).
     * @return The spmf sequences file.
     */
    public File getSpmfFile() {
        if(spmfFile == null && sequences != null){
            File out = FileUtil.makeTempFile();
            new SPMFWriter().write(out, sequences);
            return out;
        }
        return spmfFile;
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
}
