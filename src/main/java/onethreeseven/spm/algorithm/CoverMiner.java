package onethreeseven.spm.algorithm;

import onethreeseven.spm.data.SequentialPatternWriter;
import onethreeseven.spm.model.CoverMap;
import onethreeseven.spm.model.CoveredSequentialPattern;
import onethreeseven.spm.model.Trie;
import onethreeseven.spm.model.TrieIterator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Makes a {@link onethreeseven.spm.model.Trie}
 * and assigns cover to every node.
 * @author Luke Bermingham
 */
public class CoverMiner {

    private static final Logger log = Logger.getLogger(CoverMiner.class.getSimpleName());

    private interface PatternProcessor{
        void process(CoveredSequentialPattern pattern);
    }

    /**
     * Mines covered contiguous sequential patterns from the sequence database
     * and writes them to a file.
     * @param sequences The sequence database.
     * @param minSup The minimum requires support.
     * @param outFile The file to write the patterns to.
     */
    public void run(int[][] sequences, int minSup, File outFile){
        SequentialPatternWriter writer = new SequentialPatternWriter(outFile);
        run(sequences, minSup, writer::write);
        writer.close();
    }

    /**
     * Mines covered contiguous sequential patterns from the sequence database
     * and returns them in a collection.
     * @param sequences The sequence database.
     * @param minSup The minimum requires support.
     */
    public Collection<CoveredSequentialPattern> run(int[][] sequences, int minSup){
        final Collection<CoveredSequentialPattern> out = new ArrayList<>();
        run(sequences, minSup, out::add);
        return out;
    }

    private void run(int[][] sequences, int minSup, PatternProcessor processor){

        //build a trie with custom values
        Trie<CoveredItem> t = populateTrie(sequences, minSup);

        log.info("Mining patterns");
        while(!t.isEmpty()){
            //visit all the paths in the Trie and find the most covered path
            CoveredSequentialPattern mostCoveredPath = getMostCoveredPath(t);
            //store the path
            processor.process(mostCoveredPath);
            //now remove it from the trie and do this until trie is empty
            remove(t, mostCoveredPath);
        }
    }

    private boolean remove(Trie<CoveredItem> t, CoveredSequentialPattern toRemove){

        //special case: a pattern of size 1
        if(toRemove.size() == 1){
            t.remove1stLevelNode(new CoveredItem(toRemove.getCover(), toRemove.getSequence()[0]));
            return true;
        }

        //break pattern down into all length-2 pairs and iterate the Trie
        //removing all occurrences. the alternative is storing a separate set
        //of processed edges and checking that when the covered paths is scanned for
        int[] seq = toRemove.getSequence();
        int nPairs = seq.length - 1;
        int[][] pairs = new int[nPairs][2];
        for (int i = 0, startIdx = 0, endIdx = 1; i < nPairs; i++, startIdx++, endIdx++) {
            pairs[i] = new int[]{seq[startIdx], seq[endIdx]};
        }

        boolean removedSomething = false;

        TrieIterator<CoveredItem> iter = t.getPatternIterator(false);
        while(iter.hasNext()){
            List<CoveredItem> path = iter.next();

            if(path.size() <= 1){
                continue;
            }

            //check whether path has any of these pairs
            for (int pairIdx = 0; pairIdx < pairs.length && path.size() > 1; pairIdx++) {
                int[] itemPair = pairs[pairIdx];

                int itemA = itemPair[0];
                int itemB = itemPair[1];

                //check for a matching item pair and if found un-parent it and break
                for (int i = 0; i < path.size() - 1; i++) {
                    if (itemA == path.get(i).item && itemB == path.get(i + 1).item) {
                        boolean unParented = iter.unParent(path.toArray(new CoveredItem[path.size()]), i + 1);
                        if (unParented) {
                            path = path.subList(0, i + 1);
                            removedSomething = true;
                            break;
                        }
                    }
                }
            }
        }
        return removedSomething;
    }

