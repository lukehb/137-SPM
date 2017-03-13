package onethreeseven.spm.model;

import gnu.trove.map.hash.TObjectIntHashMap;
import onethreeseven.collections.Range;

/**
 * A map containing pairs of items and the cover for that pair.
 * @author Luke Bermingham
 */
public class CoverMap {

    private final TObjectIntHashMap<Range> coverMap;

    public CoverMap(){
        this.coverMap = new TObjectIntHashMap<>();
    }

    public CoverMap(int[][] seqDb){
        this.coverMap = new TObjectIntHashMap<>();
        for (int[] sequence : seqDb) {
            add(sequence);
        }
    }

    public void add(int[] sequence){
        int lastIdx = sequence.length - 1;
        for (int i = 0; i < lastIdx; i++) {
            int itemA = sequence[i];
            int itemB = sequence[i+1];
            Range r = new Range(itemA, itemB);
            coverMap.adjustOrPutValue(r, 1, 1);
        }
    }

    public int getCover(int itemA, int itemB){
        return coverMap.get(new Range(itemA, itemB));
    }

}
