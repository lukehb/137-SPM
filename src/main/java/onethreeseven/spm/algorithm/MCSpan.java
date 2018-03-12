package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;
import onethreeseven.spm.model.TrieIterator;

import java.util.ArrayList;

/**
 * Mines max-contiguous sequential patterns.
 * @author Luke Bermingham
 */
public class MCSpan extends AbstractContiguousSPM {
    @Override
    protected IPatternClosure getPatternClosure() {
        return (supA, supB) -> true;
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

    @Override
    public String toString() {
        return getSimpleName() + "(" + getPatternType() + ")";
    }

    @Override
    public String getSimpleName() {
        return "mcspan";
    }

    @Override
    public String getPatternType() {
        return "max contiguous";
    }
}
