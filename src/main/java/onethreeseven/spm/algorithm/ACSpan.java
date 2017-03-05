package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;

import java.util.ArrayList;

/**
 * Mines all contiguous sequential patterns.
 * @author Luke Bermingham
 */
public class ACSpan extends AbstractContigousSPM {
    @Override
    protected IPatternClosure getPatternClosure() {
        return (supA, supB) -> false;
    }

    @Override
    protected boolean addToOutput(ArrayList<Integer> pattern, int support, boolean marked) {
        return true;
    }

    @Override
    protected String getPatternClosureSuffix() {
        return "";
    }
}
