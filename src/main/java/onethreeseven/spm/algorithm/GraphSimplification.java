package onethreeseven.spm.algorithm;

import gnu.trove.iterator.TIntObjectIterator;
import onethreeseven.spm.model.*;
import onethreeseven.collections.IntArray;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Simplifies sequence databases by analysing the sequence graph.
 * @author Luke Bermingham
 */
public class GraphSimplification {

    /**
     * Simplifies a sequence database using a lossless algorithm (not patterns are lost).
     * @param sequences The sequence database, note this will be modified internally.
     * @param minSup The absolute minimum support.
     * @return The simplified sequence graphs of the sequence databases.
     */
    public List<SequenceGraph> runLossless(final int[][] sequences, int minSup){
        List<SequenceGraph> graphs = SequenceGraph.fromSequences(sequences);
        LookupSequence[] lookups = Arrays.stream(sequences).map(LookupSequence::new).toArray(LookupSequence[]::new);
        minSup = Math.max(1, minSup);
        doLossless(graphs, lookups, minSup);
        //get the "simplified" sequences
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = lookups[i].getActiveSequence();
        }
        return graphs;
    }

    /**
     * Simplifies a sequence database using a lossy algorithm.
     * @param sequences The sequence database, note this will be modified internally.
     * @param simplificationFactor The simplification factor between 0 and 1.
     * @param minSup The absolute minimum support.
     * @return The simplified sequence graphs of the sequence databases.
     */
    public List<SequenceGraph> runLossy(final int[][] sequences, double simplificationFactor, int minSup) {

        simplificationFactor = Math.min(1, Math.max(simplificationFactor, 0));
        minSup = Math.max(1, minSup);

        List<SequenceGraph> graphs = SequenceGraph.fromSequences(sequences);
        if(simplificationFactor == 0){
            return graphs;
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

        //get the "simplified" sequences
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = lookups[i].getActiveSequence();
        }

        return graphs;
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
                    if(v.getNumberOfVisitors() >= minSup){
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
        //get the min node
        double minCentrality = Integer.MAX_VALUE;
        IntArray toRemove = new IntArray(4, false);

        TIntObjectIterator<SequenceNode> iter = g.nodes.iterator();
        while(iter.hasNext()){
            iter.advance();
            SequenceNode node = iter.value();

            double centrality = getCentrality(node);

            if(Math.abs(centrality-minCentrality) < 1e-07){
                toRemove.add(node.id);
            }
            else if(centrality < minCentrality){
                toRemove.clear();
                toRemove.add(node.id);
                minCentrality = centrality;
            }
        }

        //remove the "min" nodes
        removeNodes(g, lookups, toRemove);
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

    private double getCentrality(SequenceNode node){
        return node.getCover();
    }

}
