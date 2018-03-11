package onethreeseven.spm.algorithm;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoTKS;
import java.io.File;
import java.io.IOException;

/**
 * Wrapper for {@link AlgoTKS}.
 * Remember the min-sup parameter actually represent the parameter "top-k" in this case.
 * @author Luke Bermingham
 */
public class TKSWrapper extends SPMFAlgoWrapper {



    @Override
    public String toString() {
        return "TKS(Top-k)";
    }

    @Override
    public void run(SPMParameters parameters, File outFile) {
        AlgoTKS algo = new AlgoTKS();

        //pass minsup as top-k (they are both ints)
        try {
            algo.runAlgorithm(parameters.getSpmfFile().getAbsolutePath(), outFile.getAbsolutePath(), parameters.getMinSup());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
