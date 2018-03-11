package onethreeseven.spm.model;

/**
 * Pattern closures. I.e. the rule used to discard patterns.
 * @author Luke Bermingham
 */
public interface IPatternClosure {
    boolean discard(int supA, int supB);

    public static final IPatternClosure MAX = (supA, supB) -> true;

    public static final IPatternClosure CLOSED = (supA, supB) -> supA == supB;

    public static final IPatternClosure NONE = (supA, supB) -> false;

}
