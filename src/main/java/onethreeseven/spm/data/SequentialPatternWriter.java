package onethreeseven.spm.data;

import onethreeseven.common.data.AbstractWriter;
import onethreeseven.spm.model.SequentialPattern;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Writes {@link SequentialPattern} to file.
 * @author Luke Bermingham
 */
public class SequentialPatternWriter {

    private BufferedWriter bw;
    private static final Logger log = Logger.getLogger(SequentialPatternWriter.class.getSimpleName());

    public SequentialPatternWriter(File file){
        try {
            this.bw = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(SequentialPattern pattern){
        if(bw == null){
            log.severe("Cannot write because buffered writer is null, try making a new class.");
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
    }
}
