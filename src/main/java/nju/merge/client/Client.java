package nju.merge.client;

import nju.merge.IO.JSONUtils;
import nju.merge.core.DatasetCollector;
import nju.merge.core.DatasetFilter;
import nju.merge.core.GitService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Client {

    private static final String output = "/content/merge/output/";
    private static final String gitPath = "/content/merge/gitRepos/";
    private static final String drivePath = "/content/drive/MyDrive/merge/output/";
    private static final String repoList = "/content/drive/MyDrive/merge/list";
    private static final String doneList = "/content/drive/MyDrive/merge/done";

//    private static final String output = "G:/merge/output/";
//    private static final String gitPath = "G:/merge/gitRepos/";
//    private static final String repoList = "G:/merge/list.txt";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void addSimpleRepo(Map<String, String> repos){
        repos.put("junit4","https://github.com/junit-team/junit4.git");
    }

    public static void addReposFromText(String txtPath, Map<String, String> repos) throws IOException {
        Path path = Paths.get(txtPath);
        List<String> lines = Files.readAllLines(path);
        lines.forEach(line -> {
            repos.put(line.split(",")[0].strip(), line.split(",")[1].strip());
        });
    }

    public static void main(String[] args) throws Exception{
        Map<String, String> repos = new HashMap<>();
//        addSimpleRepo(repos);
        addReposFromText(repoList, repos);
        repos.forEach((project, url) -> {
            try {
                if(questDoneRepo(project)){
                    logger.info("{} has been analyzed", project);
                    return;
                }
            } catch (Exception e){}
            String path = gitPath + project + "/";
            String outputConflictFiles = output + "/" + "conflictFiles/";
            String outputJsonPath = output + "/" + "mergeTuples" + "/";
            try {
                if(args.length == 0) {
                    logger.info("--------------------------collect conflict files----------------------------------");
                    collectGitConflicts(path, project, url, outputConflictFiles);
                    logger.info("--------------------------collect merge tuples----------------------------------");
                    collectMergeScenario(outputJsonPath, project, outputConflictFiles);
                    logger.info("--------------------------merge tuples analysis----------------------------------");
                    mergeTuplesAnalysis(outputJsonPath + project + ".json");
                }else{
                    if(args[0].contains("1")){
                        logger.info("--------------------------collect conflict files----------------------------------");
                        collectGitConflicts(path, project, url, outputConflictFiles);
                    }
                    if(args[0].contains("2")){
                        logger.info("--------------------------collect merge tuples----------------------------------");
                        collectMergeScenario(outputJsonPath, project, outputConflictFiles);
                    }
                    if(args[0].contains("3")){
                        logger.info("--------------------------merge tuples analysis----------------------------------");
                        mergeTuplesAnalysis(outputJsonPath + project + ".json");
                    }
                }
                tarAndMove(project, outputConflictFiles + project, path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void tarAndMove(String projectName, String filesPath, String projectPath) throws Exception {
        String tarFile = projectName + ".tar.gz";
        logger.info("tar -zcvf {} {}", output + tarFile, filesPath);
        ProcessBuilder pb = new ProcessBuilder(
                "tar",
                "-zcvf",
                output + tarFile,
                filesPath);
        pb.start().waitFor();
        logger.info("mv {} {}", output + tarFile, drivePath);
        ProcessBuilder pb2 = new ProcessBuilder(
                "mv",
                output + tarFile,
                drivePath);
        pb2.start().waitFor();
        recordRepo(projectName);
        deleteRepo(projectPath);
        deleteRepo(filesPath);
    }

    public static boolean questDoneRepo(String projectName) throws IOException {
        Path path = Paths.get(doneList);
        List<String> doneRepos = Files.readAllLines(path);
        return doneRepos.contains(projectName);
    }

    public static void recordRepo(String projectName) throws IOException {
        FileUtils.writeLines(new File(doneList), Collections.singleton(projectName), true);
    }

    public static void deleteRepo(String repoPath) throws IOException {
        FileUtils.deleteDirectory(new File(repoPath));
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
        DatasetFilter df = new DatasetFilter(jsonPath, output + "/" + "conflictTuples");
        df.analysis();
    }
}
