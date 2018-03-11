package onethreeseven.spm.algorithm;

import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.AlgoCMSPADE;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap;
import java.io.File;
import java.io.IOException;

/**
 * Wrapper around {@link AlgoCMSPADE}
 * @author Luke Bermingham
 */
public class CMSpadeWrapper extends SPMFAlgoWrapper {
    @Override
    public void run(SPMParameters parameters, File outFile) {

        double minSupRel = parameters.getMinSupRelative();

        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
        IdListCreator idListCreator = IdListCreator_FatBitmap.getInstance();
        CandidateGenerator candidateGenerator = CandidateGenerator_Qualitative.getInstance();
        SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator, idListCreator);
        try {

            sequenceDatabase.loadFile(parameters.getSpmfFile().getAbsolutePath(), minSupRel);

            AlgoCMSPADE algo = new AlgoCMSPADE(minSupRel, true, abstractionCreator);
            //write to file and read back again
            algo.runAlgorithm(sequenceDatabase, candidateGenerator, false, false, outFile.getAbsolutePath(), false);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "CMSpade(All)";
    }

}
