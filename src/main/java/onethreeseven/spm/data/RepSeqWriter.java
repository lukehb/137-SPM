package onethreeseven.spm.data;


import onethreeseven.spm.model.RepSeq;
import onethreeseven.common.data.AbstractWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Writer for {@link RepSeq}
 * @author Luke Bermingham
 */
public class RepSeqWriter extends AbstractWriter<Collection<RepSeq>> {

    @Override
    protected void write(BufferedWriter bw, Collection<RepSeq> seqs) throws IOException {
        for (RepSeq seq : seqs) {
            bw.write(seq.toString());
            bw.newLine();
        }
    }
}
