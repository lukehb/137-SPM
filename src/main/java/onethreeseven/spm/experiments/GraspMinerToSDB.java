package trajsuite.experiments.Effectiveness;

import onethreeseven.collections.IntArray;
import onethreeseven.spm.algorithm.GraspMiner;
import onethreeseven.spm.model.RepSeq;
import onethreeseven.spm.model.SequenceGraph;

import java.util.Collection;
import java.util.PrimitiveIterator;

/**
 * Convert GraspMiner output to ints.
 * @author Luke Bermingham
 */
public class GraspMinerToSDB {

    public int[][] run(int minAbsSup, int[][] seqDb, SequenceGraph g, int maxGap){
        GraspMiner algo = new GraspMiner();
        Collection<RepSeq> patterns = algo.run(g, seqDb, minAbsSup, maxGap);

        int[][] out = new int[patterns.size()][];
        int i = 0;
        for (RepSeq pattern : patterns) {
            IntArray seq = new IntArray(pattern.size(), false);
            PrimitiveIterator.OfInt iter = pattern.intIter();
            while(iter.hasNext()){
                seq.add(iter.next());
            }
            out[i] = seq.getArray();
            i++;
        }
        return out;
    }

}
