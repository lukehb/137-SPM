package onethreeseven.spm.algorithm;

import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoPrefixSpan;
import java.io.File;
import java.io.IOException;

/**
 * A wrapper around SPMF's Prefix span {@link AlgoPrefixSpan}.
 * @author Luke Bermingham
 */
public class PrefixSpanWrapper extends SPMFAlgoWrapper {

    @Override
    public void run(SPMParameters parameters, File outFile) {
        AlgoPrefixSpan algo = new AlgoPrefixSpan();
        try {
            algo.runAlgorithm(parameters.getSpmfFile().getAbsolutePath(), outFile.getAbsolutePath(), parameters.getMinSup());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "PrefixSpan(All)";
    }

}
