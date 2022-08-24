package nju.merge.client;

import nju.merge.Utils.JSONUtils;
import nju.merge.Utils.PathUtil;
import nju.merge.core.ConflictCollector;
import nju.merge.core.DatasetCollector;
import nju.merge.core.DatasetFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Client {

    private static final String reposDir = "./repos";   // store all the repos
    private static final String outputDir = "./output";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void addSimpleRepo(Map<String, String> repos) {
        repos.put("junit4", "https://github.com/junit-team/junit4.git");
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> repos = new HashMap<>();
        addSimpleRepo(repos);

        repos.forEach((projectName, url) -> {
            String repoPath = PathUtil.getFileWithPathSegment(reposDir, projectName); // store the specific repo
            String outputConflictPath = PathUtil.getFileWithPathSegment(outputDir, "conflictFiles");   // store all conflict files during collecting
            String outputJsonPath = PathUtil.getFileWithPathSegment(outputDir, "mergeTuples"); // store output tuples
            String filteredTuplePath = PathUtil.getFileWithPathSegment(outputDir, "filteredTuples"); // store filtered tuples
            try {
                logger.info("-------------------------- Collect conflict files ----------------------------------");
                collectMergeConflict(repoPath, projectName, url, outputConflictPath);

                logger.info("-------------------------- Collect merge tuples ----------------------------------");
                collectMergeTuples(outputJsonPath, projectName, outputConflictPath);

                logger.info("-------------------------- Merge tuples analysis ----------------------------------");
                mergeTuplesAnalysis(PathUtil.getFileWithPathSegment(outputJsonPath, projectName + ".json"), projectName, filteredTuplePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Get base, ours, theirs, truth and conflict versions of all java source files with conflicts.
     * Conflict files contain conflict blocks.
     */
    public static void collectMergeConflict(String projectPath, String projectName, String url, String output) throws Exception {
        ConflictCollector collector = new ConflictCollector(projectPath, projectName, url, output);
        collector.process();
    }

    public static void collectMergeTuples(String outputFile, String projectName, String conflictFilesPath) throws Exception {
        DatasetCollector collector = new DatasetCollector();
        collector.extractFromProject(conflictFilesPath);
        JSONUtils.writeTuples2Json(collector.mergeTuples, projectName, outputFile);
    }

    public static void mergeTuplesAnalysis(String jsonPath, String projectName, String outputDir) throws Exception {
        DatasetFilter filter = new DatasetFilter(jsonPath, projectName, outputDir);
        filter.analysis();
    }
}
