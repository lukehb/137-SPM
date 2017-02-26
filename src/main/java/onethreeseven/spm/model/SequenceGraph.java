package onethreeseven.spm.model;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import onethreeseven.collections.IntArray;
import java.util.*;

/**
 * A graph show the transition between symbols in a sequence.
 *
 * @author Luke Bermingham
 */
public class SequenceGraph implements Iterable<SequenceEdge> {

    public final TIntObjectHashMap<SequenceNode> nodes = new TIntObjectHashMap<>();

    public PrimitiveIterator.OfInt getNodeIdIter(){
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
    public static Collection<SequenceGraph> fromSequences(int[][] sequences){
        return constructGraphs(sequences);
    }

    private static Collection<SequenceGraph> constructGraphs(int[][] sequences){

        //graph id
        int gid = 0;
        final int noEntryKey = -1;
        final TIntObjectHashMap<SequenceGraph> graphs = new TIntObjectHashMap<>(10, 0.75f, noEntryKey);
        final TIntIntHashMap nodeToGraphMap = new TIntIntHashMap(10, 0.75f, noEntryKey, noEntryKey);

        //populate the nodes and their neighbour relationships
        for (int seqIdx = 0; seqIdx < sequences.length; seqIdx++) {
            int[] sequence = sequences[seqIdx];
            if(sequence.length == 0){continue;}

            TIntObjectHashMap<SequenceGraph> toMerge = new TIntObjectHashMap<>();
            SequenceNode prevNode = null;
            int prevGraphId = noEntryKey;

            for (int i = 0; i < sequence.length; i++) {
                int curId = sequence[i];
                SequenceNode curNode;
                SequenceGraph curGraph;
                int curGraphId = nodeToGraphMap.get(curId);
                //not contained in any graph, have to use prevEntryGraph
                if(curGraphId == noEntryKey){
                    curNode = new SequenceNode(curId);
                    //no previous graph, have to make one
                    if(prevGraphId == noEntryKey){
                        curGraph = new SequenceGraph();
                        curGraph.nodes.put(curId, curNode);
                        curGraphId = gid;
                        gid++;
                        graphs.put(curGraphId, curGraph);
                    }
                    //there is a previous graph
                    else{
                        curGraphId = prevGraphId;
                        curGraph = graphs.get(curGraphId);
                        prevNode.addEdgeTo(curNode, seqIdx, i-1);
                        curGraph.nodes.put(curId, curNode);
                    }
                }
                //this node does appear in some graph already
                else{
                    curGraph = graphs.get(curGraphId);
                    curNode = curGraph.nodes.get(curId);
                    if (prevNode != null) {
                        prevNode.addEdgeTo(curNode, seqIdx, i-1);
                    }
                }
                nodeToGraphMap.put(curId, curGraphId);
                toMerge.put(curGraphId, curGraph);
                prevNode = curNode;
                prevGraphId = curGraphId;
            }

            //merge the "toMerge" map
            if(toMerge.size() > 1){
                mergeGraphs(toMerge, nodeToGraphMap, graphs);
            }
        }
        return graphs.valueCollection();
    }

    private static void mergeGraphs(TIntObjectHashMap<SequenceGraph> toMerge, TIntIntHashMap nodeToGraphMap, TIntObjectHashMap<SequenceGraph> graphs){
        TIntObjectIterator<SequenceGraph> iter = toMerge.iterator();
        iter.advance();

        //merge all graphs into this one
        SequenceGraph mergeInto = iter.value();
        int mergedGraphId = iter.key();

        while(iter.hasNext()){
            //merge the actual nodes into the new graph
            iter.advance();
            SequenceGraph curGraph = iter.value();
            mergeInto.nodes.putAll(curGraph.nodes);
            //change all the nodeToGraph mappings
            TIntObjectIterator<SequenceNode> nodeIter = curGraph.nodes.iterator();
            while(nodeIter.hasNext()){
                nodeIter.advance();
                nodeToGraphMap.put(nodeIter.key(), mergedGraphId);
            }
            graphs.remove(iter.key());
        }
    }

    @Override
    public Iterator<SequenceEdge> iterator() {
        return new Iterator<SequenceEdge>() {

            final TIntObjectIterator<SequenceNode> iter = nodes.iterator();
            final Stack<SequenceEdge> buffer = new Stack<>();

            @Override
            public boolean hasNext() {
                boolean isMoreNodes = iter.hasNext();
                boolean bufferEmpty = buffer.isEmpty();

                if(!bufferEmpty){
                    return true;
                }

                if(isMoreNodes) {
                    iter.advance();
                    SequenceNode cur = iter.value();
                    buffer.addAll(cur.outEdges());

                    return !buffer.isEmpty() || hasNext();
                }
                else {
                    return false;
                }
            }

            @Override
            public SequenceEdge next() {
                return buffer.pop();
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
