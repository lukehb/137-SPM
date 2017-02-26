package onethreeseven.spm.model;

import java.util.Comparator;

/**
 * Compare edges based on cover, if cover is a tie compare the in-degree of the source nodes,
 * if that is a tie compare the numerical ids.
 * @author Luke Bermingham
 */
public class EdgeCoverComparator implements Comparator<SequenceEdge> {
    @Override
    public int compare(SequenceEdge edge1, SequenceEdge edge2) {
        int edgeCover1 = edge1.getCover();
        int edgeCover2 = edge2.getCover();
        //compare edge cover
        if (edgeCover1 == edgeCover2) {
            int nInEdgesSrc1 = edge1.source.inDegree();
            int nInEdgesSrc2 = edge2.source.inDegree();
            //compare the number of in-edges on the source nodes
            if (nInEdgesSrc1 == nInEdgesSrc2) {
                //finally resort to breaking the tie using the id
                return edge1.id - edge2.id;
            }
            return nInEdgesSrc2 - nInEdgesSrc1;
        }
        return edgeCover1 - edgeCover2;
    }
}
