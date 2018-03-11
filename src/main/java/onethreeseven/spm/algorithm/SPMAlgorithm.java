package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.SequentialPattern;

import java.io.File;
import java.util.Collection;

/**
 * Interface for all sequential pattern mining algorithms.
 * @author Luke Bermingham
 */
public interface SPMAlgorithm {

    void run(SPMParameters parameters, File outFile);
    Collection<SequentialPattern> run(SPMParameters parameters);

}
