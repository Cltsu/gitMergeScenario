package nju.merge.core;

import nju.merge.entity.MergeConflict;
import nju.merge.entity.MergeScenario;
import nju.merge.utils.PathUtils;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.RecursiveMerger;
import org.eclipse.jgit.merge.ThreeWayMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ConflictCollector {
    private static final Logger logger = LoggerFactory.getLogger(GitService.class);
    private final String projectName;
    private final String projectPath;
    private final String URL;
    private final String output;
    private Repository repository;

    private final String[] fileType;

    public ConflictCollector(String projectPath, String projectName, String url, String output, String[] fileType) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.URL = url;
        this.output = output;
        this.fileType = fileType;
    }

    /**
     * Get base, ours, theirs, truth and conflict versions of all java source files with conflicts.
     * Conflict files contain conflict blocks.
     */
    public void process() throws Exception {
        GitService service = new GitService();
        repository = service.cloneIfNotExist(this.projectPath, URL);

        List<RevCommit> mergeCommits = service.getMergeCommits(repository);
        List<MergeConflict> conflictList = new ArrayList<>();

        for (RevCommit commit : mergeCommits) {
            mergeAndGetConflict(commit, conflictList);
        }

        for (MergeConflict conflict : conflictList) {
            saveConflictToFiles(conflict);
        }

//        threeWayMergeFile(PathUtils.getFileWithPathSegment(output, projectName));
    }

    private String isTargetFileType(String filename){
        for(String s : fileType){
            if(filename.endsWith(s)) return s;
        }
        return "";
    }
    private void mergeAndGetConflict(RevCommit resolve, List<MergeConflict> conflictList) throws Exception {
        RevCommit ours = resolve.getParents()[0];
        RevCommit theirs = resolve.getParents()[1];

        ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repository, true);

        if (!merger.merge(ours, theirs)) {
            RecursiveMerger rMerger = (RecursiveMerger) merger;
            RevCommit base = (RevCommit) rMerger.getBaseCommitId();

            MergeConflict conflict = new MergeConflict();
            rMerger.getMergeResults().forEach((file, result) -> {
                if (!isTargetFileType(file).equals("") && result.containsConflicts()) {
                    logger.info("file {} added", file);
                    conflict.conflictFiles.add(file);
                }
            });

            if (conflict.conflictFiles.size() != 0) {
                conflict.base = base;
                conflict.ours = ours;
                conflict.theirs = theirs;
                conflict.resolve = resolve;
                conflict.commitId = resolve.getName();
                conflictList.add(conflict);
            }
        }
    }

    private void saveConflictToFiles(MergeConflict conflict){
        Map<String, MergeScenario> scenarioMap = new HashMap<>();
        for (String fileName : conflict.conflictFiles) {
            scenarioMap.put(fileName, new MergeScenario(projectName, conflict.commitId, fileName ));
        }

        if (scenarioMap.size() == 0)
            return;

        RevCommit resolve = conflict.resolve;
        RevCommit base = conflict.base;
        RevCommit ours = conflict.ours;
        RevCommit theirs = conflict.theirs;

//        logger.info("Collecting scenario in merge commit {}", resolve.getName());
        scenarioMap.forEach((file, scenario) -> {
            try {
                scenario.resolve = getFileWithCommitAndPath(file, resolve);
                scenario.ours = getFileWithCommitAndPath(file, ours);
                scenario.theirs = getFileWithCommitAndPath(file, theirs);
                if (isBaseExist(base)) {
                    scenario.base = getFileWithCommitAndPath(file, base);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        scenarioMap.forEach((file, scenario) -> {
            try {
                scenario.write2folder(output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean isBaseExist(ObjectId id) {
        RevWalk walk = new RevWalk(repository);
        try {

            walk.parseAny(id);
        } catch (MissingObjectException e) {
            logger.info("Base not found in {}", id.getName());
//            e.printStackTrace();
            return false;
        } catch (IOException e) {
            logger.warn("A pack file or loose object could not be read!");
            return false;
        }
        return true;
    }

    private byte[] getFileWithCommitAndPath(String path, RevCommit commit) throws IOException {
        TreeWalk treeWalk = TreeWalk.forPath(repository, path, commit.getTree());
        if (treeWalk == null)
            return null;
        ObjectLoader objectLoader = repository.open(treeWalk.getObjectId(0));
        return objectLoader.getBytes();
    }

    private void threeWayMergeFile(String folder) throws IOException {
        Path path = Paths.get(folder);
        Files.walkFileTree(path, new FileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!isTargetFileType(dir.toString()).equals("")) {
                    String suffix = isTargetFileType(dir.toString());
                    File[] files = dir.toFile().listFiles();
                    if (files == null)
                        return FileVisitResult.CONTINUE;

                    File base = null, ours = null, theirs = null, resolve = null;
                    for (File f : files) {
                        String name = f.getName();
                        if(name.equals("base" + suffix)){
                            base = f;
                        } else if(name.equals("ours" + suffix)){
                            ours = f;
                        } else if(name.equals("theirs" + suffix)){
                            theirs = f;
                        } else if(name.equals("resolve" + suffix)){
                            resolve = f;
                        }
                    }
                    if (base != null && ours != null && theirs != null && resolve != null) {
                        File conflict = new File(dir.toString(), "conflict" + suffix);
                        if (conflict.exists())
                            if (!conflict.delete()) {
                                logger.warn("file failed to be deleted");
                            }
                        Files.copy(ours.toPath(), conflict.toPath());

                        // KEY: 会直接将冲突写入 conflict 文件
                        //logger.info("git merge-file --diff3 {} {} {}", conflict.getPath(), base.getPath(), theirs.getPath());
                        ProcessBuilder pb2 = new ProcessBuilder(
                                "git",
                                "merge-file",
                                "--diff3",
                                conflict.getPath(),
                                base.getPath(),
                                theirs.getPath()
                        );
                        try {
                            pb2.start().waitFor();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc){
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
