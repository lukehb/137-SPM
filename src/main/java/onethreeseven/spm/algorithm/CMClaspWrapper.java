//package onethreeseven.spm.algorithm;
//
//import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.AlgoCM_ClaSP;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreator;
//import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreatorStandard_Map;
//
//import java.io.File;
//import java.io.IOException;
//
///**
// * Wrapper for {@link AlgoCM_ClaSP}
// * @author Luke Bermingham
// */
//public class CMClaspWrapper extends SPMFAlgoWrapper {
//
//
//
//    @Override
//    public void run(SPMParameters parameters, File tmpOutputFile) {
//        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
//        IdListCreator idListCreator = IdListCreatorStandard_Map.getInstance();
//        SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator, idListCreator);
//
//        try {
//
//            sequenceDatabase.loadFile(parameters.getSpmfFile().getAbsolutePath(), parameters.getMinSupRelative());
//            AlgoCM_ClaSP algo = new AlgoCM_ClaSP(parameters.getMinSup(), abstractionCreator, true, true);
//            algo.runAlgorithm(sequenceDatabase, false, false, tmpOutputFile.getAbsolutePath(), false);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    @Override
//    public String getSimpleName() {
//        return "cmclasp";
//    }
//
//    @Override
//    public String getPatternType() {
//        return "closed";
//    }
//
//}
