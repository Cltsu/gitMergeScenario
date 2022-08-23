package nju.merge.client;

import nju.merge.Utils.JSONUtils;
import nju.merge.core.ConflictCollector;
import nju.merge.core.DatasetCollector;
import nju.merge.core.DatasetFilter;
//import nju.merge.core.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Client {

    private static final String root = "/Users/zhuyihang/Desktop/";
    private static final String output = "/Users/zhuyihang/Desktop/";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void addSimpleRepo(Map<String, String> repos){
        repos.put("junit4","https://github.com/junit-team/junit4.git");
//        repos.put("spring-boot","");
    }

    public static void addReposFromText(String txtPath, Map<String, String> repos){}

    public static void main(String[] args) throws Exception{
        Map<String, String> repos = new HashMap<>();
        addSimpleRepo(repos);

        repos.forEach((project, url) -> {
            String path = root + project + "/";
            String outputConflictPath = output + "conflict/";
            String outputJsonPath = output + "mergeTuples" + "/";
            try {
//                logger.info("-------------------------- Collect conflict files ----------------------------------");
//                collectMergeConflict(path, project, url, outputConflictPath);
//
//                logger.info("-------------------------- Collect merge tuples ----------------------------------");
                collectMergeTuples(outputJsonPath, project, outputConflictPath);

                logger.info("-------------------------- Merge tuples analysis ----------------------------------");
                mergeTuplesAnalysis(outputJsonPath + project + ".json");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void collectMergeConflict(String projectPath, String projectName, String url, String output) throws Exception {
        ConflictCollector collector = new ConflictCollector(projectPath, projectName, url, output);
        collector.process();
    }

    public static void collectMergeTuples(String outputFile, String projectName, String conflictFilesPath) throws Exception {
        DatasetCollector collector = new DatasetCollector();
        collector.extractFromProject(conflictFilesPath);
        JSONUtils.writeTuples2Json(collector.mergeTuples, projectName, outputFile);
    }

    public static void mergeTuplesAnalysis(String jsonPath) throws Exception {
        DatasetFilter filter = new DatasetFilter(jsonPath);
        filter.analysis();
    }
}
