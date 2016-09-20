package onethreeseven.spm.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Pattern iterator for {@link Trie}. It also has facility to check the count of a pattern
 * and whether or not it ends in a marked node.
 * @author Luke Bermingham
 */
public class TrieIterator<T> implements Iterator<ArrayList<T>> {

    private final boolean showSubPatterns;
    private final Trie<T> t;
    private final Iterator<ArrayDeque<Trie<T>.TrieNode>> pathIter;
    private ArrayDeque<Trie<T>.TrieNode> currentPatten = null;
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
        endNode = currentPatten.pollLast();
        if(!showSubPatterns){
            currentPatten.clear();
        }

        return pattern;
    }

    public int getCount() {
        return endNode != null ? endNode.getCount() : 0;
    }

    public boolean isMarked() {
        return endNode != null && t.isMarked(endNode);
    }

}
