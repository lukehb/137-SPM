package onethreeseven.spm.data;

import onethreeseven.common.data.AbstractWriter;
import onethreeseven.spm.model.SequenceEdge;
import onethreeseven.spm.model.SequenceGraph;
import onethreeseven.spm.model.SequenceNode;
import onethreeseven.spm.model.Visitations;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;

/**
 * Writes each sequence in the sequence graph to a file with support.
 * @author Luke Bermingham
 */
public class ChunkedSequenceGraphWriter extends AbstractWriter<SequenceGraph> {
    @Override
    protected void write(BufferedWriter bw, SequenceGraph g) throws IOException {
        //traverse it in a dfs way starting at any nodes with no in edges.
        //not the best because it doesn't support closed cycles

        Visitations v = null;
        ArrayDeque<SequenceNode> chunk = new ArrayDeque<>();

        for (SequenceNode n : g.nodes.valueCollection()) {
            //haven't check this node yet and it has no in-edges
            if(n.inEdges().isEmpty()){
                while(n.outDegree() == 1){
                    //get the edge to the next node
                    SequenceEdge outEdge = n.outEdges().iterator().next();
                    if(v == null){
                        v = new Visitations(outEdge.getVisitors());
                    }else{
                        v = Visitations.tryConnectTouching(v, outEdge.getVisitors());
                    }
                    //make sure it has greater than 0 support
                    if(v.getSupport() == 0){break;}
                    //add the current node to the chunk
                    chunk.add(n);
                    //make this the new current node
                    n = outEdge.destination;
                }
                //do the actual write
                if(v != null){
                    writeChunk(bw, chunk, v.getSupport());
                    v = null;
                    chunk.clear();
                }
            }
        }
    }

    private void writeChunk(BufferedWriter bw, Collection<SequenceNode> chunk, int sup) throws IOException {
        //write the chunk
        for (Iterator<SequenceNode> iterator = chunk.iterator(); iterator.hasNext(); ) {
            SequenceNode chunkNode = iterator.next();
            bw.write(String.valueOf(chunkNode.id));
            if(iterator.hasNext()){
                bw.write(delimiter);
            }
        }
        bw.write(" #SUP:");
        bw.write(sup);
        bw.newLine();
    }

}
