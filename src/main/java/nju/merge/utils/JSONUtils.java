package nju.merge.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
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
            logger.info("JSON file not found in: {}", path);
            return null;
        }

        List<MergeTuple> mergeTuples = new ArrayList<>();

        JSONReader reader = JSONReader.of(new FileReader(path));
        JSONArray array = (JSONArray) reader.readObject().get("mergeTuples");

        reader.close();

        for(Object obj : array){
            String jsonString = JSON.toJSONString(obj);
            mergeTuples.add(JSON.parseObject(jsonString, MergeTuple.class));
        }

        return mergeTuples;
    }

    public static void writeTuples2Json(List<MergeTuple> tuples, String project, String output) throws Exception {
        JSONObject json = new JSONObject();
        json.put("Project", project);

        JSONArray array = new JSONArray();
        tuples.forEach(tuple -> {
            JSONObject obj = new JSONObject();
            obj.put("path", tuple.path);
            obj.put("ours", tuple.ours);
            obj.put("theirs", tuple.theirs);
            obj.put("base", tuple.base);
            obj.put("resolve", tuple.resolve);
            array.add(obj);
        });

        json.put("mergeTuples", array);

        File dir = new File(output);
        if(!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(PathUtils.getFileWithPathSegment(output,project+".json")));
        osw.write(json.toJSONString());
        osw.flush();
        osw.close();
    }



}
