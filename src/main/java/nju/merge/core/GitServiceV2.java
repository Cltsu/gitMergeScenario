package nju.merge.core;

import nju.merge.util.GitService;
import nju.merge.util.MergeScenario;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class GitServiceV2 {

    private static final Logger logger = LoggerFactory.getLogger(GitServiceV2.class);

    public Repository CloneIfNotExist(String path, String url) throws Exception {
        File gitFolder = new File(path);
        Repository repo;
        if(gitFolder.exists()) {
            logger.info("git repo is found...........");
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            repo = builder.setGitDir(new File(gitFolder, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
        } else{
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
//                    System.out.println(file);
//                    Iterator<MergeChunk> it = result.iterator();
//                    while(it.hasNext()){
//                        MergeChunk mc = it.next();
//                        if(!mc.getConflictState().equals(MergeChunk.ConflictState.NO_CONFLICT)) {
//                            System.out.println("begin: " + mc.getBegin() + "|end: " + mc.getEnd());
//                        }
//                    }
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
        String gitPath = "D:\\gitProject\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings";
        String path = gitPath + project + "\\";
        GitServiceV2 gs = new GitServiceV2();
        Repository repo = gs.CloneIfNotExist(path,"");
        ObjectId merged = repo.resolve("7bd23716c3bd71ae2f0d07da8e3dd9ef0cea0992");
        RevWalk walk = new RevWalk(repo);
        RevCommit m1 = walk.parseCommit(merged);

        gs.mergeAndGetConflictFiles(m1, repo, project, path, output);
//        List<DiffEntry> entries = gs.diffTwoCommits(c1, c2, repo);
//        entries = gs.filterDiffs(entries);
//        gs.showDiffs(entries);
    }


    public static void test2() throws Exception {
        String gitPath = "D:\\gitProject\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings";
        String path = gitPath + project + "\\";
        GitServiceV2 gs = new GitServiceV2();
//        Repository repo = gs.CloneIfNotExist(path,"");
        gs.threeWayMergeFile("D:\\output\\platform_packages_apps_settings");
//        gs.threeWayMergeFile("G:\\output\\test\\test.java");
    }


    public static void run() throws Exception {
        String gitPath = "D:\\gitProject\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings";
        String path = gitPath + project + "\\";
        GitServiceV2 gs = new GitServiceV2();
        Repository repo = gs.CloneIfNotExist(path,"");

        List<RevCommit> commits = gs.collectMergeCommits(repo);
        Map<String, MergeScenario> map = new HashMap<>();
        for(RevCommit c : commits){
            gs.mergeAndGetConflictFiles(c, repo, project, path, output);
        }
    }

    public static void main(String[] args) throws Exception{
        test2();
    }
}
