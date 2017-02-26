package onethreeseven.spm.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

/**
 * Test which is faster for "contains" querying a
 * {@link Trie} or a {@link java.util.HashSet}
 *
 * The results are fairly conclusive, if you have to convert a string to a Character[]
 * it is too big a performance impact to even matter.
 *
 * @author Luke Bermingham
 */
public class TriePerformanceTest {

    private static final String[] baseStrings = new String[]{
        "the", "quick", "brown", "fox", "jumped", "over", "the", "lazy", "dog", "onethreeseven",
        "137best", "Playing jazz vibe chords quickly excites my wife", "Exquisite", "farm",
        "wench", "gives", "body", "jolt", "to", "prize", "stinker", "a", "b", "c", "d", "e",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private static final int nStrings = 100000;
    private static final int minStringSize = 20;
    private static final Random r = new Random();

    private static final HashSet<String> hashSet = new HashSet<>(baseStrings.length);
    private static final Trie<Character> trie = new Trie<>();
    private static final String[] queries = new String[nStrings];

    @BeforeClass
    public static void setup(){
        //build the strings to enter
        final StringBuilder sb = new StringBuilder();
        final int nBaseStrings = baseStrings.length;

        for (int i = 0; i < nStrings; i++) {
            while(sb.length() < minStringSize){
                sb.append(baseStrings[r.nextInt(nBaseStrings)]);
            }
            String candidate = sb.toString();
            sb.setLength(0);
            //file hashset
            hashSet.add(candidate);
            //fill trie
            Character[] c = candidate.chars().mapToObj(j->(char)j).toArray(Character[]::new);
            trie.add(c);
            //fill queries
            queries[i] = candidate;
        }
    }

    @Test
    public void testTrieContainsQueries(){
        int nContained = 0;
        long startTime = System.currentTimeMillis();
        for (String query : queries) {
            Character[] c = query.chars().mapToObj(i->(char)i).toArray(Character[]::new);
            nContained += (trie.getFrequencyOf(c)) > 0 ? 1 : 0;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Trie took: " + (endTime - startTime) + "ms");
        System.out.println("Trie matched: " + nContained);
    }

    @Test
    public void testHashSetContainsQueries(){
        int nContained = 0;
        long startTime = System.currentTimeMillis();
        for (String query : queries) {
            if(hashSet.contains(query)){
                nContained++;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Hashset took: " + (endTime - startTime) + "ms");
        System.out.println("Hashset matched: " + nContained);
    }

}
