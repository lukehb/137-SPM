package onethreeseven.spm.algorithm;

import gnu.trove.iterator.TIntObjectIterator;
import onethreeseven.spm.model.LookupSequence;
import onethreeseven.spm.model.SequenceGraph;
import onethreeseven.spm.model.SequenceNode;
import onethreeseven.collections.IntArray;
import java.util.Arrays;
import java.util.List;

/**
 * Extract representative pattern from set of sequences
 * @author Luke Bermingham
 */
public class GraphSimplification {

    public List<SequenceGraph> run(final int[][] sequences, double simplificationFactor) {

        List<SequenceGraph> graphs = SequenceGraph.fromSequences(sequences);
        if(simplificationFactor == 0){
            return graphs;
        }

        int totalItems = Arrays.stream(sequences).mapToInt(value -> value.length).sum();

        //build look-up sequences
        LookupSequence[] lookups = new LookupSequence[sequences.length];
        for (int i = 0; i < sequences.length; i++) {
            int[] sequence = sequences[i];
            lookups[i] = new LookupSequence(sequence);
        }

        double curReduction = 0;

        while(curReduction <= simplificationFactor) {
            //simplify graphs
            for (SequenceGraph g : graphs) {
                simplify(g, lookups);
            }
            //see how much of a reduction occurred
            int nCurrentItems = Arrays.stream(lookups).mapToInt(LookupSequence::size).sum();
            curReduction = 1 - ((double)nCurrentItems/totalItems);
        }

        //get the "simplified" sequences
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = lookups[i].getActiveSequence();
        }

        return graphs;
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
