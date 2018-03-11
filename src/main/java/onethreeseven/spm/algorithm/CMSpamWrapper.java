package onethreeseven.spm.algorithm;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM;
import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.SequentialPattern;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Wrapper for {@link CMSpamWrapper}
 * @author Luke Bermingham
 */
public class CMSpamWrapper extends SPMFAlgoWrapper {

    @Override
    public void run(SPMParameters parameters, File tmpOutputFile) {

        AlgoCMSPAM algo = new AlgoCMSPAM();

        try {
            algo.runAlgorithm(parameters.getSpmfFile().getAbsolutePath(), tmpOutputFile.getAbsolutePath(), parameters.getMinSupRelative(), false);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    @Override
    public String toString() {
        return "CMSpam(All)";
    }

}
