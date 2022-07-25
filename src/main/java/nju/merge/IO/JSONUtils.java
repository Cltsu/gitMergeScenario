package nju.merge.IO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import nju.merge.core.DatasetFilter;
import nju.merge.entity.MergeTuple;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class JSONUtils {
    public static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static List<MergeTuple> loadTuplesFromJson(String path) throws Exception{
        File json = new File(path);
        if(!json.exists()){
            logger.info("cant find JSON file : {}", path);
        }
        List<MergeTuple> tuples = new ArrayList<>();
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
                    tuples.add(tmp);
                    reader.endObject();
                }
                reader.endArray();
            }
        }
        reader.endObject();
        reader.close();
        return tuples;
    }


    public static void writeTuples2Json(List<MergeTuple> tuples, String project, String output) throws Exception {
        JSONObject json = new JSONObject();
        json.put("Project", project);
        JSONArray array = new JSONArray();
        tuples.forEach(tuple -> {
            JSONObject tmp = new JSONObject();
            tmp.put("path", tuple.path);
            tmp.put("a", tuple.a);
            tmp.put("b", tuple.b);
            tmp.put("o", tuple.o);
            tmp.put("r", tuple.r);
            array.add(tmp);
        });
        json.put("mergeTuples", array);
        File dir = new File(output);
        if(!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(output + project + ".json"));
        osw.write(json.toJSONString());
        osw.flush();
        osw.close();
    }

    private static List<String> removeBlankLine(List<String> lines){
        return lines.stream().filter(line -> !"".equals(line)).collect(Collectors.toList());
    }


}
