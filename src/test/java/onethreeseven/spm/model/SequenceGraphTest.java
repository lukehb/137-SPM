package onethreeseven.spm.model;

import onethreeseven.collections.Range;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collection;
import java.util.Random;

/**
 * Tests {@link SequenceGraph}
 * @author Luke Bermingham
 */
public class SequenceGraphTest {

    @Test
    public void testAddEdgesAndCount() throws Exception {

        final int nTransitions = 10000;
        final SequenceGraph g = new SequenceGraph();
        final int universe = 137;
        final Random rand = new Random();

        int totalUniqueEdges = 0;

        for (int i = 0; i < nTransitions; i++) {
            int symbolA = rand.nextInt(universe);
            int symbolB = rand.nextInt(universe);
            SequenceNode fromNode = g.nodes.get(symbolA);
            if(fromNode == null){
                fromNode = new SequenceNode(symbolA);
                g.nodes.put(symbolA, fromNode);
            }
            SequenceNode toNode = g.nodes.get(symbolB);
            if(toNode == null){
                toNode = new SequenceNode(symbolB);
                g.nodes.put(symbolB, toNode);
            }
            totalUniqueEdges += fromNode.addEdgeTo(toNode, rand.nextInt(50), i) ? 1 : 0;
        }
        System.out.println("Total unique edges: " + totalUniqueEdges);


        int countedEdges = 0;
        for (SequenceEdge ignored : g) {
            countedEdges++;
        }
        System.out.println("Total iterated edges: " + countedEdges);


        Assert.assertEquals(countedEdges, totalUniqueEdges);
    }

    @Test
    public void testRepeatedSequence(){

        int[][] seqs = new int[][]{
                new int[]{65, 66, 65, 66, 65, 66, 65, 66}
        };

        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(seqs);
        SequenceGraph g = graphs.iterator().next();

        SequenceNode node65 = g.nodes.get(65);
        SequenceEdge edge65to66 = node65.getOutEdge(66);
        Visitations edge65to66Visitors = edge65to66.getVisitors();

        Visitations expected65to66 = new Visitations();
        expected65to66.addVisitor(0, new Range(0,1));
        expected65to66.addVisitor(0, new Range(2,3));
        expected65to66.addVisitor(0, new Range(4,5));
        expected65to66.addVisitor(0, new Range(6,7));

        Assert.assertTrue(edge65to66Visitors.equals(expected65to66));

        //check node66 too
        SequenceNode node66 = g.nodes.get(66);
        SequenceEdge edge66to65 = node66.getOutEdge(65);
        Visitations edge66to65Visitors = edge66to65.getVisitors();

        Visitations expected66to65 = new Visitations();
        expected66to65.addVisitor(0, new Range(1,2));
        expected66to65.addVisitor(0, new Range(3,4));
        expected66to65.addVisitor(0, new Range(5,6));

        Assert.assertTrue(edge66to65Visitors.equals(expected66to65));

    }


    /**
     *        (2)
     *        / \
     *       /   \
     *   [1]/     \[1]
     *     /       \
     *    /   [3]   \     [1]
     *  (1)---------(3)---------(7)
     *    \
     *     \
     *      \[1]
     *       \
     *        \
     *        (4)
     * Note all edges are directed left to right in this diagram.
     */

    private static final int[][] sequences = {
            new int[]{3,7},
            new int[]{1,2,3},
            new int[]{1,4},
            new int[]{1,3},
            new int[]{1,3},
            new int[]{1,3},
    };

    @Test
    public void testMakeWeightedDirectedGraph() throws Exception {
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(sequences);
        //there is one graph
        Assert.assertTrue(graphs.size() == 1);
        SequenceGraph graph = graphs.iterator().next();
        //it has nodes 1,2,3,4,7
        Assert.assertTrue(graph.nodes.size() == 5);
        Assert.assertTrue(graph.nodes.containsKey(1));
        Assert.assertTrue(graph.nodes.containsKey(2));
        Assert.assertTrue(graph.nodes.containsKey(3));
        Assert.assertTrue(graph.nodes.containsKey(4));
        Assert.assertTrue(graph.nodes.containsKey(7));
        //edges are weighted accordingly, 1-3[3], 1-2[1], 1-4[1], 3-7[1]
        Assert.assertTrue(graph.nodes.get(1).getOutEdge(3).getSupport() == 3);
        Assert.assertTrue(graph.nodes.get(2).getOutEdge(3).getSupport() == 1);
        Assert.assertTrue(graph.nodes.get(1).getOutEdge(2).getSupport() == 1);
        Assert.assertTrue(graph.nodes.get(1).getOutEdge(4).getSupport() == 1);
        Assert.assertTrue(graph.nodes.get(3).getOutEdge(7).getSupport() == 1);
    }


}