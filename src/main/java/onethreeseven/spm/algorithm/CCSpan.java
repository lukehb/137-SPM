package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.IPatternClosure;
import onethreeseven.spm.model.Trie;
import onethreeseven.spm.model.TrieIterator;
import onethreeseven.spm.model.ContiguousSubSeqIterator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * CCSpan: Mining closed contiguous sequential patterns.
 * By Jingsong Zhang, Yinglin Wang and Dingyu Yang.
 * This is our implementation of their algorithm, we use a {@link Trie}
 * to speed up the pattern searching.
 * @author Luke Bermingham
 */
public class CCSpan extends AbstractContigousSPM{

    @Override
    protected IPatternClosure getPatternClosure() {
        return (supA, supB) -> supA == supB;
    }

    @Override
    protected boolean addToOutput(ArrayList<Integer> pattern, int support, boolean marked) {
        return marked;
    }

    @Override
    protected String getPatternClosureSuffix() {
        return " [CLOSED] ";
    }
}
