package nju.merge.core;

import nju.merge.entity.MergeTuple;
import nju.merge.utils.JSONUtils;
import nju.merge.utils.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nju.merge.utils.JSONUtils.loadTuplesFromJson;

public class ChunkFilter {
    private final String projectName;
    private final String outputDir;

    private List<MergeTuple> tuples;
    private static final Logger logger = LoggerFactory.getLogger(ChunkFilter.class);

    public ChunkFilter(String path, String projectName, String outputDir) throws Exception {
        this.tuples = loadTuplesFromJson(path);
        this.projectName = projectName;
        this.outputDir = outputDir;
    }

    public static boolean defaultFilter(MergeTuple tuple){
        return filterIncompleteTuple(tuple) && !filterAcceptOneSide(tuple);
    }

    public static boolean filterIncompleteTuple(MergeTuple tuple){
        return !(tuple.resolve.size() == 0 || tuple.ours.size() == 0 || tuple.theirs.size() == 0);
    }

    public static boolean filterAcceptOneSide(MergeTuple tuple){
        return tuple.resolve.equals(tuple.ours) || tuple.resolve.equals(tuple.theirs) || tuple.resolve.equals(tuple.base);
    }


    public static boolean filterConcat(MergeTuple tuple){
        List<String> ours = new ArrayList<>(tuple.ours);
        List<String> theirs = new ArrayList<>(tuple.theirs);

        if(ours.size() == 0 || theirs.size() == 0)
            return false;

        List<String> resolve = tuple.resolve;
        if(resolve.size() == ours.size() + theirs.size()){
            List<String> copy = new ArrayList<>(ours);
            ours.addAll(theirs);
            theirs.addAll(copy);
            if(resolve.equals(ours) || resolve.equals(theirs))
                return true;
        }
        return false;
    }


    public static boolean filterMixLine(MergeTuple tuple){
        return !(tuple.resolve.size() == 0 || filterAcceptOneSide(tuple) || filterConcat(tuple) || filterOutOfVocabularyLine(tuple));
    }

    public static boolean filterOutOfVocabularyLine(MergeTuple tuple){
        Set<String> rec = new HashSet<>();
        rec.addAll(tuple.ours);
        rec.addAll(tuple.theirs);
        rec.addAll(tuple.base);
        for(var s : tuple.resolve){
            if(!rec.contains(s))
                return true;
        }
        return false;
    }

    public static boolean filterLackOfResolution(MergeTuple tuple){
        return tuple.resolve.size() == 0;
    }

    private static boolean equalCodeSnippet(List<String> one, List<String> another){
        if(one.size() == another.size()){
            for(int i = 0; i < one.size() ; i++){
                if(!one.get(i).equals(another.get(i))) return false;
            }
            return true;
        }
        return false;
    }


    public void analysis() throws Exception {
        logger.info("Total tuples : {}", this.tuples.size());


        List<MergeTuple> lackOfR = this.tuples.stream().filter(ChunkFilter::filterLackOfResolution).collect(Collectors.toList());
        logger.info("Lack of resolution : {} ", lackOfR.size());


        tuples = tuples.stream().filter(ChunkFilter::filterIncompleteTuple).collect(Collectors.toList());
//        logger.info("Complete tuples : {}", this.tuples.size());
        List<MergeTuple> acceptOneSide = this.tuples.stream().filter(ChunkFilter::filterAcceptOneSide).collect(Collectors.toList());
        logger.info("Accept one side : {} ", acceptOneSide.size());

        List<MergeTuple> concat = this.tuples.stream().filter(ChunkFilter::filterConcat).collect(Collectors.toList());
        List<MergeTuple> mixLine = this.tuples.stream().filter(ChunkFilter::filterMixLine).collect(Collectors.toList());
        List<MergeTuple> outOfVocabulary = this.tuples.stream().filter(ChunkFilter::filterOutOfVocabularyLine).collect(Collectors.toList());

//        JSONUtils.writeTuples2Json(mixLine, projectName, PathUtils.getFileWithPathSegment(outputDir, "mixLine"));
//        JSONUtils.writeTuples2Json(outOfVocabulary, projectName, PathUtils.getFileWithPathSegment(outputDir, "outOfVocabulary"));
//        JSONUtils.writeTuples2Json(lackOfR, projectName, PathUtils.getFileWithPathSegment(outputDir, "lackOfResolution"));

        logger.info("Concat : {} ", concat.size());
        logger.info("Interleave : {} ", mixLine.size());
        logger.info("Out of vocabulary : {} ", outOfVocabulary.size());
    }

    public void analysisDefault() throws Exception {
        List<MergeTuple> defaultTuples = this.tuples.stream().filter(ChunkFilter::defaultFilter).collect(Collectors.toList());
        JSONUtils.writeTuples2Json(defaultTuples, projectName, PathUtils.getFileWithPathSegment(outputDir, "defaultFilter"));
        logger.info("tuples : {}", defaultTuples.size());
    }
}
