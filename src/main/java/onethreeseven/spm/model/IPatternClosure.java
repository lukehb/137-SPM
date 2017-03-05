package onethreeseven.spm.model;

/**
 * Pattern closures, so far this contract is only used by {@link onethreeseven.spm.algorithm.CCSpan}.
 * @author Luke Bermingham
 */
public interface IPatternClosure {
    boolean discard(int supA, int supB);

    public static final IPatternClosure MAX = (supA, supB) -> true;

    public static final IPatternClosure CLOSED = (supA, supB) -> supA == supB;

    public static final IPatternClosure NONE = (supA, supB) -> false;

}
