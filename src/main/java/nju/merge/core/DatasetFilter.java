package nju.merge.core;

import nju.merge.Utils.JSONUtils;
import nju.merge.Utils.PathUtil;
import nju.merge.entity.MergeTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nju.merge.Utils.JSONUtils.loadTuplesFromJson;

public class DatasetFilter {
    private final String projectName;
    private final String outputDir;

    private List<MergeTuple> tuples;
    private static final Logger logger = LoggerFactory.getLogger(DatasetFilter.class);

    public DatasetFilter(String path, String projectName, String outputDir) throws Exception {
        this.tuples = loadTuplesFromJson(path);
        this.projectName = projectName;
        this.outputDir = outputDir;
    }

    public static boolean filterIncompleteTuple(MergeTuple tuple){
        return !(tuple.resolve.size() == 0 || tuple.ours.size() == 0 || tuple.theirs.size() == 0);
    }

    public static boolean filterAcceptOneSide(MergeTuple tuple){
        return equalCodeSnippet(tuple.resolve, tuple.ours) || equalCodeSnippet(tuple.resolve, tuple.theirs) || equalCodeSnippet(tuple.resolve, tuple.base);
    }


    public static boolean filterConcat(MergeTuple tuple){
        if(tuple.ours.size() == 0 || tuple.theirs.size() == 0) return false;
        if(tuple.resolve.size() == tuple.ours.size() + tuple.theirs.size()){
            return equalCodeSnippet(tuple.resolve.subList(0, tuple.ours.size()), tuple.ours) &&
                    equalCodeSnippet(tuple.resolve.subList(tuple.ours.size(), tuple.resolve.size()), tuple.theirs)
                    ||
                    equalCodeSnippet(tuple.resolve.subList(0, tuple.theirs.size()), tuple.theirs) &&
                            equalCodeSnippet(tuple.resolve.subList(tuple.theirs.size(), tuple.resolve.size()), tuple.ours);
        }
        return false;
    }


    public static boolean filterMixLine(MergeTuple tuple){
        return !(tuple.resolve.size() == 0 || filterAcceptOneSide(tuple) || filterConcat(tuple) || filterOutOfVocabularyLine(tuple));
    }

    public static boolean filterOutOfVocabularyLine(MergeTuple tuple){
        Set<String> uniqueLines = new HashSet<>();
        uniqueLines.addAll(tuple.ours);
        uniqueLines.addAll(tuple.theirs);
        uniqueLines.addAll(tuple.base);
        for(var s : tuple.resolve){
            if(!uniqueLines.contains(s)) return true;
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

        List<MergeTuple> acceptOneSide = this.tuples.stream().filter(DatasetFilter::filterAcceptOneSide).collect(Collectors.toList());
        List<MergeTuple> lackOfR = this.tuples.stream().filter(DatasetFilter::filterLackOfResolution).collect(Collectors.toList());
        logger.info("Accept one side : {} ", acceptOneSide.size());
        logger.info("Lack of resolution : {} ", lackOfR.size());

        // 要求有ours, theirs, resolve(这里需要这么严格吗)
        tuples = tuples.stream().filter(DatasetFilter::filterIncompleteTuple).collect(Collectors.toList());
        logger.info("Complete tuples : {}", this.tuples.size());

        List<MergeTuple> concat = this.tuples.stream().filter(DatasetFilter::filterConcat).collect(Collectors.toList());
        List<MergeTuple> mixLine = this.tuples.stream().filter(DatasetFilter::filterMixLine).collect(Collectors.toList());
        List<MergeTuple> outOfVocabulary = this.tuples.stream().filter(DatasetFilter::filterOutOfVocabularyLine).collect(Collectors.toList());

        JSONUtils.writeTuples2Json(mixLine, projectName, PathUtil.getFileWithPathSegment(outputDir, "mixLine"));
        JSONUtils.writeTuples2Json(outOfVocabulary, projectName, PathUtil.getFileWithPathSegment(outputDir, "outOfVocabulary"));
        JSONUtils.writeTuples2Json(lackOfR, projectName, PathUtil.getFileWithPathSegment(outputDir, "lackOfResolution"));

        logger.info("Concat : {} ", concat.size());
        logger.info("MixLine : {} ", mixLine.size());
        logger.info("Out of vocabulary : {} ", outOfVocabulary.size());
    }
}
