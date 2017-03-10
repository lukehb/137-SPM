package onethreeseven.spm.model;

import gnu.trove.map.hash.TObjectIntHashMap;
import onethreeseven.collections.Range;

/**
 * The coverMap for each pair of items in a sequence data-base.
 * @author Luke Bermingham
 */
public class CoverMap {

    private TObjectIntHashMap<Range> coverMap;

    public CoverMap(int[][] seqDb){
        this.coverMap = new TObjectIntHashMap<>();
        for (int[] sequence : seqDb) {
            int lastIdx = sequence.length - 1;
            for (int i = 0; i < lastIdx; i++) {
                int itemA = sequence[i];
                int itemB = sequence[i+1];
                Range r = new Range(itemA, itemB);
                coverMap.adjustOrPutValue(r, 1, 1);
            }
        }
    }

    public int getCover(int itemA, int itemB){
        return coverMap.get(new Range(itemA, itemB));
    }

}
