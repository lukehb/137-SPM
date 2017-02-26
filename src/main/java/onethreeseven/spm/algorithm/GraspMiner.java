package onethreeseven.spm.algorithm;

import onethreeseven.collections.IntArray;
import onethreeseven.collections.IntIterator;
import onethreeseven.spm.model.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

/**
 * Mine the representative sequences from the edges graph.
 * @author Luke Bermingham
 */
public class GraspMiner {

    private int minSup;
    private int maxGap;
    private BitSet processedEdges;

    public Collection<RepSeq> run(SequenceGraph g, int[][] sequences, int minSup, int maxGap){

        ArrayList<RepSeq> out = new ArrayList<>();

        this.minSup = Math.max(1, minSup);
        this.maxGap = Math.max(1, maxGap);
        this.processedEdges = new BitSet();

        for (int i = 0; i < sequences.length; i++) {
            int[] sequence = sequences[i];
            if (sequence.length == 0) {
                continue;
            }

            IntIterator seqIter = new IntIterator(sequence);
            while (seqIter.hasNext()) {
                Path p = getStartingPath(g, seqIter);
                //case: no starting path is possible, go to next sequence
                if (p == null) {
                    break;
                }
                p = expandPath(p, g, seqIter.copy());
                if (p == null) {
                    continue;
                }

                //we don't consider a single edge as a pattern
                if (p.size() > 2) {
                    //process path edges
                    processedEdges.or(p.getEdgeIds());
                    //save path
                    out.add(new RepSeq(
                            p.getCover(),
                            p.visitations.getSupport(),
                            p.visitations,
                            p.getSequence()
                    ));
                }
            }
        }
        return out;
    }

    private Path expandPath(Path p, SequenceGraph g, IntIterator seqIter){
        while(seqIter.hasNext()){
            SequenceEdge nextEdge = getNextEdge(g, seqIter);
            if(nextEdge == null){return p;}
            Visitations candidate = Visitations.tryConnect(p.visitations, nextEdge.getVisitors(), maxGap, minSup);
            int candidateSup = candidate.getSupport();
            //case: the expansion is supported, make this the new path
            if(candidateSup >= minSup){
                ArrayList<SequenceEdge> edges = new ArrayList<>();
                edges.addAll(p.edges);
                edges.add(nextEdge);
                candidate.addComplement(p.visitations);
                p = new Path(edges, candidate);
            }
            //case: it is not supported at all (even by this sequence, so we are done expanding)
            else if(candidateSup == 0){
                return p;
            }
        }
        return p;
    }

    private SequenceNode getNextNode(SequenceGraph g, IntIterator seqIter){
        while(seqIter.hasNext()){
            int nodeId = seqIter.next();
            SequenceNode node = g.nodes.get(nodeId);
            if(node == null){
                if(seqIter.hasNext()){
                    seqIter.next();
                }
                continue;
            }
            return node;
        }
        return null;
    }

    /**
     * Traverse through a sequence and get the next edge which has enough support.
     * @param g The sequence graph to look for the next edge in.
     * @param seqIter The iterator through the current sequence.
     * @return The next edge and its visitors (minus indices already covered).
     */
    private SequenceEdge getNextEdge(SequenceGraph g, IntIterator seqIter){
        while(seqIter.hasNext()){
            SequenceNode curNode = getNextNode(g, seqIter);
            if(curNode == null || !seqIter.hasNext()){return null;}
            SequenceEdge edge = curNode.getOutEdge(seqIter.peek());
            if(edge == null){continue;}
            //case: support not met
            if(edge.getSupport() < minSup ){
                continue;
            }
            return edge;
        }
        return null;
    }

    private Path getStartingPath(SequenceGraph g, IntIterator seqIter){
        while(seqIter.hasNext()){
            SequenceEdge startingEdge = getNextEdge(g, seqIter);
            if(startingEdge == null){return null;}
            //case: already used this edge, don't start with it
            if(processedEdges.get(startingEdge.id)){
                continue;
            }

            IntIterator expansionIter = seqIter.copy();
            for (int i = 0; i < maxGap; i++) {
                if(!expansionIter.hasNext()){break;}
                SequenceEdge nextEdge = getNextEdge(g, expansionIter);
                if(nextEdge == null){break;}

                Visitations merged =
                        Visitations.tryConnect(startingEdge.getVisitors(), nextEdge.getVisitors(), maxGap, minSup);

                if(merged.getSupport() < minSup){
                    continue;
                }

                //case: enough support, make a path
                ArrayList<SequenceEdge> edges = new ArrayList<>();
                edges.add(startingEdge);
                edges.add(nextEdge);
                seqIter.set(expansionIter);
                return new Path(edges, merged);
            }
        }
        return null;
    }

    private class Path{
        private final Visitations visitations;
        private final ArrayList<SequenceEdge> edges;

        Path(ArrayList<SequenceEdge> edges, Visitations visitations){
            this.visitations = visitations;
            this.edges = edges;
        }

        /**
         * @return The number of items in the path.
         */
        int size(){
            return edges.size()+1;
        }

        BitSet getEdgeIds(){
            BitSet edgeIds = new BitSet();
            for (SequenceEdge edge : edges) {
                edgeIds.set(edge.id);
            }
            return edgeIds;
        }

        int[] getSequence(){
            if(edges.isEmpty()){return new int[]{};}
            IntArray arr = new IntArray(edges.size(), false);

            SequenceNode prevNode = null;
            for (SequenceEdge edge : edges) {
                SequenceNode src = edge.source;
                if(prevNode == null || src.id != prevNode.id){
                    arr.add(src.id);
                }
                arr.add(edge.destination.id);
                prevNode = edge.destination;
            }
            return arr.getArray();
        }

        int getCover(){
            return this.visitations.getCover();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path path = (Path) o;
            return edges.equals(path.edges);
        }

        @Override
        public int hashCode() {
            return edges.hashCode();
        }
    }

}
