package onethreeseven.spm.model;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import onethreeseven.collections.IntArray;
import onethreeseven.spm.algorithm.DepthFirstSearch;
import java.util.*;

/**
 * A graph show the transition between symbols in a sequence.
 *
 * @author Luke Bermingham
 */
public class SequenceGraph implements Iterable<SequenceEdge> {

    public final TIntObjectHashMap<SequenceNode> nodes = new TIntObjectHashMap<>();

    public PrimitiveIterator.OfInt getIntIterator(){
        return new PrimitiveIterator.OfInt() {
            final TIntObjectIterator<SequenceNode> internalIter = nodes.iterator();
            @Override
            public boolean hasNext() {
                return internalIter.hasNext();
            }

            @Override
            public int nextInt() {
                internalIter.advance();
                return internalIter.key();
            }
        };
    }

    /**
     * Constructs 1 or many graphs from the collection of sequences
     * @param sequences the sequences, one integer sequence per array
     * @return Sequence graphs
     */
    public static List<SequenceGraph> fromSequences(int[][] sequences){
        return constructGraphs(sequences);
    }

    private static List<SequenceGraph> constructGraphs(int[][] sequences){
        TIntObjectHashMap<SequenceNode> nodes = new TIntObjectHashMap<>();
        //populate the nodes and their neighbour relationships
        for (int seqIdx = 0; seqIdx < sequences.length; seqIdx++) {
            int[] sequence = sequences[seqIdx];
            if(sequence.length == 0){continue;}
            SequenceNode prevNode = null;

            for (int i = 0; i < sequence.length; i++) {
                int curId = sequence[i];
                SequenceNode curNode = nodes.get(curId);
                if (curNode == null) {
                    curNode = new SequenceNode(curId);
                    nodes.put(curId, curNode);
                }
                if (prevNode != null) {
                    prevNode.addEdgeTo(curNode, seqIdx, i-1);
                }
                prevNode = curNode;
            }
        }

        //sort the sequence nodes in descending order by who has the highest number of neighbours
        //this should allow us to get good starting candidates for constructing graphs using depth
        //first searching
        BitSet processedIds = new BitSet();
        List<SequenceNode> sortedNodes = new ArrayList<>(nodes.valueCollection());
        Collections.sort(sortedNodes, (o1, o2) -> o2.degree() - o1.degree());
        nodes.clear();

        Iterator<SequenceNode> iter = sortedNodes.iterator();

        //do a depth-first search and construct the graph(s)
        ArrayList<SequenceGraph> graphs = new ArrayList<>();
        while(iter.hasNext()){
            SequenceNode startNode = iter.next();
            if(!processedIds.get(startNode.id)){
                dfsConstructGraph(startNode, processedIds, graphs);
            }
            iter.remove();
        }

        return graphs;
    }

    private static void dfsConstructGraph(SequenceNode startNode, BitSet processedIds, ArrayList<SequenceGraph> graphs){
        SequenceGraph graph = new SequenceGraph();

        final SequenceNode[] sharedNode = new SequenceNode[]{null};

        new DepthFirstSearch(){
            @Override
            protected void processNode(SequenceNode node) {
                if(!processedIds.get(node.id)){
                    graph.nodes.put(node.id, node);
                    processedIds.set(node.id);
                }
                else{
                    //has a neighbour in some other sequence graph, merge the two graphs together
                    sharedNode[0] = node;
                }
            }

            @Override
            protected boolean keepSearching(SequenceNode node) {
                return true;
            }
        }.search(startNode);

        //there is shared node in the new graph that is already in one of the other graphs
        //this means the two graphs should merge together
        if(sharedNode[0] != null){
            for (SequenceGraph otherGraph : graphs) {
                if(otherGraph.nodes.containsKey(sharedNode[0].id)){
                    otherGraph.nodes.putAll(graph.nodes);
                }
            }
        }
        //no shared node, just add the graph
        else{
            graphs.add(graph);
        }
    }

    @Override
    public Iterator<SequenceEdge> iterator() {

        final BitSet processedEdges = new BitSet();
        final Iterator<SequenceNode> nodeIter = nodes.valueCollection().iterator();

        return new Iterator<SequenceEdge>() {
            SequenceEdge cachedNext = null;
            Iterator<SequenceEdge> localEdgeIter = nodeIter.next().iterator();

            private SequenceEdge getNext(){
                while(localEdgeIter.hasNext()){
                    SequenceEdge edge = localEdgeIter.next();
                    if(!processedEdges.get(edge.id)){
                        processedEdges.set(edge.id);
                        return edge;
                    }
                }
                if(nodeIter.hasNext()){
                    localEdgeIter = nodeIter.next().iterator();
                    return getNext();
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                if(cachedNext == null){
                    cachedNext = getNext();
                    return cachedNext != null;
                }
                else{
                    return true;
                }
            }

            @Override
            public SequenceEdge next() {
                if(cachedNext == null){
                    SequenceEdge next = getNext();
                    if(next == null){
                        throw new NoSuchElementException("No more edges left to iterate.");
                    }
                    return next;
                }
                else{
                    SequenceEdge res = cachedNext;
                    cachedNext = null;
                    return res;
                }
            }
        };
    }

    /**
     * Remove a node from the graph and join all in/out edges it had.
     * Note: The removal operation usually creates one new edge per in/out edge pair that
     * shared the removed node. And this new edge inherits the visitors and cover of the
     * in/out edge pair it absorbed.
     * @param nodeId The id of the node to remove from this graph.
     * @return the ids of nodes removed
     */
    public int[] remove(int nodeId){
        SequenceNode node = nodes.get(nodeId);
        if(node == null){throw new IllegalArgumentException("No such node found in the graph.");}
        //make edges between source node of in edge and destination node of out edge

        //note: had to make copies of in/out edges because fusing edges
        //can create new edges, which, during iteration, can lead to concurrent modification.
        ArrayList<SequenceEdge> inEdges = new ArrayList<>(node.inEdges());
        ArrayList<SequenceEdge> outEdges = new ArrayList<>(node.outEdges());

        for (SequenceEdge inEdge : inEdges) {
            for (SequenceEdge outEdge : outEdges) {
                SequenceEdge.fuseEdges(inEdge, outEdge);
            }
        }

        IntArray removedNodes = new IntArray(1 + node.inEdges().size() + node.outEdges().size(), false);

        //remove cur-node links from edges (in-edges)
        for (SequenceEdge inEdge : inEdges) {
            SequenceNode priorNode = inEdge.source;
            priorNode.removeEdge(node, false);
            //check if we have isolated this other node
            if(priorNode.inEdges().size() == 0 && priorNode.outEdges().size() == 0){
                this.nodes.remove(priorNode.id);
                removedNodes.add(priorNode.id);
            }
        }
        //out-edge links
        for (SequenceEdge outEdge : outEdges) {
            SequenceNode posteriNode = outEdge.destination;
            posteriNode.removeEdge(node, true);
            //check if we have isolated this other node
            if(posteriNode.inEdges().size() == 0 && posteriNode.outEdges().size() == 0){
                this.nodes.remove(posteriNode.id);
                removedNodes.add(posteriNode.id);
            }
        }
        node.clear();
        //remove it from the actual graph
        this.nodes.remove(nodeId);
        removedNodes.add(nodeId);
        return removedNodes.getArray();
    }

}
