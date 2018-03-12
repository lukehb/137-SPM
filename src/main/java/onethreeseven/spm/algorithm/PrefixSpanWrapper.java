//package onethreeseven.spm.algorithm;
//
//import ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.AlgoPrefixSpan_AGP;
//import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoPrefixSpan;
//import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.AlgoSPADE;
//
//import java.io.File;
//import java.io.IOException;
//
///**
// * A wrapper around SPMF's Prefix span {@link AlgoPrefixSpan}.
// * @author Luke Bermingham
// */
//public class PrefixSpanWrapper extends SPMFAlgoWrapper {
//
//    @Override
//    public void run(SPMParameters parameters, File outFile) {
//
//        AlgoPrefixSpan algo = new AlgoPrefixSpan();
//        try {
//            algo.runAlgorithm(parameters.getSpmfFile().getAbsolutePath(), outFile.getAbsolutePath(), parameters.getMinSup());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public String getSimpleName() {
//        return "prefixspan";
//    }
//
//    @Override
//    public String getPatternType() {
//        return "all";
//    }
//
//}
