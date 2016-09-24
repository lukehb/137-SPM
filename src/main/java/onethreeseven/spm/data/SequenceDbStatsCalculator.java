package onethreeseven.spm.data;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.hash.TIntHashSet;
import onethreeseven.spm.model.SequenceEdge;
import onethreeseven.spm.model.SequenceGraph;
import onethreeseven.spm.model.SequenceNode;
import java.util.Collection;

/**
 * Calculate statistics for sequence databases in the format int[][].
 * @author Luke Bermingham
 */
public class SequenceDbStatsCalculator {

    private int totalSequences;
    private int totalItems;
    private int nDistinctItems;
    private double avgSequenceLength;
    private double recurrence;

    public void calculate(int[][] seqDb){
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(seqDb);


        totalItems = 0;
        totalSequences = seqDb.length;

        TIntHashSet distinctItems = new TIntHashSet();
        TIntIntHashMap edgeOccurrenceMap = new TIntIntHashMap();

        for (int[] sequence : seqDb) {

            totalItems += sequence.length;

            if(sequence.length == 0){
                continue;
            }

            int prevItem = sequence[0];
            distinctItems.add(prevItem);

            //populate the distinct items and the edge occurrences
            for (int i = 1; i < sequence.length; i++) {
                int curItem = sequence[i];
                distinctItems.add(curItem);

                SequenceEdge edge = getEdge(graphs, prevItem, curItem);
                if(edge == null){
                    continue;
                }
                edgeOccurrenceMap.adjustOrPutValue(edge.id, 1, 1);
            }

        }
        nDistinctItems = distinctItems.size();
        recurrence = getRecurrence(edgeOccurrenceMap);
        avgSequenceLength = totalItems/(double)totalSequences;
    }

    private SequenceEdge getEdge(Collection<SequenceGraph> graphs, int srcNodeId, int destNodeId){
        for (SequenceGraph graph : graphs) {
            SequenceNode curNode = graph.nodes.get(srcNodeId);
            if(curNode == null){continue;}
            return curNode.getOutEdge(destNodeId);
        }
        return null;
    }

    public static double getRecurrence(TIntIntHashMap edgeOccurrences){
        double totalOccurrences = 0;
        double totalDuplicates = 0;

        TIntIntIterator iter = edgeOccurrences.iterator();
        while(iter.hasNext()){
            iter.advance();
            int nEdgeOccurences = iter.value();
            totalOccurrences += nEdgeOccurences;
            if(nEdgeOccurences > 1){
                totalDuplicates += (nEdgeOccurences - 1);
            }
        }
        return totalDuplicates/totalOccurrences;
    }

    public int getTotalSequences() {
        return totalSequences;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getnDistinctItems() {
        return nDistinctItems;
    }

    public double getAvgSequenceLength() {
        return avgSequenceLength;
    }

    public double getRecurrence() {
        return recurrence;
    }

    public void printStats(){
        //output stats
        System.out.println("#Sequences: " + totalSequences);
        System.out.println("#Items: " + totalItems);
        System.out.println("Avg sequence length: " + avgSequenceLength);
        System.out.println("#Distinct items: " + nDistinctItems);
        System.out.println("Recurrence: " + recurrence);
    }

}
