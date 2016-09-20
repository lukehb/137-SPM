package onethreeseven.spm.model;

import java.util.*;

/**
 * A contiguous integer sequence with some metrics (i.e support, cover).
 * @author Luke Bermingham
 */
public class RepSeq {

    private final static String fmt = "%s #COVER:%d #SUP:%d";

    private final int[] sequence;
    private final int cover;
    private final int sup;
    private Visitations visitations;

    public RepSeq(int cover, int sup, Visitations visitations, int... sequence) {
        this.sequence = sequence;
        this.cover = cover;
        this.sup = sup;
        this.visitations = visitations;
    }

    public Visitations getVisitations() {
        return visitations;
    }

    private String stringifySequence(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.length; i++) {
            int symbol = sequence[i];
            sb.append(String.valueOf(symbol));
            if (i < sequence.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public int getCover() {
        return cover;
    }

    public int size(){
        return sequence.length;
    }

    @Override
    public String toString() {
        return String.format(fmt, stringifySequence(), cover, sup);
    }

    public PrimitiveIterator.OfInt intIter() {
        return Arrays.stream(sequence).iterator();
    }

}
