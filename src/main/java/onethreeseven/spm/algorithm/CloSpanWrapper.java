//package onethreeseven.spm.algorithm;
//
//import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.AlgoCloSpan;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.SequenceDatabase;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.creators.AbstractionCreator;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.creators.AbstractionCreator_Qualitative;
//import java.io.File;
//import java.io.IOException;
//
///**
// * Wrapper for {@link CloSpanWrapper}
// * @author Luke Bermingham
// */
//public class CloSpanWrapper extends SPMFAlgoWrapper {
//
//
//    @Override
//    public void run(SPMParameters parameters, File tmpOutputFile) {
//        try{
//
//            double minSupRel = parameters.getMinSupRelative();
//
//            AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
//            SequenceDatabase sequenceDatabase = new SequenceDatabase();
//
//            sequenceDatabase.loadFile(parameters.getSpmfFile().getAbsolutePath(), minSupRel);
//            AlgoCloSpan algorithm = new AlgoCloSpan(minSupRel, abstractionCreator, true, true);
//            algorithm.runAlgorithm(sequenceDatabase, false, false, tmpOutputFile.getAbsolutePath(), false);
//
//        }catch (IOException e){
//
//            e.printStackTrace();
//
//        }
//    }
//
//    @Override
//    public String getSimpleName() {
//        return "clospan";
//    }
//
//    @Override
//    public String getPatternType() {
//        return "closed";
//    }
//}
