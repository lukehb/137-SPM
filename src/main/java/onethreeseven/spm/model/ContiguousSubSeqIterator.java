package onethreeseven.spm.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} for iterating over a sequence's contiguous sub-sequences of a given size.
 * For example sequence = {1,2,3,4,5} and subSize = 2, we get {1,2} {2,3} {3,4} {4,5}.
 * @author Luke Bermingham
 */
public class ContiguousSubSeqIterator implements Iterator<int[]> {

    private final int subSize;
    private final int[] sequence;
    private int i = 0;

    public ContiguousSubSeqIterator(int subSize, int[] sequence){
        this.subSize = subSize;
        this.sequence = sequence;
    }

    @Override
    public boolean hasNext() {
        return i + subSize < sequence.length + 1;
    }

    @Override
    public int[] next() {
        if(!hasNext()){
            throw new NoSuchElementException("There is no next element.");
        }
        int[] subSeq = new int[subSize];
        System.arraycopy(sequence, i, subSeq, 0, subSize);
        i++;
        return subSeq;
    }

    /**
     * @return Get the next sub-sequence, but boxed as {@link Integer}.
     */
    public Integer[] nextBoxed() {
        if(!hasNext()){
            throw new NoSuchElementException("There is no next element.");
        }
        Integer[] subSeq = new Integer[subSize];
        for (int j = 0; j < subSize; j++) {
            subSeq[j] = sequence[i + j];
        }
        i++;
        return subSeq;
    }

}
