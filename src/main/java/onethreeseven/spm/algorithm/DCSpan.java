package onethreeseven.spm.algorithm;

import onethreeseven.collections.Range;
import onethreeseven.spm.data.SPMFParser;
import onethreeseven.spm.data.SequentialPatternWriter;
import onethreeseven.spm.model.CoveredSequentialPattern;
import onethreeseven.spm.model.SequentialPattern;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * Mines the contiguous sequential pattern output and prunes it down
 * to the most covered set that meets the specified redundancy requirements.
 * @author Luke Bermingham
 */
public class DCSpan extends SPMAlgorithm {

    @Override
    protected Collection<SequentialPattern> runImpl(SPMParameters params) {
        if(params.getOutFile() != null){
            File outFile = params.getOutFile();

            //String filename = outFile.getName();
            //String path = Paths.get(outFile.toURI()).getParent().toAbsolutePath().toString();
            //File acspanFile = new File(Paths.get(path, "acspan_" + filename).toAbsolutePath().toString());

            SPMParameters acSpanParams = new SPMParameters(params.getSequences(), params.getMinSup());
            //acSpanParams.setOutFile(acspanFile);

            Collection<SequentialPattern> patterns = new ACSpan().run(acSpanParams);

            //load the ac-span patterns into memory...if possible
//            SPMFParser parser = new SPMFParser();
//            List<SequentialPattern> patterns = parser.parsePatterns(acspanFile);

            run(params.getSequences(), (List<SequentialPattern>) patterns, params.getMaxRedund(), outFile);
            return null;
        }
        else{
            Collection<SequentialPattern> patterns = new ACSpan().run(params);
            return run(params.getSequences(), (List<SequentialPattern>) patterns, params.getMaxRedund());
        }
    }

    @Override
    public String getSimpleName() {
        return "dcspan";
    }

    @Override
    public String getPatternType() {
        return "Distinct Contiguous";
    }

    private interface PatternProcessor{
        void process(CoveredSequentialPattern pattern);
    }

    protected List<SequentialPattern> run(int[][] seqDb, List<SequentialPattern> patterns, double maxRedundancy){
        ArrayList<SequentialPattern> out = new ArrayList<>();
        run(seqDb, patterns, maxRedundancy, out::add);
        return out;
    }

