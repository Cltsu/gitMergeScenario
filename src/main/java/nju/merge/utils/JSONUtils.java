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
import java.util.ArrayList;
import java.util.List;

public class JSONUtils {
    public static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static List<MergeTuple> loadTuplesFromJson(String path) {
        ArrayList<MergeTuple> res = new ArrayList<>();
        // batch operation

        // load path下所有数字文件
        File projectTupleDir = new File(path);
        String[] fileList = projectTupleDir.list();
        if (fileList == null) {
            return null;
        }
        int count = 0;
        for (String s : fileList) {
            // todo: 一次性加载所有tuple在海量数据下可能溢出？
            res.addAll(loadArrayFromJson(PathUtils.getFileWithPathSegment(path, s), "mergeTuples", MergeTuple.class));
            logger.info("{} files loaded for analysis out of {} files", ++count, fileList.length);
        }
        return res;
    }

    public static void writeTuples2Json(List<MergeTuple> tuples, String project, String output) throws Exception {
        int len = tuples.size();

        // batch operation
        int batchEnd = Math.min(1000, tuples.size());
        int batchStart = 0;
        int fileCounter = 1;
        do {
            JSONObject json = new JSONObject();
            json.put("Project", project);
            JSONArray array = new JSONArray();
            tuples.subList(batchStart, batchEnd).forEach(tuple -> {
                JSONObject obj = new JSONObject();
                obj.put("path", tuple.path);
                obj.put("ours", tuple.ours);
                obj.put("theirs", tuple.theirs);
                obj.put("base", tuple.base);
                obj.put("resolve", tuple.resolve);
                array.add(obj);
            });
            json.put("mergeTuples", array);
            json2File(json, PathUtils.getFileWithPathSegment(output, project), Integer.toString(fileCounter));
            logger.info("{}% -- {} out of {} tuple added to JSONArray", 100d * batchEnd / len, batchEnd, len);

            fileCounter++;
            batchStart = batchEnd;
            batchEnd = Math.min(batchEnd + 1000, tuples.size());
        } while(batchStart != batchEnd);
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

    private static <T> List<T> loadArrayFromJson(String jsonPath, String fieldName, Class<T> classType) {
        try {
            JSONReader reader = JSONReader.of(new FileReader(jsonPath));
            JSONArray array = (JSONArray) reader.readObject().get(fieldName);
            return List.of(array.toArray(classType));
        } catch (FileNotFoundException e) {
            logger.warn("JSON file not found in: {}", jsonPath);
            return null;
        }
    }

    private static void json2File(JSONObject json, String outputDir, String name) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(PathUtils.getFileWithPathSegment(outputDir, name + ".json")));
        String content = json.toJSONString();
        osw.write(content);
        osw.flush();
        osw.close();
    }
}
