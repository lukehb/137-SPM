package onethreeseven.spm.model;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A linked sequence structure that facilities fast sub-sequence checking.
 * @author Luke Bermingham
 */
public class LookupSequence {


    private final HashMap<Integer, Node> headerTable;
    private final int[] sequence;
    private int nullId = -1;
    private int size;

    public LookupSequence(int[] sequence) {
        this.sequence = sequence;
        this.size = sequence.length;
        this.headerTable = new HashMap<>();
        //build the header table, node links and jump links
        processNodes(sequence);
    }

    public Integer[] getSymbolUniverse(){
        return headerTable.keySet().toArray(new Integer[headerTable.size()]);
    }

    private void processNodes(int... sequence){
        for (int i = 0; i < sequence.length; i++) {
            int symbol = sequence[i];
            Node node = new Node(i, symbol);
            Node headerNode = headerTable.get(node.symbol);
            if (headerNode == null) {
                this.headerTable.put(node.symbol, node);
            }
            //header node already exists, link this node as a jump node
            else {
                while (headerNode.jump != null) {
                    headerNode = headerNode.jump;
                }
                headerNode.jump = node;
            }
        }

        //check if our null index is okay still
        while(headerTable.containsKey(nullId)){
            nullId--;
        }

    }

    /**
     * Much like a {@link java.util.BitSet} nodes aren't removed from a {@link LookupSequence};
     * instead the index of the node is set to a special "null" index to indicate it has been cleared.
     * @param symbol The symbol to clear (the lookup will be used to find all occurrences of this symbol).
     */
    public void clear(int symbol){
        Node curNode = headerTable.get(symbol);
        while(curNode != null){
            size--;
            //clear the symbol by using the special "null" id
            this.sequence[curNode.index] = nullId;
            curNode = curNode.jump;
        }
        headerTable.remove(symbol);
    }

    /**
     * Clear the indices of this symbol sequence
     * @param symbolSequence the contiguous sub-sequence to clear
     * @return whether the clear had any effect
     */
    public boolean clear(int[] symbolSequence){
        int[] indices = getIndicesOf(symbolSequence);
        if(indices == null){
            return false;
        }
        for (int index : indices) {
            this.sequence[index] = nullId;
            size--;
        }
        return true;
    }

    /**
     * @return An int sequence of all symbols not set to the special "null" id.
     */
    public int[] getActiveSequence(){
        int[] seq = new int[size];
        int i = 0;
        for (int symbol : this.sequence) {
            if(symbol != nullId){
                seq[i] = symbol;
                i++;
            }
        }
        return seq;
    }

    /**
     * Get the indices of the first occurrence on this contiguous sub-sequence.
     * @param contiguousSequence the sub-sequence to find
     * @return the indices of the sub-sequence
     */
    public int[] getIndicesOf(int[] contiguousSequence){
        if(contiguousSequence.length == 0){
            return null;
        }

        int[] indices = new int[contiguousSequence.length];
        Arrays.fill(indices, -1);

        Node startNode = headerTable.get(contiguousSequence[0]);
        while(startNode != null){
            int startIdx = startNode.index;
            if(this.sequence[startIdx] != nullId){
                indices[0] = startIdx;
                for (int i = 1; i < contiguousSequence.length; i++) {
                    int seqIdx = startIdx + i;
                    if(seqIdx > this.sequence.length-1 || this.sequence[seqIdx] != contiguousSequence[i]){
                        break;
                    }
                    indices[i] = seqIdx;
                }
                if(indices[contiguousSequence.length-1] != -1){
                    return indices;
                }
            }
            startNode = startNode.jump;
        }

        return null;
    }

    /**
     * @param queryWithGaps A query sub-sequence (with gaps allowed)
     * @return Whether or not this lookup sequence contains the query sub-sequence in some form.
     *         Note: The query sub-sequence does not have to be contiguous.
     */
    public boolean contains(final int[] queryWithGaps){
        int curIndex = -1;
        for (int symbol : queryWithGaps) {
            Node cur = headerTable.get(symbol);
            if(cur == null){return false;}
            Integer lowestSymbolIndex = null;
            while(cur != null){
                if(cur.index > curIndex){
                    lowestSymbolIndex = cur.index;
                    break;
                }
                cur = cur.jump;
            }
            if(lowestSymbolIndex == null){return false;}
            curIndex = lowestSymbolIndex;
        }
        return curIndex > -1;
    }

    public int size(){
        return size;
    }


    private class Node {
        private final int symbol;
        private final int index;
        private Node jump = null;

        Node(int index, int symbol) {
            this.index = index;
            this.symbol = symbol;
        }
    }

}
