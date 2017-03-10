package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;
import onethreeseven.spm.model.Trie;
import onethreeseven.spm.model.TrieIterator;

import java.util.ArrayList;

/**
 * CCSpan: Mining closed contiguous sequential patterns.
 * By Jingsong Zhang, Yinglin Wang and Dingyu Yang.
 * This is our implementation of their algorithm, we use a {@link Trie}
 * to speed up the pattern searching.
 * @author Luke Bermingham
 */
public class CCSpan extends AbstractContiguousSPM {

    @Override
    protected IPatternClosure getPatternClosure() {
        return (supA, supB) -> supA == supB;
    }

    @Override
    protected boolean addToOutput(ArrayList<Integer> pattern, TrieIterator<Integer> patternIter) {
        //if the pattern is not marked we don't want to write it
        if(!patternIter.isMarked()){
            return false;
        }
        else{
            //un-mark the current pattern so we don't process it again
            patternIter.unMark();
            return true;
        }
    }

}
