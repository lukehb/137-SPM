package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.CandidatePattern;
import onethreeseven.spm.model.SequenceEdge;
import onethreeseven.spm.model.SequenceGraph;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Test {@link ProtoMiner}.
 * @author Luke Bermingham
 */
public class ProtoMinerTest {

    private static int[][] seqDb = new int[][]{
            new int[]{1,2,3,4,5},
            new int[]{1,2,3,4,5},
            new int[]{1,2,3,4,6},
            new int[]{1,2,3,4,6},
            new int[]{1,2,3,4,7},
            new int[]{1,2,3,4,7},
            new int[]{1,2,3,4,8},
            new int[]{1,2,3,4,8}
    };

    private static final SequenceGraph g;
    static {
        Collection<SequenceGraph> graphs = SequenceGraph.fromSequences(seqDb);
        g = graphs.iterator().next();
    }

    private static final HashSet<CandidatePattern> expectedPatterns = new HashSet<>();
    static {
        //pattern 1->2->3->4
        ArrayList<SequenceEdge> edges1234 = new ArrayList<>();
        edges1234.add(g.nodes.get(1).getOutEdge(2));
        edges1234.add(g.nodes.get(2).getOutEdge(3));
        edges1234.add(g.nodes.get(3).getOutEdge(4));
        expectedPatterns.add(new CandidatePattern(edges1234, null));
        //pattern 4->5
        ArrayList<SequenceEdge> edges45 = new ArrayList<>();
        edges45.add(g.nodes.get(4).getOutEdge(5));
        expectedPatterns.add(new CandidatePattern(edges45, null));
        //pattern 4->6
        ArrayList<SequenceEdge> edges46 = new ArrayList<>();
        edges46.add(g.nodes.get(4).getOutEdge(6));
        expectedPatterns.add(new CandidatePattern(edges46, null));
        //pattern 4->7
        ArrayList<SequenceEdge> edges47 = new ArrayList<>();
        edges47.add(g.nodes.get(4).getOutEdge(7));
        expectedPatterns.add(new CandidatePattern(edges47, null));
        //pattern 4->8
        ArrayList<SequenceEdge> edges48 = new ArrayList<>();
        edges48.add(g.nodes.get(4).getOutEdge(8));
        expectedPatterns.add(new CandidatePattern(edges48, null));
    }


    @Test
    public void run() throws Exception {

        final int minSup = 2;
        final int minLen = -1;
        Collection<CandidatePattern> patterns = new ProtoMiner().run(seqDb, minSup, minLen);
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