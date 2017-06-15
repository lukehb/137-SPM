package onethreeseven.spm.data;

import onethreeseven.common.data.AbstractWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Write int sequences in SPMF item-set format like:
 * 1 2 3 4 5
 * 2 3 1 4 6
 * That is, there are no duplicated and it is delimited by spaces.
 * @author Luke Bermingham
 */
public class SPMFItemsetWriter extends AbstractWriter<int[][]> {

    @Override
    protected boolean write(BufferedWriter bufferedWriter, int[][] sequences) throws IOException {
        boolean didWrite = false;
        for (int[] seq : sequences) {
            didWrite = writeItemset(bufferedWriter, seq);
            bufferedWriter.newLine();
        }
        return didWrite;
    }

    private boolean writeItemset(BufferedWriter bw, int[] sequence) throws IOException {
        int[] seq = Arrays.stream(sequence).distinct().sorted().toArray();
        for (int i = 0; i < seq.length; i++) {
            bw.write(String.valueOf(seq[i]));
            if(i < seq.length - 1){
                bw.write(" ");
            }
        }
        return true;
    }

}
