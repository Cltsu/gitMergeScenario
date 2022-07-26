package nju.merge.client;

import nju.merge.IO.JSONUtils;
import nju.merge.IO.PathUtil;
import nju.merge.core.DatasetCollector;
import nju.merge.core.DatasetFilter;
import nju.merge.core.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void addSimpleRepo(Map<String, String> repos){
        repos.put("junit4","https://github.com/junit-team/junit4.git");
//        repos.put("spring-boot","");
    }

    public static void addReposFromText(String txtPath, Map<String, String> repos){

    }


    public static void main(String[] args) {
        String output = "./output";
        String repoPath = "../repos";
        Map<String, String> repos = new HashMap<>();
        addSimpleRepo(repos);
        repos.forEach((projectName, url) -> {
            String path = PathUtil.getFileWithPathSegment(repoPath, projectName);
            String outputConflictFiles = PathUtil.getFileWithPathSegment(output, "conflictFiles");  // forceMkdir
            String outputJsonPath = PathUtil.getFileWithPathSegment(output, "mergeTuples");
            try {
                logger.info("--------------------------collect conflict files----------------------------------");
                collectGitConflicts(path, projectName, url, outputConflictFiles);
                logger.info("--------------------------collect merge tuples----------------------------------");
                collectMergeScenario(outputJsonPath, projectName, outputConflictFiles);
                logger.info("--------------------------merge tuples analysis----------------------------------");
                mergeTuplesAnalysis(PathUtil.getFileWithPathSegment(outputJsonPath , projectName + ".json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static void collectGitConflicts(String projectPath, String projectName, String url, String output) throws Exception {
        GitService gitService = new GitService();
        gitService.collectAllConflicts(projectPath, projectName, url, output);
    }

    public static void collectMergeScenario(String outputFile, String projectName, String conflictFilesPath) throws Exception {
        DatasetCollector dc = new DatasetCollector();
        dc.extractFromProject(conflictFilesPath);
        JSONUtils.writeTuples2Json(dc.allTuple, projectName, outputFile);
    }

    public static void mergeTuplesAnalysis(String jsonPath) throws Exception {
        DatasetFilter df = new DatasetFilter(jsonPath);
        df.analysis();
    }
}
