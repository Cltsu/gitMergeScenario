package nju.merge.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import nju.merge.IO.JSONUtils;
import nju.merge.entity.MergeScenario;
import nju.merge.entity.MergeTuple;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nju.merge.IO.JSONUtils.loadTuplesFromJson;

public class DatasetFilter {

    private List<MergeTuple> tuples;
    private String projectName;
    private String output;
    private static final Logger logger = LoggerFactory.getLogger(DatasetFilter.class);

    public DatasetFilter(String path) throws Exception {
        this.tuples = loadTuplesFromJson(path);
    }


    public static boolean filterIncompleteTuple(MergeTuple tuple){
        return !(tuple.r.size() == 0 || tuple.a.size() == 0 || tuple.b.size() == 0);
    }

    public static boolean filterAcceptOneSide(MergeTuple tuple){
        return equalCodeSnippet(tuple.r, tuple.a) || equalCodeSnippet(tuple.r, tuple.b) || equalCodeSnippet(tuple.r, tuple.o);
    }


    public static boolean filterConcat(MergeTuple tuple){
        if(tuple.a.size() == 0 || tuple.b.size() == 0) return false;
        if(tuple.r.size() == tuple.a.size() + tuple.b.size()){
            return equalCodeSnippet(tuple.r.subList(0, tuple.a.size()), tuple.a) &&
                    equalCodeSnippet(tuple.r.subList(tuple.a.size(), tuple.r.size()), tuple.b)
                    ||
                    equalCodeSnippet(tuple.r.subList(0, tuple.b.size()), tuple.b) &&
                            equalCodeSnippet(tuple.r.subList(tuple.b.size(), tuple.r.size()), tuple.a);
        }
        return false;
    }


    public static boolean filterMixLine(MergeTuple tuple){
        return !(tuple.r.size() == 0 || filterAcceptOneSide(tuple) || filterConcat(tuple) || filterOutOfVocabularyLine(tuple));
    }


    public static boolean filterOutOfVocabularyLine(MergeTuple tuple){
        Set<String> uniqueLines = new HashSet<>();
        uniqueLines.addAll(tuple.a);
        uniqueLines.addAll(tuple.b);
        uniqueLines.addAll(tuple.o);
        for(var s : tuple.r){
            if(!uniqueLines.contains(s)) return true;
        }
        return false;
    }

    public static boolean filterLackOfResolution(MergeTuple tuple){
        return (tuple.r.size() == 0);
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
        JSONUtils.writeTuples2Json(tuples, "junit4", "G:\\merge\\output\\" + kind + "\\");
    }


    public void analysis() throws Exception {
        String project = "platform_packages_apps_settings";
        String output = "G:\\output\\tuples\\mixlines.json";
        logger.info("Total tuples : {}", this.tuples.size());

        List<MergeTuple> acceptOneSide = this.tuples.stream().filter(DatasetFilter::filterAcceptOneSide).toList();
        List<MergeTuple> lackOfR = this.tuples.stream().filter(DatasetFilter::filterLackOfResolution).toList();


        this.tuples = this.tuples.stream().filter(DatasetFilter::filterIncompleteTuple).toList();


        List<MergeTuple> concat = this.tuples.stream().filter(DatasetFilter::filterConcat).toList();
        List<MergeTuple> mixLine = this.tuples.stream().filter(DatasetFilter::filterMixLine).toList();
        List<MergeTuple> outofVoca = this.tuples.stream().filter(DatasetFilter::filterOutOfVocabularyLine).toList();



        saveTuple2Json(mixLine, "mix");
        saveTuple2Json(outofVoca, "out");
        saveTuple2Json(lackOfR, "lackOfResolution");
        logger.info("Complete tuples : {}", this.tuples.size());
        logger.info("Accept one side : {} ", acceptOneSide.size());
        logger.info("Lack of resolution : {} ", lackOfR.size());
        logger.info("Concat : {} ", concat.size());
        logger.info("MixLine : {} ", mixLine.size());
        logger.info("Out of vocabulary : {} ", outofVoca.size());
        JSONUtils.writeTuples2Json(mixLine, project, output);
    }

}
