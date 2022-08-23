package nju.merge.core;

import nju.merge.Utils.JSONUtils;
import nju.merge.entity.MergeTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nju.merge.Utils.JSONUtils.loadTuplesFromJson;

public class DatasetFilter {

    private List<MergeTuple> tuples;
    private static final Logger logger = LoggerFactory.getLogger(DatasetFilter.class);

    public DatasetFilter(String path) throws Exception {
        this.tuples = loadTuplesFromJson(path);
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

    public void saveTuple2Json(List<MergeTuple> tuples, String kind) throws Exception {
        JSONUtils.writeTuples2Json(tuples, "junit4", "/Users/zhuyihang/Desktop/output/" + kind + "/");
    }

    public void analysis() throws Exception {
        String project = "platform_packages_apps_settings";
        String output = "/Users/zhuyihang/Desktop/output/tuples/mixlines.json";
        logger.info("Total tuples : {}", this.tuples.size());

        List<MergeTuple> acceptOneSide = this.tuples.stream().filter(DatasetFilter::filterAcceptOneSide).collect(Collectors.toList());
        List<MergeTuple> lackOfR = this.tuples.stream().filter(DatasetFilter::filterLackOfResolution).collect(Collectors.toList());
        logger.info("Accept one side : {} ", acceptOneSide.size());
        logger.info("Lack of resolution : {} ", lackOfR.size());



        tuples = tuples.stream().filter(DatasetFilter::filterIncompleteTuple).collect(Collectors.toList());
        logger.info("Complete tuples : {}", this.tuples.size());

        List<MergeTuple> concat = this.tuples.stream().filter(DatasetFilter::filterConcat).collect(Collectors.toList());
        List<MergeTuple> mixLine = this.tuples.stream().filter(DatasetFilter::filterMixLine).collect(Collectors.toList());
        List<MergeTuple> outofVoca = this.tuples.stream().filter(DatasetFilter::filterOutOfVocabularyLine).collect(Collectors.toList());


        saveTuple2Json(mixLine, "mix");
        saveTuple2Json(outofVoca, "out");
        saveTuple2Json(lackOfR, "lackOfResolution");


        logger.info("Concat : {} ", concat.size());
        logger.info("MixLine : {} ", mixLine.size());
        logger.info("Out of vocabulary : {} ", outofVoca.size());
        JSONUtils.writeTuples2Json(mixLine, project, output);
    }
}
