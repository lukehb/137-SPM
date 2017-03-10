package onethreeseven.spm.model;

/**
 * A {@link SequentialPattern} with support and cover.
 * @author Luke Bermingham
 */
public class CoveredSequentialPattern extends SequentialPattern {

    private static final String coverSuffix = " #COVER:";

    private final int cover;

    public CoveredSequentialPattern(int[] sequence, int support, int cover) {
        super(sequence, support);
        this.cover = cover;
    }

    public int getCover() {
        return cover;
    }

    @Override
    public String toString() {
        return super.toString() + coverSuffix + cover;
    }
}
