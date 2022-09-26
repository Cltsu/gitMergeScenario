package nju.merge.core;

import nju.merge.client.Client;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConflictCollector {
    private static final Logger logger = LoggerFactory.getLogger(GitService.class);
    private final String projectName;
    private final String projectPath;
    private final String URL;
    private final String output;
    private Repository repository;

    private static Long cnt = 0L;

    public ConflictCollector(String projectPath, String projectName, String url, String output) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.URL = url;
        this.output = output;
    }

    /**
     * Get base, ours, theirs, truth and conflict versions of all java source files with conflicts.
     * Conflict files contain conflict blocks.
     */
    public void process() throws Exception {
        GitService service = new GitService();
        repository = service.cloneIfNotExist(this.projectPath, URL);

        List<RevCommit> mergeCommits = service.getMergeCommits(repository);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        Long start = System.currentTimeMillis();

        for (RevCommit commit : mergeCommits){
            executor.submit(() -> {
                try {
                    mergeAndCollectConflictFiles(commit);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        while(!executor.isTerminated());

        Long end = System.currentTimeMillis();
        System.out.println(1.0 * (end - start) / 60000);

        threeWayMergeFile(PathUtils.getFileWithPathSegment(output, projectName));
    }

    private void mergeAndCollectConflictFiles(RevCommit merged) throws Exception {
        RevCommit p1 = merged.getParents()[0];
        RevCommit p2 = merged.getParents()[1];
        logger.info("merge {} and {}, child commit {}", p1.getName(), p2.getName(), merged.getName());
        ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repository, true);

        if(!merger.merge(p1, p2)){
            RecursiveMerger rMerger = (RecursiveMerger)merger;
            RevCommit base = (RevCommit) rMerger.getBaseCommitId();

            rMerger.getMergeResults().forEach((file, result) -> {
                if(file.endsWith(".java") && result.containsConflicts()) {
                    logger.info("conflicts were found in {}", file);
                    MergeScenario scenario = new MergeScenario(this.projectName, merged.getName(), file);
                    try {
                        logger.info("collecting scenario in merged commit {}", merged.getName());
                        scenario.resolve = getFileByCommitAndPath(file, merged);
                        scenario.ours = getFileByCommitAndPath(file, p1);
                        scenario.theirs = getFileByCommitAndPath(file, p2);

                        if(isBaseExist(base)){
                            scenario.base = getFileByCommitAndPath(file, base);
                        }
                        scenario.write2folder(output);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    private boolean isBaseExist(ObjectId id) {
        RevWalk walk = new RevWalk(repository);
        try {
            walk.parseAny(id);
        } catch (MissingObjectException e) {
            logger.info("Base not found in {}", id.getName());
            return false;
        } catch (IOException e) {
            logger.warn("A pack file or loose object could not be read!");
            return false;
        }
        return true;
    }

    private byte[] getFileByCommitAndPath(String path, RevCommit commit) throws IOException {
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
                    if (files == null)
                        return FileVisitResult.CONTINUE;

                    File base = null, ours = null, theirs = null, resolve = null;
                    for (File f : files) {
                        String name = f.getName();
                        switch (name) {
                            case "base.java":
                                base = f;
                                break;
                            case "ours.java":
                                ours = f;
                                break;
                            case "theirs.java":
                                theirs = f;
                                break;
                            case "resolve.java":
                                resolve = f;
                                break;
                        }
                    }
                    if (base != null && ours != null && theirs != null && resolve != null) {
                        File conflict = new File(dir.toString(), "conflict.java");
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
