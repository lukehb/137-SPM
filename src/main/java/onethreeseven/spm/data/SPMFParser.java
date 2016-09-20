package onethreeseven.spm.data;


import onethreeseven.collections.IntArray;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * For parsing output from SPMF sequential patterns.
 * Can also parse SPMF sequence files.
 * @see <a href="http://www.philippe-fournier-viger.com/spmf/index.php?link=developers.php">SPMF</a>
 * for details on the file format.
 * @author Luke Bermingham
 */
public class SPMFParser {

    private static final Logger logger = Logger.getLogger(SPMFParser.class.getSimpleName());
    private final String delimiter;

    public SPMFParser(){
        this(" ");
    }

    public SPMFParser(String delimiter){
        this.delimiter = delimiter;
    }

    public int[][] parse(File file, int minSeqLength){
        try {
            return parse(new BufferedReader(new FileReader(file)), minSeqLength);
        } catch (FileNotFoundException e) {
            logger.severe("Could not find spmf output file to parse: " + e.getMessage());
        }
        return new int[][]{};
    }

    public int[][] parse(BufferedReader br, int minSeqLength){

        ArrayList<int[]> sequences = new ArrayList<>();

        String line;
        boolean keepReading = true;
        boolean readToLineBreak = false;

        while(keepReading){
            try {
                //has two read modes, read until a #, or read until a line break
                line = br.readLine();
                //we only care about lines that are read during the "read up to #" mode.
                if(line != null && !line.isEmpty()){
                    //parse
                    int[] seq = parseSequence(line);
                    //check if the sequence is long enough, and add it
                    if(seq.length >= minSeqLength){
                        sequences.add(seq);
                    }
                }
                readToLineBreak = !readToLineBreak;
                keepReading = line != null;

            } catch (IOException e) {
                logger.severe("Could not read spmf output file: " + e.getMessage());
            }
        }

        int[][] out = new int[sequences.size()][];
        out = sequences.toArray(out);
        return out;
    }

    private int[] parseSequence(String line){

        line = line.trim();
        String[] lineParts = line.split(delimiter);

        IntArray arr = new IntArray(lineParts.length, false);

        for (String part : lineParts) {
            part = part.trim();
            if(part.equals("-1") || part.equals("-2")){continue;}
            if(part.startsWith("#")){break;}
            arr.add(Integer.parseInt(part));
        }

        return arr.getArray();
    }

}
