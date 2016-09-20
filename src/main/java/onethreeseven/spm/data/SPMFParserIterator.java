package onethreeseven.spm.data;

import java.io.*;
import java.util.Arrays;

/**
 * Reads the SPMF format and makes each pattern available through iteration.
 * @see <a href="http://www.philippe-fournier-viger.com/spmf/index.php?link=developers.php">SPMF</a>
 * for details on the file format.
 * Note: {@link onethreeseven.spm.algorithm.GraspMiner} outputs cover, which this also supports.
 * @author Luke Bermingham
 */
public class SPMFParserIterator {

    private static final String delimiter = " ";

    private final BufferedReader reader;

    //current line fields
    private int[] curPattern;
    private int curCover;
    private int curSup;

    public SPMFParserIterator(File patternFile) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(patternFile));
    }

    public boolean advance(){
        try {
            if(!reader.ready()){return false;}
            String line = reader.readLine();
            parseLine(line);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int[] getPattern(){
        return curPattern;
    }

    public int getSupport(){
        return curSup;
    }

    public int getCover(){
        return curCover;
    }

    public void close(){
        try {
            this.reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String line){

        String[] parts = line.split("#");
        if(parts.length == 3){
            //35244 35246 32297 #COVER:12 #SUP:6
            String patternPart = parts[0];
            String[] patternTokens = patternPart.split(delimiter);
            this.curPattern = Arrays.stream(patternTokens).mapToInt(Integer::parseInt).toArray();

            String coverPart = parts[1];
            this.curCover = Integer.parseInt(coverPart.split(":")[1].trim());

            String supPart = parts[2];
            this.curSup = Integer.parseInt(supPart.split(":")[1].trim());
        }
        else if(parts.length == 2){
            //35244 35246 32297 #SUP:6
            String patternPart = parts[0];
            String[] patternTokens = patternPart.split(delimiter);
            this.curPattern = Arrays.stream(patternTokens).mapToInt(Integer::parseInt).toArray();

            String supPart = parts[1];
            this.curSup = Integer.parseInt(supPart.split(":")[1].trim());
        }
        //only have the pattern part
        else if(parts.length == 1){
            String patternPart = parts[0];
            String[] patternTokens = patternPart.split(delimiter);
            this.curPattern = Arrays.stream(patternTokens).mapToInt(Integer::parseInt).toArray();
        }
    }

}
