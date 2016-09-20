package onethreeseven.spm.data;

import onethreeseven.common.data.AbstractWriter;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writing spmf pattern file to gephi edge list csv format.
 * @author Luke Bermingham
 */
public class GephiWriter extends AbstractWriter<SPMFParserIterator> {

    private static final String headerRow = "Source,Target,Weight,Support";
    private static final String delimiter = ",";


    @Override
    protected void write(BufferedWriter bw, SPMFParserIterator spp) throws IOException {

        bw.write(headerRow);
        bw.newLine();

        while(spp.advance()){

            int[] pattern = spp.getPattern();
            int cover = spp.getCover();
            int sup = spp.getSupport();

            int sourceId = pattern[0];
            for (int i = 1; i < pattern.length; i++) {
                int targetId = pattern[i];

                bw.write(Integer.toString(sourceId));
                bw.write(delimiter);
                bw.write(Integer.toString(targetId));
                bw.write(delimiter);
                bw.write(Integer.toString(cover));
                bw.write(delimiter);
                bw.write(Integer.toString(sup));
                bw.newLine();

                sourceId = targetId;
            }


        }
        spp.close();


    }
}
