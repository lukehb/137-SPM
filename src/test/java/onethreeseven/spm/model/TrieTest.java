package onethreeseven.spm.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link Trie}
 * @author Luke Bermingham
 */
public class TrieTest {

    @Test
    public void testBuildAndQueryTrie() throws Exception {
        //note: frequency only increases for the last node in the sequence
        Trie<Character> t = new Trie<>();
        t.add(new Character[]{'A'});
        t.add(new Character[]{'t', 'o'});
        t.add(new Character[]{'t', 'e', 'a'});
        t.add(new Character[]{'t', 'e', 'd'});
        t.add(new Character[]{'t', 'e', 'n'});
        t.add(new Character[]{'i'});
        t.add(new Character[]{'i', 'n'});
        t.add(new Character[]{'i', 'n', 'n'});
        //Assertions
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A'}) == 1);
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'t', 'o'}) == 1);
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'t', 'e'}) == 1);
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'t', 'e', 'd'}) == 1);
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'i', 'n', 'n'}) == 1);
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'i', 'n'}) == 1);
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'i'}) == 1);
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'z'}) == 0);
    }

    @Test
    public void testAddLockedSequence() throws Exception{
        Trie<Character> t = new Trie<>();
        //test that we can add a locked sequence
        Assert.assertTrue(t.add(new Character[]{'A', 'B', 'C'}, 3, true, false));
        //test that adding it again is not possible
        Assert.assertFalse(t.add(new Character[]{'A', 'B', 'C'}, 3, true, false));
        //double check by checking the count of the sequence
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A', 'B', 'C'}) == 1);
        //test we can add a variant of the original sequence
        Assert.assertTrue(t.add(new Character[]{'A', 'B', 'D'}, 3, true, false));
        //check the frequency of the variant
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A', 'B', 'D'}) == 1);
        //check the frequency of the shared pre-sequence
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A', 'B'}) == 1);
        //and that frequency of the singleton sequence
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A'}) == 1);
    }

    @Test
    public void testClosedPatternClosure() throws Exception {
        final IPatternClosure closed = (supA, supB) -> supA == supB;

        Trie<Character> t = new Trie<>();

        //add some length 1 sequences
        t.add(new Character[]{'A'});
        t.add(new Character[]{'A'});
        t.add(new Character[]{'B'});
        t.add(new Character[]{'C'});
        //add some length 2 sequences that
        t.add(new Character[]{'A', 'B'});
        t.add(new Character[]{'A', 'B'});
        t.add(new Character[]{'A', 'C'});
        t.add(new Character[]{'A', 'D'});
        //check which sequences should correctly supersede
        Assert.assertTrue(t.supersede(new Character[]{'A', 'B'}, 2, closed));
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A', 'B'}) == 2);

        Assert.assertFalse(t.supersede(new Character[]{'A', 'C'}, 2, closed));
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A', 'C'}) == 0);

        Assert.assertFalse(t.supersede(new Character[]{'A', 'D'}, 2, closed));
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A', 'D'}) == 0);

        //check that removing the A,C and A,D updated the count of singleton A
        Assert.assertTrue(t.getFrequencyOf(new Character[]{'A'}) == 2);
    }
}