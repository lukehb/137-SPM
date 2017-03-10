package onethreeseven.spm.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A general sequential pattern, has items and support.
 * @author Luke Bermingham
 */
public class SequentialPattern {

    private static final String supportSuffix = " #SUP:";

    final int support;
    final int[] sequence;

    public SequentialPattern(int[] sequence, int support) {
        this.support = support;
        this.sequence = sequence;
    }

    public SequentialPattern(List<Integer> sequence, int support){
        this.support = support;
        this.sequence = sequence.stream().mapToInt(value -> value).toArray();
    }

    public List<Integer> asList(){
        return Arrays.stream(sequence).boxed().collect(Collectors.toList());
    }

    public int getSupport() {
        return support;
    }

    public int[] getSequence() {
        return sequence;
    }

    public int size(){
        return sequence.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int item : sequence) {
            sb.append(String.valueOf(item));
            sb.append(" ");
        }
        sb.append(supportSuffix);
        sb.append(support);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequentialPattern that = (SequentialPattern) o;
        if (support != that.support) return false;
        return Arrays.equals(sequence, that.sequence);

    }

    @Override
    public int hashCode() {
        int result = support;
        result = 31 * result + Arrays.hashCode(sequence);
        return result;
    }
}
