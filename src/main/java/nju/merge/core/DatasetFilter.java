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

public class DatasetFilter {
    private final String projectName;
    private final String outputDir;

    private List<MergeTuple> tuples;
    private static final Logger logger = LoggerFactory.getLogger(DatasetFilter.class);

    public DatasetFilter(String path, String projectName, String outputDir) {
        this.tuples = loadTuplesFromJson(path); // todo: 一次性加载所有tuple在海量数据下可能溢出？
        this.projectName = projectName;
        this.outputDir = outputDir;
    }

    public static boolean defaultFilter(MergeTuple tuple){
        return filterIncompleteTuple(tuple) && !filterAcceptOneSide(tuple);
    }

    public static boolean filterIncompleteTuple(MergeTuple tuple){
        return !(tuple.resolve.size() == 0 || tuple.ours.size() == 0 || tuple.theirs.size() == 0 || tuple.base.size()==0);
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
            return resolve.equals(ours) || resolve.equals(theirs);
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

    public static boolean filterNoBase(MergeTuple tuple){
        return tuple.base.size() == 0;
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

        JSONUtils.writeTuples2Json(mixLine, projectName, PathUtils.getFileWithPathSegment(outputDir, "mixLine"));
        JSONUtils.writeTuples2Json(outOfVocabulary, projectName, PathUtils.getFileWithPathSegment(outputDir, "outOfVocabulary"));
        JSONUtils.writeTuples2Json(lackOfR, projectName, PathUtils.getFileWithPathSegment(outputDir, "lackOfResolution"));

        logger.info("Concat : {} ", concat.size());
        logger.info("MixLine : {} ", mixLine.size());
        logger.info("Out of vocabulary : {} ", outOfVocabulary.size());

        List<MergeTuple> noBase = this.tuples.stream().filter(DatasetFilter::filterNoBase).collect(Collectors.toList());
        logger.info("no base: {}  {}%", noBase.size(), 100*(double)(noBase.size())/tuples.size());

    }

    public void analysisDefault() throws Exception {
        List<MergeTuple> defaultTuples = this.tuples.stream().filter(DatasetFilter::defaultFilter).collect(Collectors.toList());
        JSONUtils.writeTuples2Json(defaultTuples, projectName, PathUtils.getFileWithPathSegment(outputDir, "defaultFilter"));
        logger.info("tuples : {}", defaultTuples.size());
    }
}
