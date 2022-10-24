package nju.merge.client;

import nju.merge.core.ConflictCollector;
import nju.merge.core.DatasetCollector;
import nju.merge.core.DatasetFilter;
import nju.merge.entity.CollectRecord;
import nju.merge.utils.IOUtils;
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
import java.util.*;

public class Client {
    private static final String workdir = ".";
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

    public static void main(String[] args) throws Exception {
        Map<String, String> repos = new HashMap<>();
        addReposFromText(repoList, repos);
        File progressFile = new File(PathUtils.getFileWithPathSegment("log", "progress_out_" + new Date().getTime() + ".csv"));
        IOUtils.outputRes2csv(progressFile, CollectRecord.getHeaders());

        repos.forEach((projectIdentification, url) -> {
            String repoPath = PathUtils.getFileWithPathSegment(reposDir, projectIdentification); // store the specific repo
            String outputConflictPath = PathUtils.getFileWithPathSegment(outputDir, "conflictFiles");   // store all conflict files during collecting
            String outputJsonPath = PathUtils.getFileWithPathSegment(outputDir, "mergeTuples"); // store output tuples
            String filteredTuplePath = PathUtils.getFileWithPathSegment(outputDir, "filteredTuples"); // store filtered tuples

            CollectRecord record = new CollectRecord(projectIdentification);
            try {
                if (args.length == 0) {
                    logger.info("-------------------------- Collect conflict files ----------------------------------");
                    record.setMerge_commits(collectMergeConflict(repoPath, projectIdentification, url, outputConflictPath));

                    logger.info("-------------------------- Collect merge tuples ----------------------------------");
                    collectMergeTuples(outputJsonPath, projectIdentification, outputConflictPath);

                    logger.info("-------------------------- Merge tuples analysis ----------------------------------");
                    mergeTuplesAnalysis(PathUtils.getFileWithPathSegment(outputJsonPath, projectIdentification), projectIdentification, filteredTuplePath, record);
                } else {
                    if (args[0].contains("1")) {
                        logger.info("-------------------------- Collect conflict files ----------------------------------");
                        record.setMerge_commits(collectMergeConflict(repoPath, projectIdentification, url, outputConflictPath));
                    }
                    if (args[0].contains("2")) {
                        logger.info("-------------------------- Collect merge tuples ----------------------------------");
                        collectMergeTuples(outputJsonPath, projectIdentification, outputConflictPath);
                    }
                    if (args[0].contains("3")) {
                        logger.info("-------------------------- Merge tuples analysis ----------------------------------");
                        mergeTuplesAnalysis(PathUtils.getFileWithPathSegment(outputJsonPath, projectIdentification), projectIdentification, filteredTuplePath, record);
                    }
                }
//                deleteRepo(repoPath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            IOUtils.outputRes2csv(progressFile, record.toString());
        });
    }

    public static void deleteRepo(String repoPath) throws IOException {
        FileUtils.deleteDirectory(new File(repoPath));
    }

    public static int collectMergeConflict(String projectPath, String projectIdentification, String url, String output) throws Exception {
        ConflictCollector collector = new ConflictCollector(projectPath, projectIdentification, url, output);
        return collector.process();
    }

    public static void collectMergeTuples(String outputDir, String projectIdentification, String conflictFilesPath) throws Exception {
        DatasetCollector collector = new DatasetCollector();
        collector.extractFromProject(PathUtils.getFileWithPathSegment(conflictFilesPath, projectIdentification));
        JSONUtils.writeTuples2Json(collector.mergeTuples, projectIdentification, outputDir);
    }

    public static void mergeTuplesAnalysis(String jsonDir, String projectIdentification, String outputDir, CollectRecord record) throws Exception {
        DatasetFilter filter = new DatasetFilter(jsonDir, projectIdentification, outputDir);
        filter.analysis(record);
//        filter.analysisDefault();
    }
}
