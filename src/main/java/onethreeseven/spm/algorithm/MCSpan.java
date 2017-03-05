package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;

import java.util.ArrayList;

/**
 * Mines max-contiguous sequential patterns.
 * @author Luke Bermingham
 */
public class MCSpan extends AbstractContigousSPM {
    @Override
    protected IPatternClosure getPatternClosure() {
        return (supA, supB) -> true;
    }

    @Override
    protected boolean addToOutput(ArrayList<Integer> pattern, int support, boolean marked) {
        return marked;
    }

    @Override
    protected String getPatternClosureSuffix() {
        return " [MAX] ";
    }
}
