package onethreeseven.spm.algorithm;

import onethreeseven.common.util.FileUtil;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.model.SequentialPattern;

import java.io.File;
import java.util.Collection;

/**
 * Wrapper for algos from SPMF library
 * @author Luke Bermingham
 */
public abstract class SPMFAlgoWrapper implements SPMAlgorithm {

    @Override
    public Collection<SequentialPattern> run(SPMParameters parameters) {
        final File tmpOutputFile = FileUtil.makeTempFile();

        run(parameters, tmpOutputFile);

        SPMFParser parser = new SPMFParser();
        Collection<SequentialPattern> patterns = parser.parsePatterns(tmpOutputFile);

        if(tmpOutputFile.exists() && !tmpOutputFile.delete()){
            tmpOutputFile.deleteOnExit();
        }

        return patterns;
    }
}
