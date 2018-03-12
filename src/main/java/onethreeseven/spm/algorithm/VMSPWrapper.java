//package onethreeseven.spm.algorithm;
//
//import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoVMSP;
//
//import java.io.File;
//import java.io.IOException;
//
///**
// * Wrapper for {@link VMSPWrapper}
// * @author Luke Bermingham
// */
//public class VMSPWrapper extends SPMFAlgoWrapper{
//
//    @Override
//    public void run(SPMParameters parameters, File tmpOutputFile) {
//        AlgoVMSP algo = new AlgoVMSP();
//
//        try {
//            algo.runAlgorithm(parameters.getSpmfFile().getAbsolutePath(), tmpOutputFile.getAbsolutePath(), parameters.getMinSupRelative());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public String getSimpleName() {
//        return "vmsp";
//    }
//
//    @Override
//    public String getPatternType() {
//        return "max";
//    }
//
//}
