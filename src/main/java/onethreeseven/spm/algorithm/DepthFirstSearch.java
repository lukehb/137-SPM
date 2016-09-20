package onethreeseven.spm.algorithm;



import onethreeseven.spm.model.SequenceEdge;
import onethreeseven.spm.model.SequenceNode;
import java.util.BitSet;
import java.util.Stack;

/**
 * Performs a depth first search using a starting {@link SequenceNode}
 * @author Luke Bermingham
 */

public abstract class DepthFirstSearch {

    private BitSet alreadyAdded = new BitSet();

    public void search(SequenceNode startNode){
        Stack<SequenceNode> toSearch = new Stack<>();

        toSearch.add(startNode);
        alreadyAdded.set(startNode.id);

        while(!toSearch.isEmpty()){
            SequenceNode graphNode = toSearch.pop();
            processNode(graphNode);
            if(!keepSearching(graphNode)){
                break;
            }
            //add cur nodes neighbours to the search if we haven't already
            for (SequenceEdge edge : graphNode) {
                if(!alreadyAdded.get(edge.destination.id)){
                    toSearch.add(edge.destination);
                    alreadyAdded.set(edge.destination.id);
                }
            }
        }
    }

    protected abstract void processNode(SequenceNode node);
    protected abstract boolean keepSearching(SequenceNode node);
}
