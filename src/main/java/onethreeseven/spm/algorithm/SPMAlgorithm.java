package onethreeseven.spm.algorithm;

import onethreeseven.spm.model.SequentialPattern;
import onethreeseven.trajsuitePlugin.algorithm.BaseAlgorithm;

import java.io.File;
import java.util.Collection;

/**
 * Interface for all sequential pattern mining algorithms.
 * @author Luke Bermingham
 */
public abstract class SPMAlgorithm extends BaseAlgorithm<Collection<SequentialPattern>, SPMParameters> {

    public abstract String getPatternType();

    public String toString(){
        return getSimpleName() + "(" + getPatternType() + ")";
    }

}
