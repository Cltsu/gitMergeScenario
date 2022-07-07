package nju.merge.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
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

public class DatasetFilter {

    private List<MergeTuple> tuples;
    private static final Logger logger = LoggerFactory.getLogger(DatasetFilter.class);

    public DatasetFilter(String path) throws Exception {
        loadTuplesFromJson(path);
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

    public static List<String> removeBlankLine(List<String> lines){
        return lines.stream().filter(line -> !"".equals(line)).toList();
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


    public void loadTuplesFromJson(String path) throws Exception{
        File json = new File(path);
        if(!json.exists()){
            logger.info("cant find JSON file : {}", path);
        }
        this.tuples = new ArrayList<>();
        JSONReader reader = new JSONReader(new FileReader(path));
        reader.startObject();
        while(reader.hasNext()){
            String k1 = reader.readString();
            if(k1.equals("Project")){
                reader.readString();
            } else if(k1.equals("mergeTuples")){
                reader.startArray();
                while(reader.hasNext()){
                    reader.startObject();
                    MergeTuple tmp = new MergeTuple();
                    while(reader.hasNext()) {
                        String key = reader.readString();
                        if("a".equals(key)){
                            tmp.a = reader.readObject(List.class);
                        }else if("b".equals(key)){
                            tmp.b = reader.readObject(List.class);
                        }else if("o".equals(key)){
                            tmp.o = reader.readObject(List.class);
                        }else if("r".equals(key)){
                            tmp.r = reader.readObject(List.class);
                        }else if("path".equals(key)){
                            tmp.path = reader.readObject(String.class);
                        }
                    }
                    tmp.a = removeBlankLine(tmp.a);
                    tmp.b = removeBlankLine(tmp.b);
                    tmp.o = removeBlankLine(tmp.o);
                    tmp.r = removeBlankLine(tmp.r);
                    this.tuples.add(tmp);
                    reader.endObject();
                }
                reader.endArray();
            }
        }
        reader.endObject();
        reader.close();
    }

    public void saveMix2Json(){

    }


    public void showResults(){

    }


    public void analysis() throws Exception {
        String project = "platform_packages_apps_settings";
        String output = "G:\\output\\tuples\\mixlines.json";
        logger.info("Total tuples : {}", this.tuples.size());

        List<MergeTuple> acceptOneSide = this.tuples.stream().filter(DatasetFilter::filterAcceptOneSide).toList();
        logger.info("Accept one side : {} ", acceptOneSide.size());

        this.tuples = this.tuples.stream().filter(DatasetFilter::filterIncompleteTuple).toList();
        logger.info("complete tuples : {}", this.tuples.size());

        List<MergeTuple> concat = this.tuples.stream().filter(DatasetFilter::filterConcat).toList();
        List<MergeTuple> mixLine = this.tuples.stream().filter(DatasetFilter::filterMixLine).toList();
        List<MergeTuple> outofVoca = this.tuples.stream().filter(DatasetFilter::filterOutOfVocabularyLine).toList();

        logger.info("Concat : {} ", concat.size());
        logger.info("MixLine : {} ", mixLine.size());
        logger.info("Out of vocabulary : {} ", outofVoca.size());
        DatasetCollector.writeTuples2Json(mixLine, project, output);
    }

    public static void main(String[] args) throws Exception{
        String path = "G:\\output\\tuples\\tmp.json";
        DatasetFilter df = new DatasetFilter(path);
        df.analysis();
    }
}
