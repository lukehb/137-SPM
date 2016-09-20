package onethreeseven.spm.model;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A node in a {@link SequenceGraph}, represent a single symbol in the graph.
 * Nodes have directed edges going in and out of them.
 * @author Luke Bermingham
 */
public class SequenceNode implements Iterable<SequenceEdge> {
    public final int id;
    //the collection of outEdges is treated as edge from this node to that neighbour node
    //(i.e it is a directed edge, not a bi-directional edge). Note: the key is the node id.
    private final TIntObjectHashMap<SequenceEdge> outEdges;
    private final TIntObjectHashMap<SequenceEdge> inEdges;

    private int cover = 0;

    public SequenceNode(int id) {
        this.id = id;
        this.outEdges = new TIntObjectHashMap<>(2);
        this.inEdges = new TIntObjectHashMap<>(2);
    }

    /**
     * Adds an out-going edge from this node to some other.
     * Also does the in-edge from the other node back to this one.
     * @param toNode The node to make an edge to.
     * @param sequenceId The id of the visiting sequence.
     * @param thisNodeIndex The index of this node in the sequence that is making the visit.
     * @return True if we added a new edge, false if we just incremented an existing edge.
     */
    public boolean addEdgeTo(SequenceNode toNode, int sequenceId, int thisNodeIndex) {
        SequenceEdge edge = outEdges.get(toNode.id);
        boolean newEdge = edge == null;

        if (newEdge) {
            edge = new SequenceEdge(this, toNode);
            outEdges.put(edge.destination.id, edge);
            toNode.inEdges.put(this.id, edge);
        }
        edge.visit(sequenceId, thisNodeIndex);
        cover += 1;
        toNode.cover += 1;
        return newEdge;
    }

    void addOutEdge(SequenceEdge edge){
        SequenceEdge existingEdge = outEdges.get(edge.destination.id);
        SequenceNode toNode = edge.destination;
        //update cover
        int addedCover = edge.getCover();
        cover += addedCover;
        toNode.cover += addedCover;
        //case already exists
        if(existingEdge != null){
            existingEdge.getVisitors().union(edge.getVisitors());
        }else{
            this.outEdges.put(toNode.id, edge);
            toNode.inEdges.put(this.id, edge);
        }
    }

    /**
     * Remove an in or out edge from this node.
     * @param node The node that is being connected to/from this node.
     * @param inEdge Whether it is an edge going "in" to this node or "out" of this node.
     * @return Whether the removal was successful.
     */
    public boolean removeEdge(SequenceNode node, boolean inEdge){
        SequenceEdge removedEdge = (inEdge) ? inEdges.remove(node.id) : outEdges.remove(node.id);
        if(removedEdge != null){
            this.cover -= removedEdge.getCover();
        }
        return removedEdge != null;
    }

    public SequenceEdge getOutEdge(int adjNodeId){
        return outEdges.get(adjNodeId);
    }

    public SequenceEdge getInEdge(int adjNodeId){
        return inEdges.get(adjNodeId);
    }

    public int degree(){
        return outEdges.size();
    }

    public void clear(){
        this.inEdges.clear();
        this.outEdges.clear();
    }

    public int getCover(){
        return this.cover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequenceNode that = (SequenceNode) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public Collection<SequenceEdge> outEdges(){
        return outEdges.valueCollection();
    }

    public Collection<SequenceEdge> inEdges() {return inEdges.valueCollection();}

    @Override
    public Iterator<SequenceEdge> iterator() {
        return new Iterator<SequenceEdge>() {

            Iterator<SequenceEdge> mainIter = outEdges().iterator();
            Iterator<SequenceEdge> otherIter = inEdges().iterator();

            @Override
            public boolean hasNext() {
                return mainIter.hasNext() || otherIter.hasNext();
            }

            private void swapIters(){
                //only swap if the other one is fine
                if(otherIter.hasNext()){
                    Iterator<SequenceEdge> tempIter = mainIter;
                    mainIter = otherIter;
                    otherIter = tempIter;
                }
            }

            @Override
            public SequenceEdge next() {

                if(mainIter.hasNext()){
                    SequenceEdge edge = mainIter.next();
                    swapIters();
                    return edge;
                }
                else if(otherIter.hasNext()){
                    swapIters();
                    return next();
                }
                throw new NoSuchElementException("No more edges left to iterate.");
            }
        };
    }
}
