package nju.merge.core;

import nju.merge.entity.MergeScenario;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);

    public Repository CloneIfNotExist(String path, String url) throws Exception {
        File gitFolder = new File(path);
        Repository repo;
        if(gitFolder.exists()) {
            logger.info("git repo {} is found...........", path);
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            repo = builder.setGitDir(new File(gitFolder, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
        } else{
            logger.info("downloading git repo from {}...........", url);
            Git git = Git.cloneRepository()
                    .setGitDir(gitFolder)
                    .setURI(url)
                    .setCloneAllBranches(true)
                    .call();
            repo = git.getRepository();
        }
        return repo;
    }

    public List<RevCommit> collectMergeCommits(Repository repo) throws Exception {
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


    public void mergeAndGetConflictFiles(RevCommit merged, Repository repo, String project,String projectPath, String output) throws Exception {
        RevCommit p1 = merged.getParents()[0];
        RevCommit p2 = merged.getParents()[1];
        logger.info("merge {} and {}, merged commit {}", p1.getName(), p2.getName(), merged.getName());
        ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repo, true);
        if(!merger.merge(p1, p2)){
            RecursiveMerger rMerger = (RecursiveMerger)merger;
            RevCommit base = (RevCommit) rMerger.getBaseCommitId();
            Map<String, MergeScenario> scenarioMap = new HashMap<>();
            rMerger.getMergeResults().forEach((file, result) -> {
                if(file.endsWith(".java") && result.containsConflicts()){
                    logger.info("conflicts were found in {}", file);
                    scenarioMap.put(file, new MergeScenario(project, merged.getName(), file));
                }
            });
            if(scenarioMap.size() == 0) return;
            logger.info("collecting scenario for merged commit {}", merged.getName());
            checkout(merged, repo);
            scenarioMap.forEach((file, scenario) ->{
                try {
                    scenario.truth = getFileBytes(projectPath + file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            checkout(p1, repo);
            scenarioMap.forEach((file, scenario) ->{
                try {
                    scenario.ours = getFileBytes(projectPath + file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            checkout(p2, repo);
            scenarioMap.forEach((file, scenario) ->{
                try {
                    scenario.theirs = getFileBytes(projectPath + file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if(isBaseExist(base, repo)) {
                checkout(base, repo);
                scenarioMap.forEach((file, scenario) -> {
                    try {
                        scenario.base = getFileBytes(projectPath + file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            scenarioMap.forEach((f, s)-> {
                try {
                    s.write2folder(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void checkout(RevCommit commit, Repository repo) throws Exception {
        Git git = new Git(repo);
        git.checkout().setName(commit.getName()).call();
    }

    public boolean isBaseExist(ObjectId id, Repository repo) throws IOException {
        RevWalk walk = new RevWalk(repo);
        try {
            AnyObjectId a = walk.parseAny(id);
        }catch (MissingObjectException e){
            logger.info("can't find base {}", id.getName());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public byte[] getFileBytes(String path) throws IOException {
        path = path.replace('/','\\');
        File file = new File(path);
        if(file.exists()) {
            Path curPath = Paths.get(path);
            return Files.readAllBytes(curPath);
        }else{
            return null;
        }
    }


    public void threeWayMergeFile(String dir) throws IOException {
        Path path = Paths.get(dir);
        Files.walkFileTree(path, new FileVisitor<>() {
            private int scenarioCount = 0;
            private int tupleCount = 0;
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.toString().endsWith(".java")) {
                    logger.info("scenario count : {}", scenarioCount++);
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
                            logger.info("tuple count : {}", tupleCount++);
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
    
    public static void test1() throws Exception {
        String gitPath = "G:\\merge\\gitRepos\\";
        String output = "G:\\merge\\output\\";
        String project = "junit4";
        String path = gitPath + project + "\\";
        GitService gs = new GitService();
        Repository repo = gs.CloneIfNotExist(path,"");

        ObjectId m2 = repo.resolve("baf1ea01ec9c1ca2ba3091c493f0aedeab725560");
        ObjectId m3 = repo.resolve("b1cf4b5bc6ead8c3dfcbbb9ebe69be85f5e53cb8");
        RevWalk walk = new RevWalk(repo);

        RevCommit r2 = walk.parseCommit(m2);
        RevCommit r3 = walk.parseCommit(m3);


        gs.mergeAndGetConflictFiles(r2, repo, project, path, output);
        gs.mergeAndGetConflictFiles(r3, repo, project, path, output);
    }


    public static void test2() throws Exception {
        String gitPath = "D:\\gitProject\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings";
        String path = gitPath + project + "\\";
        GitService gs = new GitService();
//        Repository repo = gs.CloneIfNotExist(path,"");
        gs.threeWayMergeFile("D:\\output\\platform_packages_apps_settings");
//        gs.threeWayMergeFile("G:\\output\\test\\test.java");
    }

    public static void run() throws Exception {
        String gitPath = "G:\\merge\\gitRepos\\";
        String output = "G:\\merge\\output\\";
        String project = "junit4";
        String path = gitPath + project + "\\";
        GitService gs = new GitService();
        Repository repo = gs.CloneIfNotExist(path,"");

        List<RevCommit> commits = gs.collectMergeCommits(repo);
        Map<String, MergeScenario> map = new HashMap<>();
        for(RevCommit c : commits){
            gs.mergeAndGetConflictFiles(c, repo, project, path, output);
        }
    }

    public static void main(String[] args) throws Exception{
        run();
    }
}
