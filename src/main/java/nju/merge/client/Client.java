package nju.merge.client;

import nju.merge.IO.JSONUtils;
import nju.merge.core.DatasetCollector;
import nju.merge.core.DatasetFilter;
import nju.merge.core.GitService;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

    private static final String output = "G:\\merge\\output\\";
    private static final String gitPath = "G:\\merge\\gitRepos\\";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void addSimpleRepo(Map<String, String> repos){
        repos.put("junit4","https://github.com/junit-team/junit4.git");
//        repos.put("spring-boot","");
    }

    public static void addReposFromText(String txtPath, Map<String, String> repos){

    }


    public static void main(String[] args) throws Exception{
        Map<String, String> repos = new HashMap<>();
        addSimpleRepo(repos);
        repos.forEach((project, url) -> {
            String path = gitPath + project + "\\";
            String outputConflictFiles = output + "\\" + "conflictFiles\\";
            String outputJsonPath = output + "\\" + "mergeTuples" + "\\";
            try {
                logger.info("--------------------------collect conflict files----------------------------------");
//                collectGitConflicts(path, project, url, outputConflictFiles);
                logger.info("--------------------------collect merge tuples----------------------------------");
//                collectMergeScenario(outputJsonPath, project, outputConflictFiles);
                logger.info("--------------------------merge tuples analysis----------------------------------");
                mergeTuplesAnalysis(outputJsonPath + project + ".json");
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
