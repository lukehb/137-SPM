package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.CandidatePattern;
import onethreeseven.spm.model.SequenceEdge;
import onethreeseven.spm.model.SequenceGraph;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;

/**
 * Test {@link ProtoMiner}.
 * @author Luke Bermingham
 */
public class ProtoMinerTest {

    private static final SequenceGraph g;
    static {
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(new int[][]{
                new int[]{1,2,3,4,5},
                new int[]{1,2,3,4,5},
                new int[]{1,2,3,4,6},
                new int[]{1,2,3,4,6},
                new int[]{1,2,3,4,7},
                new int[]{1,2,3,4,7},
                new int[]{1,2,3,4,8},
                new int[]{1,2,3,4,8}
        });
        g = graphs.iterator().next();
    }

    private static final HashSet<CandidatePattern> expectedPatterns = new HashSet<>();
    static {
        //pattern 1->2->3->4
        ArrayDeque<SequenceEdge> edges1234 = new ArrayDeque<>();
        edges1234.add(g.nodes.get(1).getOutEdge(2));
        edges1234.add(g.nodes.get(2).getOutEdge(3));
        edges1234.add(g.nodes.get(3).getOutEdge(4));
        expectedPatterns.add(new CandidatePattern(edges1234, null, null));
        //pattern 4->5
        ArrayDeque<SequenceEdge> edges45 = new ArrayDeque<>();
        edges45.add(g.nodes.get(4).getOutEdge(5));
        expectedPatterns.add(new CandidatePattern(edges45, null, null));
        //pattern 4->6
        ArrayDeque<SequenceEdge> edges46 = new ArrayDeque<>();
        edges46.add(g.nodes.get(4).getOutEdge(6));
        expectedPatterns.add(new CandidatePattern(edges46, null, null));
        //pattern 4->7
        ArrayDeque<SequenceEdge> edges47 = new ArrayDeque<>();
        edges47.add(g.nodes.get(4).getOutEdge(7));
        expectedPatterns.add(new CandidatePattern(edges47, null, null));
        //pattern 4->8
        ArrayDeque<SequenceEdge> edges48 = new ArrayDeque<>();
        edges48.add(g.nodes.get(4).getOutEdge(8));
        expectedPatterns.add(new CandidatePattern(edges48, null, null));
    }


    @Test
    public void run() throws Exception {

        final int minSup = 2;
        Collection<CandidatePattern> patterns = new ProtoMiner().run(g, minSup);
        System.out.println("Done mining patterns.");

        for (CandidatePattern pattern : patterns) {
            boolean wasInExpectedOutput = expectedPatterns.contains(pattern);
            System.out.println("Pattern: " + pattern.toString()
                    + " {was expected: "
                    + (wasInExpectedOutput ? "T}" : "F}"));
            Assert.assertTrue(wasInExpectedOutput);
        }


    }

}