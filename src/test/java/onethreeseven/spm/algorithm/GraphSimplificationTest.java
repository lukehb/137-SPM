package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.SequenceGraph;
import onethreeseven.spm.model.SequenceNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

/**
 * Test our {@link GraphSimplification} algorithms.
 * @author Luke Bermingham
 */
public class GraphSimplificationTest {

    @Test
    public void runLossless() throws Exception {

        final int minSup = 3;

        int[] supportedNodes = new int[]{1,2,3,6};

        int[][] supportedEdges = new int[][]{
                new int[]{1,2},
                new int[]{2,3}
        };

        int[][] sdb = new int[][]{
                new int[]{1,2,3,4,5,6},
                new int[]{1,1,2,6,5,3},
                new int[]{6,1,2,3,3}
        };

        GraphSimplification algo = new GraphSimplification();
        List<SequenceGraph> simplifiedGraphs = algo.runLossless(sdb, minSup);

        for (int supportedNode : supportedNodes) {
            Assert.assertTrue(graphsContainNode(simplifiedGraphs, supportedNode));
        }

        for (int[] supportedEdge : supportedEdges) {
            Assert.assertTrue(graphsContainEdge(simplifiedGraphs, supportedEdge));
        }

    }

    private boolean graphsContainNode(Collection<SequenceGraph> graphs, int nodeId){
        for (SequenceGraph graph : graphs) {
            if(graph.nodes.containsKey(nodeId)){
                return true;
            }
        }
        return false;
    }

    private boolean graphsContainEdge(Collection<SequenceGraph> graphs, int[] edge){
        for (SequenceGraph graph : graphs) {
            SequenceNode src = graph.nodes.get(edge[0]);
            if(src == null){continue;}
            if(src.getOutEdge(edge[1]) != null){
                return true;
            }
        }
        return false;
    }

}