package onethreeseven.spm.model;

import java.util.*;

/**
 * A tree-based structure which is useful for sequence searching and membership.
 * See: https://en.wikipedia.org/wiki/Trie
 * @author Luke Bermingham
 */
public class Trie<T> {

    private static int idGen = 0;

    private final TrieNode rootNode;
    private final BitSet lockedNodes = new BitSet();
    private final BitSet markedNodes = new BitSet();

    public Trie(){
        this.rootNode = createNode(null);
    }

    public boolean add(T[] sequence){
        return add(sequence, sequence.length, false, false);
    }

    /**
     * Add the given sequence to the Trie - may create new nodes.
     * Note: Adding the same sequence will just increase the count of the last node (assuming it isn't locked).
     * @param sequence The sequence to add.
     * @param maxNewNodes A maximum number of new nodes can be specified, if more than this number of new nodes has
     *                    to be created to insert the sequence, then the insertion fails.
     * @param lockLastNode Locking a node prevents its count from being increased until unlocked.
     *                     If the sequence ends in an already existing locked node then insertion fails.
     * @param markLastNode Nodes can be marked or unmarked regardless of locking.
     * @return Whether the count of the nodes in the path increased (this includes making new nodes).
     */
    public boolean add(T[] sequence, int maxNewNodes, boolean lockLastNode, boolean markLastNode){

        if(sequence.length > 0){

            ArrayList<TrieNode> path = new ArrayList<>(sequence.length);
            TrieNode curNode = rootNode;
            int nodesProcessed = 0;
            Iterator<TrieNode> iter = getSequenceIter(sequence);

            while(iter.hasNext()){
                TrieNode nextNode = iter.next();
                if(nextNode != null){
                    path.add(nextNode);
                    curNode = nextNode;
                    nodesProcessed++;
                }
            }

            //case: the path already existed, we may need to increase the count at each of its nodes
            if(nodesProcessed == sequence.length){
                TrieNode endNode = path.get(path.size()-1);
                //case: we processed a path that ends in an already existing locked node, can't increase count
                if(isLocked(endNode)){
                    return false;
                }
                //case: the path is not locked, increase last node count
                else{
                    endNode.count++;
                    if(lockLastNode){lock(endNode);}
                    if(markLastNode){mark(endNode);}
                    return true;
                }
            }

            //case: we have some sequence remainder that needs new nodes
            if(nodesProcessed < sequence.length){

                //case: too many new nodes would be required
                if(sequence.length - nodesProcessed > maxNewNodes){
                    return false;
                }

                for (; nodesProcessed < sequence.length; nodesProcessed++) {
                    TrieNode newNode = createNode(sequence[nodesProcessed]);
                    curNode.children.add(newNode);
                    if(nodesProcessed + 1 == sequence.length){
                        if(lockLastNode){lock(newNode);}
                        if(markLastNode){mark(newNode);}
                    }
                    path.add(newNode);
                    curNode = newNode;
                }
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty(){
        return rootNode.children.isEmpty();
    }

    /**
     * Removes a node from the root node.
     * @param item The node to remove.
     * @return True if the node was a child of the root node, otherwise false.
     */
    public boolean remove1stLevelNode(T item) {
        TrieNode childToRemove = null;
        for (TrieNode child : rootNode.children) {
            if (child.getValue().equals(item)) {
                childToRemove = child;
                break;
            }
        }
        return childToRemove != null && this.rootNode.removeChild(childToRemove);
    }

    /**
     * Gets the frequency of the last node in a given sequence (the whole sequence must exist though).
     * @param sequence the sequence to query for.
     * @return The count for the sequence at the last node, or 0 if it didn't exist in the Trie.
     */
    public int getFrequencyOf(T[] sequence){
        Iterator<TrieNode> iter = getSequenceIter(sequence);
        int i = 0;
        TrieNode endNode = null;
        while(iter.hasNext()){
            i++;
            endNode = iter.next();
        }
        if(endNode != null && i == sequence.length){
            return endNode.count;
        }
        return 0;
    }

    /**
     * Sets the state of all nodes in the Trie to unlocked.
     */
    public void unlockAll() {
        lockedNodes.clear();
    }

    /**
     * Checks that the end node is the new sequence meets the required support
     * and if it doesn't then remove it from being stored.
     * @param sequence The sequence to test.
     * @param minSup The minimum support to meet.
     * @return True if it exists with minimum support.
     */
    public boolean supersede(T[] sequence, int minSup){
        ArrayList<TrieNode> path = new ArrayList<>(sequence.length);
        Iterator<TrieNode> iter = getSequenceIter(sequence);

        while(iter.hasNext()){
            TrieNode node = iter.next();
            if(node != null){
                path.add(node);
            }
        }

        if(path.size() == sequence.length) {
            TrieNode endNode = path.get(path.size() - 1);
            TrieNode parent = (path.size() == 1) ? rootNode : path.get(path.size() - 2);
            //case: did not meet support requirement, remove it
            if (endNode.count < minSup) {
                parent.children.remove(endNode);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks whether this sequence exists, whether it meets a minimum count support,
     * and if it does then un-mark its pre/post. However, if it exists but doesn't meet the support
     * remove it.
     * @param sequence The candidate sequence.
     * @param minSup The minimum required support count.
     * @param patternClosure The rule used to discard patterns.
     * @return True if it exists with minimum support.
     */
    public boolean supersede(T[] sequence, int minSup, IPatternClosure patternClosure){
        ArrayList<TrieNode> path = new ArrayList<>(sequence.length);
        Iterator<TrieNode> iter = getSequenceIter(sequence);

        while(iter.hasNext()){
            TrieNode node = iter.next();
            if(node != null){
                path.add(node);
            }
        }

        //if the nodes processed falls short, then the sequence doesn't even exist in the Trie
        if(path.size() == sequence.length){
            TrieNode endNode = path.get(path.size()-1);
            TrieNode parent = (path.size() == 1) ? rootNode : path.get(path.size()-2);
            //case: did not meet support requirement, remove it
            if(endNode.count < minSup){
                parent.children.remove(endNode);
            }
            //met the requirement, check to un-mark parent
            else{
                //unMark pre-sequence if necessary
                if(isMarked(parent)){
                    //same count means we can un-mark parent
                    if(patternClosure.discard(parent.count, endNode.count)){
                        unMark(parent);
                    }
                }
                //unMark post-sequence if necessary
                {
                    TrieNode curNode = rootNode;
                    //note starting i = 1, goes down a different branch
                    for (int i = 1; i < sequence.length; i++) {
                        T curVal = sequence[i];
                        boolean foundMatch = false;
                        for (TrieNode child : curNode.children) {
                            if(child.value.equals(curVal)){
                                curNode = child;
                                foundMatch = true;
                                break;
                            }
                        }
                        if(!foundMatch){
                            break;
                        }
                        else if(i == sequence.length - 1 && patternClosure.discard(curNode.getCount(), endNode.getCount())){
                            if(isMarked(curNode)){
                                unMark(curNode);
                            }
                        }
                    }
                    mark(endNode);
                }


                return true;
            }
        }
        return false;
    }

    /**
     * @param showSubPatterns Do you want sub-patterns or not?
     * @return An iterator for all the paths (and their subsets) in this Trie.
     */
    public TrieIterator<T> getPatternIterator(boolean showSubPatterns){
        return new TrieIterator<>(this, showSubPatterns);
    }

    Iterator<TrieNode> getSequenceIter(T[] sequence){
        return new Iterator<TrieNode>() {

            TrieNode curNode = rootNode;
            int sequenceOffset = 0;

            @Override
            public boolean hasNext() {
                return sequenceOffset < sequence.length && curNode != null;
            }

            @Override
            public TrieNode next() {
                TrieNode matched = null;
                for (TrieNode child : curNode.children) {
                    if(child.value.equals(sequence[sequenceOffset])){
                        matched = child;
                        break;
                    }
                }
                //case: there was a node match for the current symbol, look for another
                if(matched != null){
                    sequenceOffset++;
                    curNode = matched;
                    return matched;
                }
                //case: no matching node could be found for the current symbol
                else{
                    curNode = null;
                    sequenceOffset = sequence.length;
                    return null;
                }
            }
        };
    }

    /**
     * @return An iterator for each unique path in the Trie.
     */
    Iterator<ArrayList<TrieNode>> getPathIter(){

        final ArrayList<TrieNode> path = new ArrayList<>();
        path.add(rootNode);
        //a node if considered visited once all of its children
        //have been visited. This means nodes with no children are visited
        //once we have traversed to them.
        final BitSet visited = new BitSet();

        return new Iterator<ArrayList<TrieNode>>() {

            @Override
            public boolean hasNext() {
                return !visited.get(rootNode.id);
            }

            @Override
            @SuppressWarnings("unchecked")
            public ArrayList<TrieNode> next() {

                TrieNode endNode = path.get(path.size()-1);

                //traverse up the tree if we have already processed this node
                while(!path.isEmpty()){
                    boolean isVisited = visited.get(endNode.id);
                    if(isVisited){
                        path.remove(path.size()-1); //poll
                        endNode = path.get(path.size()-1); //peek last
                    }
                    //found and unvisited node, break to process it
                    else{
                        break;
                    }
                }

                if(path.isEmpty()){
                    throw new NoSuchElementException("There is no paths left to traverse.");
                }

                //we have an unvisited node now, traverse down its unvisited children
                while(!endNode.children.isEmpty()){
                    for (TrieNode child : endNode.children) {
                        if(!visited.get(child.id)){
                            path.add(child);
                            endNode = child;
                            break;
                        }
                    }
                }

                //at this point we have an unvisited node that has no unvisited children
                visited.set(endNode.id);

                //check parents to make sure have not become "visited" by proxy because we made this child visited

                //note: the "-2" because we have just processed the end node above
                for (int i = path.size() - 2; i >= 0; i--) {
                    TrieNode pathNode = path.get(i);
                    //check path node that is has unvisited children
                    if(pathNode.children.isEmpty()){
                        visited.set(pathNode.id);
                    }
                    //check children
                    else{
                        boolean allChildrenVisited = true;
                        for (TrieNode child : pathNode.children) {
                            if(!visited.get(child.id)){
                                allChildrenVisited = false;
                                break;
                            }
                        }
                        if(allChildrenVisited){
                            visited.set(pathNode.id);
                        }
                        else{
                            break;
                        }
                    }
                }

                //copy path and remove first(root) node
                return new ArrayList<>(path.subList(1, path.size()));
            }
        };
    }

    void unMark(TrieNode node){
        this.markedNodes.clear(node.id);
    }

    void lock(TrieNode node){
        this.lockedNodes.set(node.id);
    }

    void mark(TrieNode node){
        this.markedNodes.set(node.id);
    }

    boolean isMarked(TrieNode node){
        return markedNodes.get(node.id);
    }

    protected TrieNode createNode(T value){
        return new TrieNode(value);
    }

    private boolean isLocked(TrieNode node){
        return lockedNodes.get(node.id);
    }

    /**
     * Inner node class
     */
    class TrieNode{

        private final ArrayList<TrieNode> children;
        private final int id;
        //implicitly there is no empty nodes in this Trie model
        private int count = 1;
        private final T value;

        TrieNode(T value){
            this.id = idGen++;
            this.value = value;
            this.children = new ArrayList<>(0);
        }

        public boolean removeChild(TrieNode child){
            return this.children.remove(child);
        }

        public int getCount() {
            return count;
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "TrieNode{value=" + value +
                    "|count=" + count +
                    (markedNodes.get(id) ? "|marked" : "|unmarked") +
                    (lockedNodes.get(id) ? "|locked}" : "|unlocked}");
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrieNode trieNode = (TrieNode) o;
            return id == trieNode.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

}
