package onethreeseven.spm.model;

import java.util.List;

/**
 * A candidate contiguous pattern used by the {@link onethreeseven.spm.algorithm.ProtoMiner} algorithm.
 * Unlike a normal sequential pattern is contains getters for both cover and support.
 * @author Luke Bermingham
 */
public class CandidatePattern extends CoveredSequence {

    private static final String supPrefix = " #SUP:";

    /**
     * This tracks the number of entities that visited this exact sequence of nodes
     */
    public final Visitations sequenceVisits;

    public CandidatePattern(List<SequenceEdge> sequence, Visitations sequenceVisits) {
        super(sequence);
        this.sequenceVisits = sequenceVisits;
    }

    @Override
    public String toString() {
        String str = super.toString();
        str += supPrefix + this.sequenceVisits.getSupport();
        return str;
    }

}