    protected void run(int[][] seqDb, List<SequentialPattern> patterns, double maxRedundancy, File outFile){

        SequentialPatternWriter writer = new SequentialPatternWriter(outFile);
        FileWriter fw = null;
        BufferedWriter bw = null;
        if(outFile.setWritable(true)){
            try {
                fw  = new FileWriter(outFile, true);
                bw = new BufferedWriter(fw);
                PatternProcessor processor = writer::write;
                run(seqDb, patterns, maxRedundancy, processor);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try{
                    if(fw != null){
                        fw.close();
                    }
                    if(bw != null){
                        bw.close();
                    }

                    System.gc();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void run(int[][] seqDb, List<SequentialPattern> patterns, double maxRedundancy, PatternProcessor processor){
        //stores cover associated with each pair
        final HashMap<Range, Integer> coverMap = createCoverMap(seqDb);
        seqDb = null;
        //make a map where each pair is mapped to list of sequence ids that use it
        final HashMap<Range, Set<Integer>> pairToSequenceIds = new HashMap<>();
        final HashMap<Integer, CoveredSequentialPattern> coveredPatterns = new HashMap<>();
        //populate the two maps
        populateMaps(coverMap, pairToSequenceIds, coveredPatterns, patterns);
        //no need for the sequential patterns now, we have a map of covered pattern to lookup
        patterns.clear();

        //find and output the most covered pattern, remove it, then do this repeatedly
        while(!coveredPatterns.isEmpty() && isRunning.get()){

            Map.Entry<Integer, CoveredSequentialPattern> bestEntry = null;
            //find most covered pattern
            for (Map.Entry<Integer, CoveredSequentialPattern> entry : coveredPatterns.entrySet()) {
                if (bestEntry == null || entry.getValue().getCover() > bestEntry.getValue().getCover()) {
                    bestEntry = entry;
                }
            }

            if(bestEntry == null){
                continue;
            }

            //remove it from the candidate patterns
            coveredPatterns.remove(bestEntry.getKey());

            if(bestEntry.getValue().getCover() > 1){
                processor.process(bestEntry.getValue());
                //remove the relevant pairs from the pair map
                Set<Integer> dirtyPatternIds = updatePairToSequenceIds(bestEntry.getValue().getSequence(), pairToSequenceIds);
                //update the coveredPatterns
                updatePatterns(dirtyPatternIds, coveredPatterns, pairToSequenceIds, maxRedundancy);
            }

        }
    }

    private void updatePatterns(Set<Integer> dirtyPatternIds,
                                HashMap<Integer, CoveredSequentialPattern> coveredPatterns,
                                HashMap<Range, Set<Integer>> pairToSequenceIds,
                                double maxRedundancy){

        for (Integer patternId : dirtyPatternIds) {
            CoveredSequentialPattern pattern = coveredPatterns.get(patternId);
            if(pattern == null){
                continue;
            }
            boolean isValid = isValid(pattern.getSequence(), maxRedundancy, pairToSequenceIds);
            if(!isValid || pattern.getCover() <= 1){
                coveredPatterns.remove(patternId);
            }
        }
    }

    /**
     * Break this sequence into pairs and remove the pairs from the map
     * and return a set of all affected patterns ids.
     * @param sequence The sequence to be broken up into pairs.
     * @param pairToSequenceIds The pairs map to remove the pairs from.
     * @return A set of dirty sequence ids that need updating.
     */
    private Set<Integer> updatePairToSequenceIds(int[] sequence, HashMap<Range, Set<Integer>> pairToSequenceIds){

        Set<Integer> dirtyPatternIds = new HashSet<>();
        //break sequence down into pairs
        int lastIdx = sequence.length - 1;
        for (int j = 0; j < lastIdx; j++) {
            int itemA = sequence[j];
            int itemB = sequence[j+1];
            //update cover of each pair
            Range r = new Range(itemA, itemB);
            Set<Integer> associatedSeqIds = pairToSequenceIds.remove(r);
            if(associatedSeqIds != null){
                dirtyPatternIds.addAll(associatedSeqIds);
                //coverMap.remove(r);
                pairToSequenceIds.remove(r);
            }
        }
        return dirtyPatternIds;
    }

    private HashMap<Range, Integer> createCoverMap(int[][] seqDb){
        HashMap<Range, Integer> coverMap = new HashMap<>();
        for (int[] sequence : seqDb) {
            int lastIdx = sequence.length - 1;
            for (int j = 0; j < lastIdx; j++) {
                int itemA = sequence[j];
                int itemB = sequence[j+1];
                //update cover of each pair
                Range r = new Range(itemA, itemB);
                Integer cover = coverMap.getOrDefault(r, 0);
                cover++;
                coverMap.put(r, cover);
            }
        }
        return coverMap;
    }

    private void populateMaps(HashMap<Range, Integer> pairCoverMap,
                              HashMap<Range, Set<Integer>> pairToSequenceIds,
                              HashMap<Integer, CoveredSequentialPattern> coveredPatterns,
                              List<SequentialPattern> patterns){

        for (int i = 0; i < patterns.size(); i++) {
            SequentialPattern pattern = patterns.get(i);
            int[] sequence = pattern.getSequence();
            int lastIdx = sequence.length - 1;
            int cover = 0;

            HashSet<Range> pairSet = new HashSet<>();

            for (int j = 0; j < lastIdx; j++) {
                int itemA = sequence[j];
                int itemB = sequence[j+1];
                Range r = new Range(itemA, itemB);
                //update pair to sequence ids
                Set<Integer> associatedSeqIds = pairToSequenceIds.getOrDefault(r, new HashSet<>());
                associatedSeqIds.add(i);
                pairToSequenceIds.put(r, associatedSeqIds);
                if(!pairSet.contains(r)){
                    cover += pairCoverMap.getOrDefault(r, 0);
                    pairSet.add(r);
                }
            }
            if(cover > 1){
                coveredPatterns.put(i, new CoveredSequentialPattern(sequence, pattern.getSupport(), cover));
            }
        }
    }

    private boolean isValid(int[] sequence, double maxRedundancy,
                                             HashMap<Range, Set<Integer>> pairToSequenceIds){
        int redundantPairs = 0;

        for (int i = 0; i < sequence.length - 1; i++) {
            Range pair = new Range(sequence[i], sequence[i+1]);
            if(pairToSequenceIds.get(pair) == null){
                redundantPairs++;
            }
        }

        double totalPairs = sequence.length - 1;
        double redundancy = redundantPairs/totalPairs;
        return redundancy <= maxRedundancy;

    }

    @Override
    public String toString() {
        return getSimpleName() + "(" + getPatternType() + ")";
    }

}
