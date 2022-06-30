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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GitServiceV2 {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);

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


    public List<DiffEntry> diffTwoCommits(RevCommit c1, RevCommit c2, Repository repo) throws Exception {
        logger.info("diff {} and {}", c1.getId(), c2.getId());
        Git git = new Git(repo);

        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, c1.getTree());
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, c2.getTree());

//        DiffFormatter df = new DiffFormatter(new FileOutputStream("D:\\o.txt"));
//        df.setRepository(git.getRepository());
//        List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);

        List<DiffEntry> entries = git.diff()
                .setOutputStream(new FileOutputStream("D:\\o.txt"))
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .call();

        return entries;
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


    public List<DiffEntry> filterDiffs(List<DiffEntry> entries){
        return entries.stream().filter(e ->
            e.getChangeType().equals(DiffEntry.ChangeType.MODIFY) && e.getNewPath().endsWith(".java")
        ).collect(Collectors.toList());
    }


    public static void test1() throws Exception {
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
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
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings";
        String path = gitPath + project + "\\";
        GitServiceV2 gs = new GitServiceV2();
        Repository repo = gs.CloneIfNotExist(path,"");
        RevWalk walk = new RevWalk(repo);
        ObjectId merged = repo.resolve("8a33196571af5d8af43ba2c9ff8dc9cc8ae7dfbe");
        AnyObjectId a = walk.parseAny(merged);
        System.out.println(a.getClass());
    }


    public static void run() throws Exception {
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
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
        run();
    }
}
