package onethreeseven.spm.model;


import onethreeseven.collections.Range;

/**
 * A visited, directed edge between nodes of {@link SequenceNode}.
 * @author Luke Bermingham
 */
public class SequenceEdge {

    private static int edgeCounter = 0;

    public final SequenceNode destination;
    public final SequenceNode source;
    public final int id;
    //<SequenceId, Visitor Data>
    private final Visitations visitations;

    SequenceEdge(SequenceNode source, SequenceNode destination) {
        this(source, destination, new Visitations());
    }

    private SequenceEdge(SequenceNode source, SequenceNode destination, Visitations visitations){
        this.source = source;
        this.destination = destination;
        this.id = edgeCounter++;
        this.visitations = visitations;
    }

    void visit(int visitorId, int startIdx) {
        this.visitations.addVisitor(visitorId, new Range(startIdx, startIdx+1));
    }

    /**
     * @return The number of sequences that visited this edge.
     */
    public int getWeight() {
        return visitations.getNumberOfVisitors();
    }

    public int getCover(){
        return visitations.getTotalVisitedIndices();
    }

    public Visitations getVisitors(){
        return this.visitations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequenceEdge that = (SequenceEdge) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "(" + this.source.id + ")->(" + this.destination.id + ")";
    }

    /**
     * Given two edges we can try to fuse the source node of the
     * first edge to the destination node of the second edge. Thereby, making an edge made up of
     * all sequences that could traverse "both" the source and destination edge.
     * @param srcEdge The source edge.
     * @param destEdge The destination edge.
     */
    public static void fuseEdges(SequenceEdge srcEdge, SequenceEdge destEdge){

        Visitations mergedVisitations = Visitations.tryConnectTouching(srcEdge.getVisitors(), destEdge.getVisitors());
        if(mergedVisitations.getNumberOfVisitors() == 0){return;}

        SequenceEdge newEdge = new SequenceEdge(srcEdge.source, destEdge.destination, mergedVisitations);
        srcEdge.source.addOutEdge(newEdge);
    }

}
