package nju.merge.core;

import nju.merge.entity.MergeScenario;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);
    private String projectName;
    private String projectPath;
    private String conflictOutput;

    private Repository repo;
    private int totalCommit;
    private int currentCommit;

    public GitService(){};
    public GitService(String projectName, String projectPath, String conflictOutput){
        this.conflictOutput = conflictOutput;
        this.projectPath = projectPath;
        this.projectName = projectName;
    }


    private Repository CloneIfNotExist(String path, String url) throws Exception {
        File gitFolder = new File(path);
        Repository repo;
        if(gitFolder.exists()) {
            logger.info("git repo {} is found...........", path);
        } else{
            logger.info("git clone {} {}", url, projectPath);
            ProcessBuilder pb = new ProcessBuilder(
                    "git",
                    "clone",
                    url,
                    projectPath);
            pb.start().waitFor();
        }
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repo = builder.setGitDir(new File(gitFolder, ".git"))
                .readEnvironment()
                .findGitDir()
                .build();
        return repo;
    }


    public void collectAllConflicts(String projectPath, String projectName, String url, String output) throws Exception{
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.conflictOutput = output;
        this.repo = CloneIfNotExist(this.projectPath,url);
        List<RevCommit> commits = collectMergeCommits();
        this.totalCommit = commits.size();
        this.currentCommit = 0;
        for(RevCommit c : commits){
            currentCommit++;
            mergeAndCollectConflictFiles(c);
        }
        threeWayMergeFile(conflictOutput);
    }


    private List<RevCommit> collectMergeCommits() throws Exception {
        logger.info("collecting merge commits");
        List<RevCommit> commits = new ArrayList<>();
        try (RevWalk revWalk = new RevWalk(repo)) {
            for (Ref ref : repo.getRefDatabase().getRefs()) {
                revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
            }
            for (RevCommit commit : revWalk) {
                if(commit.getParentCount() == 2){
                    commits.add(commit);
                }
            }
        }
        return commits;
    }


    private void mergeAndCollectConflictFiles(RevCommit merged) throws Exception {
        RevCommit p1 = merged.getParents()[0];
        RevCommit p2 = merged.getParents()[1];
        logger.info("({}/{}) merge {} and {}, child commit {}", currentCommit, totalCommit,p1.getName(), p2.getName(), merged.getName());
        ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repo, true);
        if(!merger.merge(p1, p2)){
            RecursiveMerger rMerger = (RecursiveMerger)merger;
            RevCommit base = (RevCommit) rMerger.getBaseCommitId();
            rMerger.getMergeResults().forEach((file, result) -> {
                if(file.endsWith(".java") && result.containsConflicts()){
                    logger.info("conflicts were found in {}", file);
                    MergeScenario ms = new MergeScenario(this.projectName, merged.getName(), file);
                    try {
                        logger.info("collecting scenario in merged commit {}", merged.getName());
                        ms.truth = getFileByCommitAndPath(file, merged);
                        ms.ours = getFileByCommitAndPath(file, p1);
                        ms.theirs = getFileByCommitAndPath(file, p2);
                        if(isBaseExist(base)){
                            ms.base = getFileByCommitAndPath(file, base);
                        }
                        ms.write2folder(this.conflictOutput);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }



    private boolean isBaseExist(ObjectId id) throws IOException {
        RevWalk walk = new RevWalk(this.repo);
        try {
            AnyObjectId a = walk.parseAny(id);
        }catch (MissingObjectException e){
            logger.info("can't find base {}", id.getName());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private byte[] getFileByCommitAndPath(String filePath, RevCommit commit) throws IOException {
        TreeWalk treeWalk = TreeWalk.forPath(this.repo, filePath, commit.getTree());
        if(treeWalk == null) return null;
        ObjectLoader objectLoader = this.repo.open(treeWalk.getObjectId(0));
        return objectLoader.getBytes();
    }

    private byte[] getFileBytes(String path) throws IOException {
        File file = new File(path);
        if(file.exists()) {
            Path curPath = Paths.get(path);
            return Files.readAllBytes(curPath);
        }else{
            return null;
        }
    }


    private void threeWayMergeFile(String dir) throws IOException {
        Path path = Paths.get(dir);
        Files.walkFileTree(path, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.toString().endsWith(".java")) {
                    File[] fs = dir.toFile().listFiles();
                    File base = null, a = null, b = null, truth = null;
                    for (var f : fs) {
                        if (f.getName().equals("base.java")) base = f;
                        else if (f.getName().equals("ours.java")) a = f;
                        else if (f.getName().equals("theirs.java")) b = f;
                        else if (f.getName().equals("truth.java")) truth = f;
                    }
                    if (base != null && a != null && b != null && truth != null) {
                        {
                            File conflict = new File(dir.toString(), "conflict.java");
                            if(conflict.exists()) conflict.delete();
                            Files.copy(a.toPath(), conflict.toPath());
                            logger.info("git merge-file --diff3 {} {} {}", conflict.getPath(), base.getPath(), b.getPath());
                            ProcessBuilder pb2 = new ProcessBuilder(
                                    "git",
                                    "merge-file",
                                    "--diff3",
                                    conflict.getPath(),
                                    base.getPath(),
                                    b.getPath());
                            try {
                                pb2.start().waitFor();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
