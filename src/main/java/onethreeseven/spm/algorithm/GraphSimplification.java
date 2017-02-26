package onethreeseven.spm.algorithm;


import onethreeseven.spm.model.*;
import onethreeseven.collections.IntArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Simplifies sequence databases by analysing the sequence graph.
 * @author Luke Bermingham
 */
public class GraphSimplification {

    /**
     * Simplifies a sequence database using a lossless algorithm (not patterns are lost).
     * @param sequences The sequence database, note this will be modified internally.
     * @param minSup The absolute minimum support.
     * @return The simplified sequence database
     */
    public int[][] runLossless(final int[][] sequences, int minSup){
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(sequences);
        LookupSequence[] lookups = Arrays.stream(sequences).map(LookupSequence::new).toArray(LookupSequence[]::new);
        minSup = Math.max(1, minSup);
        doLossless(graphs, lookups, minSup);
        //get the "simplified" sequences
        int[][] simplified = new int[sequences.length][];
        for (int i = 0; i < sequences.length; i++) {
            simplified[i] = lookups[i].getActiveSequence();
        }
        return simplified;
    }

    /**
     * Simplifies a sequence database using a lossy algorithm.
     * @param sequences The sequence database, note this will be modified internally.
     * @param simplificationFactor The simplification factor between 0 and 1.
     * @param minSup The absolute minimum support.
     * @return The simplified sequence database.
     */
    public int[][] runLossy(final int[][] sequences, double simplificationFactor, int minSup) {

        simplificationFactor = Math.min(1, Math.max(simplificationFactor, 0));
        minSup = Math.max(1, minSup);

        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(sequences);
        if(simplificationFactor == 0){
            return sequences;
        }

        int totalItems = 0;

        //build look-up sequences
        LookupSequence[] lookups = new LookupSequence[sequences.length];
        for (int i = 0; i < sequences.length; i++) {
            int[] sequence = sequences[i];
            totalItems += sequence.length;
            lookups[i] = new LookupSequence(sequence);
        }

        //run the actual simplification
        doLossless(graphs, lookups, minSup);
        doLossy(graphs, lookups, simplificationFactor, totalItems);

        int[][] simplified = new int[sequences.length][];
        //get the "simplified" sequences
        for (int i = 0; i < sequences.length; i++) {
            simplified[i] = lookups[i].getActiveSequence();
        }

        return simplified;
    }

    /**
     * Lossless simplification just removed all singleton nodes with support less than minSup.
     * @param graphs sequence graphs to examine
     * @param lookups lookup sequences
     * @param minSup the minimum absolute support
     */
    private void doLossless(Collection<SequenceGraph> graphs, LookupSequence[] lookups, int minSup){
        for (SequenceGraph g : graphs) {

            IntArray toRemove = new IntArray(10, false);

            //go through all nodes and find any that are below minSup
            for (SequenceNode node : g.nodes.valueCollection()) {
                //if all possible super-sequences of this node are infrequent, then the node is also infrequent
                //this is the apriori-property, and thus the node can be removed
                boolean inFrequent = true;
                Visitations v = null;
                for (SequenceEdge sequenceEdge : node) {
                    if(v == null){
                        v = sequenceEdge.getVisitors();
                    }
                    else{
                        v.union(sequenceEdge.getVisitors());
                    }
                    if(v.getSupport() >= minSup){
                        inFrequent = false;
                        break;
                    }
                }
                if(inFrequent){
                    toRemove.add(node.id);
                }
            }

            //remove all in-frequent nodes
            if(toRemove.size() > 0){
                removeNodes(g, lookups, toRemove);
            }
        }
    }

    private void doLossy(Collection<SequenceGraph> graphs, LookupSequence[] ls, double simplificationGoal, int totalItems){
        int nCurrentItems = Arrays.stream(ls).mapToInt(LookupSequence::size).sum();
        double curReduction = 1 - ((double)nCurrentItems/totalItems);

        while(curReduction <= simplificationGoal) {
            //simplify graphs
            for (SequenceGraph g : graphs) {
                simplify(g, ls);
            }
            //see how much of a reduction occurred
            nCurrentItems = Arrays.stream(ls).mapToInt(LookupSequence::size).sum();
            curReduction = 1 - ((double)nCurrentItems/totalItems);
        }
    }

    private void simplify(SequenceGraph g, LookupSequence[] lookups){
        //get the min edges
        double minScore = Integer.MAX_VALUE;
        Collection<SequenceEdge> toRemove = new ArrayList<>();

        for (SequenceEdge sequenceEdge : g) {
            int indexCover = sequenceEdge.getVisitors().getCover();

            if(indexCover == minScore){
                toRemove.add(sequenceEdge);
            }
            else if(indexCover < minScore){
                toRemove.clear();
                toRemove.add(sequenceEdge);
                minScore = indexCover;
            }
        }

        //remove the "min" edges
        removeEdges(g, lookups, toRemove);
    }

    private void removeEdges(SequenceGraph g, LookupSequence[] lookups, Collection<SequenceEdge> edges){
        //remove from lookup sequences
        for (SequenceEdge edge : edges) {
            for (LookupSequence lookup : lookups) {
                int[] toClear = new int[]{edge.source.id, edge.destination.id};
                while(true){
                    if(!lookup.clear(toClear)){break;}
                }
            }
        }
        //remove from sequence graph
        for (SequenceEdge edge : edges) {
            edge.source.removeEdge(edge.destination, false);
            edge.destination.removeEdge(edge.source, true);

            //check for dangling node
            if(edge.source.inEdges().size() + edge.source.outEdges().size() == 0){
                g.remove(edge.source.id);
            }
            if(edge.destination.inEdges().size() + edge.destination.outEdges().size() == 0){
                g.remove(edge.source.id);
            }
        }
    }

    private void removeNodes(SequenceGraph g, LookupSequence[] lookups, IntArray toRemove){
        int size = toRemove.size();
        for (int i = 0; i < size; i++) {
            int nodeId = toRemove.get(i);
            if(g.nodes.containsKey(nodeId)){
                //actual removal from graph
                int[] removedNodeIds = g.remove(nodeId);
                //now clear the removed node from the lookup sequences
                for (LookupSequence lookup : lookups) {
                    for (int symbol : removedNodeIds) {
                        lookup.clear(symbol);
                    }
                }
            }
        }
    }


}
