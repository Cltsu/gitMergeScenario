package nju.merge.client;

import nju.merge.core.ChunkCollector;
import nju.merge.core.ConflictCollector;
import nju.merge.core.ChunkFilter;
import nju.merge.utils.JSONUtils;
import nju.merge.utils.PathUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {


    private static String workdir = "G:/merge";
    private static final String reposDir = workdir + "/repos";   // store all the repos
    private static final String outputDir = workdir + "/output";
    private static final String repoList = workdir + "/list.txt";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void addReposFromText(String txtPath, Map<String, String> repos) throws IOException {
        Path path = Paths.get(txtPath);
        List<String> lines = Files.readAllLines(path);
        lines.forEach(line -> {
            String[] args = line.split(",");
            repos.put(args[0].strip(), args[1].strip());
        });
    }

    public static void main(String[] args) throws Exception{
        Map<String, String> repos = new HashMap<>();
        addReposFromText(repoList, repos);
        if(args.length == 2){
            workdir = args[1];
        }
        repos.forEach((projectName, url) -> {
            String repoPath = PathUtils.getFileWithPathSegment(reposDir, projectName); // store the specific repo
            String outputConflictPath = PathUtils.getFileWithPathSegment(outputDir, "conflictFiles");   // store all conflict files during collecting
            String outputJsonPath = PathUtils.getFileWithPathSegment(outputDir, "mergeTuples"); // store output tuples
            String filteredTuplePath = PathUtils.getFileWithPathSegment(outputDir, "filteredTuples"); // store filtered tuples

            try {
                if(args.length == 0) {
                    logger.info("-------------------------- Collect conflict files ----------------------------------");
                    collectMergeConflict(repoPath, projectName, url, outputConflictPath);

                    logger.info("-------------------------- Collect merge tuples ----------------------------------");
                    collectMergeTuples(outputJsonPath, projectName, outputConflictPath);

                    logger.info("-------------------------- Merge tuples analysis ----------------------------------");
                    mergeTuplesAnalysis(PathUtils.getFileWithPathSegment(outputJsonPath, projectName + ".json"), projectName, filteredTuplePath);
                }else{
                    if(args[0].contains("1")){
                        logger.info("-------------------------- Collect conflict files ----------------------------------");
                        collectMergeConflict(repoPath, projectName, url, outputConflictPath);
                    }
                    if(args[0].contains("2")){
                        logger.info("-------------------------- Collect merge tuples ----------------------------------");
                        collectMergeTuples(outputJsonPath, projectName, outputConflictPath);
                    }
                    if(args[0].contains("3")){
                        logger.info("-------------------------- Merge tuples analysis ----------------------------------");
                        mergeTuplesAnalysis(PathUtils.getFileWithPathSegment(outputJsonPath, projectName + ".json"), projectName, filteredTuplePath);
                    }
                }
//                deleteRepo(repoPath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void deleteRepo(String repoPath) throws IOException {
        FileUtils.deleteDirectory(new File(repoPath));
    }

    public static void collectMergeConflict(String projectPath, String projectName, String url, String output) throws Exception {
        ConflictCollector collector = new ConflictCollector(projectPath, projectName, url, output);
        collector.process();
    }

    public static void collectMergeTuples(String outputFile, String projectName, String conflictFilesPath) throws Exception {
        ChunkCollector collector = new ChunkCollector();
        collector.extractFromProject(PathUtils.getFileWithPathSegment(conflictFilesPath, projectName));
        JSONUtils.writeTuples2Json(collector.mergeTuples, projectName, outputFile);
    }

    public static void mergeTuplesAnalysis(String jsonPath, String projectName, String outputDir) throws Exception {
        ChunkFilter filter = new ChunkFilter(jsonPath, projectName, outputDir);
        filter.analysis();
//        filter.analysisDefault();
    }
}
