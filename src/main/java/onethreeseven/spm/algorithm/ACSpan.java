package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;
import onethreeseven.spm.model.TrieIterator;

import java.util.ArrayList;

/**
 * Mines all contiguous sequential patterns.
 * @author Luke Bermingham
 */
public class ACSpan extends AbstractContiguousSPM {
    @Override
    protected IPatternClosure getPatternClosure() {
        return (supA, supB) -> false;
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
