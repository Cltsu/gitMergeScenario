package nju.merge.client;

import nju.merge.IO.JSONUtils;
import nju.merge.core.DatasetCollector;
import nju.merge.core.DatasetFilter;
import nju.merge.core.GitService;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

    private static final String output = "G:\\merge\\output\\";
    private static final String gitPath = "G:\\merge\\gitRepos\\";


    public static void addSimpleRepo(Map<String, String> repos){
        repos.put("junit4","https://github.com/junit-team/junit4.git");
    }

    public static void addReposFromTxt(String txtPath, Map<String, String> repos){

    }


    public static void main(String[] args) throws Exception{
        Map<String, String> repos = new HashMap<>();
        addSimpleRepo(repos);
        repos.forEach((project, url) -> {
            String path = gitPath + project + "\\";
            String outputConflictFiles = output + "\\" + "conflictFiles";
            String outputJsonPath = output + "\\" + "mergeTuples" + "\\" + project + ".json";
            try {
                collectGitConflicts(path, project, url, outputConflictFiles);
                collectMergeScenario(outputJsonPath, project, outputConflictFiles);
                mergeTuplesAnalysis(outputJsonPath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }


    public static void collectGitConflicts(String projectPath, String projectName, String url, String output) throws Exception {
        GitService gs = new GitService();
        Repository repo = gs.CloneIfNotExist(projectPath,url);
        List<RevCommit> commits = gs.collectMergeCommits(repo);
        for(RevCommit c : commits){
            gs.mergeAndGetConflictFiles(c, repo, projectName, projectPath, output);
            gs.threeWayMergeFile(output);
        }
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