    private CoveredSequentialPattern getMostCoveredPath(Trie<CoveredItem> t){
        int bestCover = -1;
        //store the support and the covered path
        CoveredSequentialPattern bestPattern = null;

        TrieIterator<CoveredItem> pathIter = t.getPatternIterator(false);

        while(pathIter.hasNext()){
            ArrayList<CoveredItem> path = pathIter.next();
            int[] sequence = new int[path.size()];
            for (int i = 0; i < path.size(); i++) {
                CoveredItem coverItem = path.get(i);
                sequence[i] = coverItem.item;
            }
            int support = pathIter.getCount();
            int cover = pathIter.getValue().cover;
            if(cover > bestCover){
                bestCover = cover;
                bestPattern = new CoveredSequentialPattern(sequence, support, cover);
            }
            else if(cover == bestCover && bestPattern != null && support > bestPattern.getSupport()){
                bestPattern = new CoveredSequentialPattern(sequence, support, cover);
            }
        }

        return bestPattern;
    }

    private Trie<CoveredItem> populateTrie(int[][] sequences, int minSup){

        log.info("Making cover map");
        CoverMap coverMap = new CoverMap(sequences);


        log.info("Adding sequences to trie");
        final Trie<CoveredItem> t = new Trie<>();
        int k = 1;
        while(addLengthKPatterns(t, k, minSup, sequences, coverMap) > 0){
            k++;
        }
        return t;
    }

    /**
     * Given sequences find all length-k patterns that meet the minimum support.
     * @param t The Trie used to store/check support of the patterns.
     * @param k The size of the patterns that should be generated from the sequences.
     * @param minSup The minimum support.
     * @param db The sequences to split.
     * @return the number of length-k potential contiguous sub-sequences added.
     */
    private int addLengthKPatterns(Trie<CoveredItem> t, int k, int minSup, int[][] db, CoverMap coverMap){

        ArrayList<CoveredItem[]> candidates = new ArrayList<>();

        for (int[] sequence : db) {
            if(sequence.length < k){
                continue;
            }
            //sliding window of length-k over the sequence to extract candidates
            int startIdx = 0;
            int endIdx = startIdx + k;
            while(endIdx <= sequence.length){
                final CoveredItem[] candidate = new CoveredItem[k];
                //make the candidate
                {
                    int cover = 0;
                    for (int i = startIdx, j = 0; i < endIdx; i++, j++) {
                        int item = sequence[i];
                        cover += (j == 0) ? 0 : coverMap.getCover(sequence[i-1], item);
                        candidate[j] = new CoveredItem(cover, item);
                    }
                    candidates.add(candidate);
                    startIdx++;
                    endIdx++;
                }
                //check for post-sequence and try to add new candidate if found
                if(k > 1){
                    CoveredItem[] post = new CoveredItem[k-1];
                    System.arraycopy(candidate, 1, post, 0, k-1);
                    if(t.getFrequencyOf(post) > 0){
                        if(t.add(candidate, 1, true, false)){
                            candidates.add(candidate);
                        }
                    }
                }
                //a length-1 pattern, no need for pre-post check
                else{
                    if(t.add(candidate, k, true, false)){
                        candidates.add(candidate);
                    }
                }

            }
            //as patterns are added they are locked so that the same pattern can't be counted twice for
            //the same sequence, however, once a sequence has been processed we unlock the the patterns
            //so that new sequences can increase the count for that pattern
            t.unlockAll();
        }

        //now check that candidates are valid (using minimum support count)
        int potentialPatterns = 0;
        for (CoveredItem[] candidate : candidates) {
            if(t.supersede(candidate, minSup)){
                potentialPatterns++;
            }
        }
        return potentialPatterns;
    }


    private class CoveredItem{
        final int cover;
        final int item;

        public CoveredItem(int cover, int item) {
            this.cover = cover;
            this.item = item;
        }

        //Note: equals() and hashcode() only use the item not the cover.

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoveredItem that = (CoveredItem) o;
            return item == that.item;
        }

        @Override
        public int hashCode() {
            return item;
        }

        @Override
        public String toString() {
            return item + "|cover=" + cover;
        }
    }


}
