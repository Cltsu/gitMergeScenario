package nju.merge.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import nju.merge.entity.MergeTuple;
import nju.merge.entity.TokenConflict;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class JSONUtils {
    public static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static List<MergeTuple> loadTuplesFromJson(String path) throws Exception {
        return loadArrayFromJson(path, "mergeTuples", MergeTuple.class);
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

        json2File(json, output, project);
    }

    public static void writeTokenConflicts2Json(List<TokenConflict> tokenConflictList, String projectName, String output) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Project", projectName);

        JSONArray array = new JSONArray(tokenConflictList);
        json.put("tokenConflicts", array);

        json2File(json, output, projectName);
    }

    public static List<TokenConflict> loadTokenConflictsFromJson(String path) {
        return loadArrayFromJson(path, "tokenConflicts", TokenConflict.class);
    }

    private static <T> List<T> loadArrayFromJson(String jsonPath, String fieldName, Class<T> classType)  {
        try {
            JSONReader reader = JSONReader.of(new FileReader(jsonPath));
            JSONArray array = (JSONArray) reader.readObject().get(fieldName);
            return List.of(array.toArray(classType));
        } catch (FileNotFoundException e) {
            logger.warn("JSON file not found in: {}", jsonPath);
            return null;
        }
    }

    private static void json2File(JSONObject json, String outputDir, String projectName) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(PathUtils.getFileWithPathSegment(outputDir, projectName + ".json")));
        osw.write(json.toJSONString());
        osw.flush();
        osw.close();
    }
}
