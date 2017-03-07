package onethreeseven.spm.data;


import onethreeseven.common.data.AbstractWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes a sequence of integers to a file in the SPMF file format.
 * -1 denotes the end of an item-set
 * -2 denotes the end of a sequence
 * any other integer is a symbol in the sequence
 * This writer automatically inserts the -1 and -2 at the correct
 * position, so just pass an integer sequence.
 * @author Luke Bermingham
 */
public class SPMFWriter extends AbstractWriter<int[][]> {

    private BufferedWriter bw = null;

    public SPMFWriter() {
        setDelimiter(" "); //spmf uses space as delimiter it seems
    }

    public SPMFWriter(File file){
        setDelimiter(" ");
        try {
            if(file.createNewFile() && file.canWrite()) {
                bw = new BufferedWriter(new FileWriter(file));
            } else {
                this.logger.warning("A file already exists there, delete this first: " + file.getAbsolutePath());
            }
        } catch (IOException var13) {
            this.logger.severe("Could not create writer: " + var13.getMessage());
        }
    }

    public void write(int[] sequence){
        if(this.bw == null){
            throw new IllegalStateException("To use this method you must use the constructor that takes a file.");
        }
        write(this.bw, sequence);
    }

    public void close(){
        if(this.bw != null){
            try {
                this.bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void write(BufferedWriter bw, int[] sequence){
        try{
            for (int item : sequence) {
                bw.write(String.valueOf(item));
                bw.write(delimiter);
                bw.write("-1");
                bw.write(delimiter);
            }
            bw.write("-2");
            bw.newLine();
            bw.flush();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void write(BufferedWriter bw, int[][] sequences) throws IOException {
        for (int[] sequence : sequences) {
            write(bw, sequence);
        }
    }
}
