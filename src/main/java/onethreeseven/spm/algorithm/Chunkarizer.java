package onethreeseven.spm.algorithm;

import gnu.trove.iterator.TIntObjectIterator;
import onethreeseven.spm.model.SequenceEdge;
import onethreeseven.spm.model.SequenceGraph;
import onethreeseven.spm.model.SequenceNode;
import java.util.ArrayList;


/**
 * Mine pattern chunks by finding sequences of graph that do not diverge.
 * @author Luke Bermingham
 */
public class Chunkarizer {

    public void run(SequenceGraph g, int minSup){
        breakDivergingNodes(g, minSup);
    }

    /**
     * Go through each node and remove all its edges if it "diverges" (also remove all non-supported edges).
     * We define a divergent node as one that has >1 supported out-edges.
     * @param g The sequence graph.
     * @param minSup The minimum number of visitors an edge must have to be supported.
     */
    private void breakDivergingNodes(SequenceGraph g, int minSup){
        TIntObjectIterator<SequenceNode> iter = g.nodes.iterator();

        //reuse this list for efficiency
        final ArrayList<SequenceNode> outNodesToRemove = new ArrayList<>();

        while(iter.hasNext()){
            iter.advance();
            SequenceNode n = iter.value();
            //apply criteria to all out edges of node "n"
            int nSupportedEdges = 0;

            for (SequenceEdge outEdge : n.outEdges()) {
                int edgeSupport = outEdge.getSupport();
                //count the supported edges
                if(edgeSupport >= minSup){
                    nSupportedEdges++;
                }
                //case: remove edge if it does not meet the support threshold
                else{
                    outNodesToRemove.add(outEdge.destination);
                }
            }
            //if the node if divergent remove all edges
            if(nSupportedEdges > 1){
                for (SequenceEdge outEdge : n.outEdges()) {
                    outNodesToRemove.add(outEdge.destination);
                }
            }

            //do the actual edge removal
            for (SequenceNode outNodeToRemove : outNodesToRemove) {
                n.removeEdge(outNodeToRemove, false);
            }
            outNodesToRemove.clear();
        }
    }

}
