package onethreeseven.spm.model;

import java.util.ArrayList;
import java.util.BitSet;
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
    public Visitations sequenceVisits;

    private final BitSet coveredEdges;

    public CandidatePattern(List<SequenceEdge> sequence, Visitations sequenceVisits) {
        super(sequence);
        this.sequenceVisits = sequenceVisits;
        this.coveredEdges = new BitSet();
        for (SequenceEdge sequenceEdge : sequence) {
            coveredEdges.set(sequenceEdge.id);
        }
    }

    private CandidatePattern(List<SequenceEdge> sequence, Visitations sequenceVisits, BitSet coveredEdges, int cover){
        super(sequence);
        this.sequenceVisits = sequenceVisits;
        this.coveredEdges = coveredEdges;
        this.cover = cover;
    }

    public static CandidatePattern extend(CandidatePattern existing, SequenceEdge newEdge, Visitations sequenceVisits, boolean toHead){
        ArrayList<SequenceEdge> edges = new ArrayList<>();
        if(toHead){
            edges.add(newEdge);
            edges.addAll(existing.sequence);
        }else{
            edges.addAll(existing.sequence);
            edges.add(newEdge);
        }
        BitSet coveredEdges = (BitSet) existing.coveredEdges.clone();
        int cover = existing.cover;
        if(!coveredEdges.get(newEdge.id)){
            cover += newEdge.getCover();
        }
        coveredEdges.set(newEdge.id);
        return new CandidatePattern(edges, sequenceVisits, coveredEdges, cover);
    }


    @Override
    public String toString() {
        String str = super.toString();
        str += supPrefix + this.sequenceVisits.getSupport();
        return str;
    }

    public SequentialPattern toSequentialPattern(){
        return new SequentialPattern(getNodeIds(), sequenceVisits.getSupport()){
            @Override
            public String toString() {
                return CandidatePattern.this.toString();
            }
        };
    }

}
