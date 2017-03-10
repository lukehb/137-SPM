package onethreeseven.spm.data;


import onethreeseven.common.data.AbstractWriter;
import java.io.BufferedWriter;
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

    public SPMFWriter() {
        setDelimiter(" "); //spmf uses space as delimiter it seems
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
    protected boolean write(BufferedWriter bw, int[][] sequences) throws IOException {
        for (int[] sequence : sequences) {
            write(bw, sequence);
        }
        return true;
    }
}
