package onethreeseven.spm.data;

import onethreeseven.spm.model.SequentialPattern;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes {@link SequentialPattern} to file.
 * @author Luke Bermingham
 */
public class SequentialPatternWriter {

    private BufferedWriter bw;
    private FileWriter fw;

    public SequentialPatternWriter(File file){
        try {
            this.fw = new FileWriter(file, true);
            this.bw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(SequentialPattern pattern){
        if(bw == null){
            System.err.println("Cannot write because buffered writer is null, try making a new class.");
        }
        try {
            bw.write(pattern.toString());
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if(bw != null){
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(fw != null){
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
