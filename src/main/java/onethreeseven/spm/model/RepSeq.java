package onethreeseven.spm.model;

import java.util.*;

/**
 * A contiguous integer sequence with some metrics (i.e support, cover).
 * @author Luke Bermingham
 */
public class RepSeq extends SequentialPattern{

    private final static String coverSuffix = " #COVER:";
    private final int cover;

    public RepSeq(int cover, int sup, int... sequence) {
        super(sequence, sup);
        this.cover = cover;
    }

    @Override
    public String toString() {
        return super.toString() + coverSuffix + cover;
    }

    public PrimitiveIterator.OfInt intIter() {
        return Arrays.stream(sequence).iterator();
    }

}
