package nju.merge.core;

import nju.merge.entity.MergeConflict;
import nju.merge.entity.MergeScenario;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ConflictCollector {
    private static final Logger logger = LoggerFactory.getLogger(GitService.class);
    private String projectName;
    private String projectPath;
    private String URL;
    private String output;
    private Repository repository;

    public ConflictCollector(String projectPath, String projectName, String url, String output) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.URL = url;
        this.output = output;
    }

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

        threeWayMergeFile(output);
    }

    private void mergeAndGetConflict(RevCommit resolve, List<MergeConflict> conflictList) throws Exception {
        RevCommit ours = resolve.getParents()[0];
        RevCommit theirs = resolve.getParents()[1];
        //logger.info("Merging {} and {}, child commit {}", ours.getName(), theirs.getName(), resolve.getName());

        ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repository, true);

        if (!merger.merge(ours, theirs)) {
            RecursiveMerger rMerger = (RecursiveMerger) merger;
            RevCommit base = (RevCommit) rMerger.getBaseCommitId();

            MergeConflict conflict = new MergeConflict();

            rMerger.getMergeResults().forEach((file, result) -> {
                if (file.endsWith(".java") && result.containsConflicts()) {
                    //logger.info("Conflicts found in {}", file);
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
            scenarioMap.put(fileName, new MergeScenario(projectName, conflict.commitId, fileName));
        }

        if (scenarioMap.size() == 0)
            return;

        RevCommit resolve = conflict.resolve;
        RevCommit base = conflict.base;
        RevCommit ours = conflict.ours;
        RevCommit theirs = conflict.theirs;

        //logger.info("Collecting scenario in merge commit {}", resolve.getName());
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
        } catch (IOException e) {
            //logger.info("Base not found in {}", id.getName());
            e.printStackTrace();
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
                if (dir.toString().endsWith(".java")) {
                    File[] files = dir.toFile().listFiles();
                    File base = null, ours = null, theirs = null, resolve = null;
                    for (File f : files) {
                        String name = f.getName();
                        if(name.equals("base.java"))
                            base = f;
                        else if(name.equals("ours.java"))
                            ours = f;
                        else if(name.equals("theirs.java"))
                            theirs = f;
                        else if(name.equals("resolve.java"))
                            resolve = f;
                    }
                    if (base != null && ours != null && theirs != null && resolve != null) {
                        File conflict = new File(dir.toString(), "conflict.java");
                        if (conflict.exists())
                            conflict.delete();

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
