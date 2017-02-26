package onethreeseven.spm.model;

import org.junit.Assert;
import org.junit.Test;
import java.util.Collection;

/**
 * Test the {@link EdgeCoverComparator}.
 * @author Luke Bermingham
 */
public class EdgeCoverComparatorTest {

    private static final SequenceGraph g;
    static {
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(new int[][]{
                new int[]{1,2,3,4,5},
                new int[]{1,2,3,4,6},
                new int[]{1,2,3,4,7},
                new int[]{1,2,3,4,8}
        });
        g = graphs.iterator().next();
    }

    private static final EdgeCoverComparator comparator = new EdgeCoverComparator();

    @Test
    public void compareCoverOnly() throws Exception {
        //check cover edge 1->2
        SequenceEdge edge1to2 = g.nodes.get(1).getOutEdge(2);
        Assert.assertEquals(4, edge1to2.getCover());
        //check cover edge 4->5
        SequenceEdge edge4to5 = g.nodes.get(4).getOutEdge(5);
        Assert.assertEquals(1, edge4to5.getCover());
        //check cover edge 1->2 consider greater than edge 4->5
        Assert.assertTrue(comparator.compare(edge1to2, edge4to5) > 0);
    }

    @Test
    public void compareSourceInDegree() throws Exception {
        //check source in-degree for edge 1->2
        SequenceEdge edge1to2 = g.nodes.get(1).getOutEdge(2);
        Assert.assertEquals(0, edge1to2.source.inDegree());
        //check source in-degree for edge 2->3
        SequenceEdge edge2to3 = g.nodes.get(2).getOutEdge(3);
        Assert.assertEquals(1, edge2to3.source.inDegree());
        //same cover, but check that edge 1->2 is considered greater because of smaller in-degree.
        Assert.assertTrue(comparator.compare(edge1to2, edge2to3) > 0);
    }

}