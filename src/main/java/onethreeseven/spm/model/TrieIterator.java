package onethreeseven.spm.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Pattern iterator for {@link Trie}. It also has facility to check the count of a pattern
 * and whether or not it ends in a marked node.
 * @author Luke Bermingham
 */
public class TrieIterator<T> implements Iterator<ArrayList<T>> {

    private final boolean showSubPatterns;
    private final Trie<T> t;
    private final Iterator<ArrayList<Trie<T>.TrieNode>> pathIter;
    private List<Trie<T>.TrieNode> currentPatten = null;
    private Trie<T>.TrieNode endNode = null;

    TrieIterator(Trie<T> t, boolean showSubPatterns) {
        this.t = t;
        this.showSubPatterns = showSubPatterns;
        this.pathIter = t.getPathIter();
    }

    @Override
    public boolean hasNext() {
        return pathIter.hasNext() || !currentPatten.isEmpty();
    }

    @Override
    public ArrayList<T> next() {
        //we have current pattern
        if (currentPatten != null && !currentPatten.isEmpty()) {
            return nextPattern();
        }
        //we don't so acquire it
        else {
            currentPatten = pathIter.next();
            return nextPattern();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<T> nextPattern() {
        ArrayList<T> pattern = new ArrayList<>(currentPatten.size());
        for (Trie<T>.TrieNode node : currentPatten) {
            pattern.add(node.getValue());
        }
        endNode = currentPatten.remove(currentPatten.size()-1);
        if(!showSubPatterns){
            currentPatten.clear();
        }

        return pattern;
    }

    public T getValue(){ return  endNode != null ? endNode.getValue() : null; }

    public int getCount() {
        return endNode != null ? endNode.getCount() : 0;
    }

    public boolean isMarked() {
        return endNode != null && t.isMarked(endNode);
    }

    public void unMark(){
        if(endNode != null){
            t.unMark(endNode);
        }
    }

    /**
     * Un-parent a node in the current pattern.
     * @param childIdx The index of the node to un-parent.
     */
    public boolean unParent(T[] path, int childIdx){
        if(childIdx == 0){
            throw new IllegalArgumentException("Cannot un-parent first index.");
        }

        if(path.length <= 1){
            throw new IllegalStateException("Cannot un-parent anything from an empty or length-1 pattern.");
        }

        //re-build path if it is null
        List<Trie<T>.TrieNode> pattern = currentPatten;
        if(pattern.isEmpty()){
            pattern = new ArrayList<>();
            Iterator<Trie<T>.TrieNode> nodeIter = t.getSequenceIter(path);
            while(nodeIter.hasNext()){
                pattern.add(nodeIter.next());
            }
        }

        //do the actual removal
        int i = 0;
        int parentIdx = childIdx - 1;
        Trie<T>.TrieNode parent = null;
        for (Trie<T>.TrieNode node : pattern) {
            if(i == parentIdx){
                parent = node;
            }
            else if(i == childIdx && parent != null){
                return parent.removeChild(node);
            }
            i++;
        }
        return false;
    }

}
