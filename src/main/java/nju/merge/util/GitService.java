package nju.merge.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GitService{

    private static final String REMOTE_REFS_PREFIX = "refs/remotes/origin/";
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

    public List<RevCommit> collectMergeCommits3(Repository repo) throws Exception {
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

    public List<RevCommit> collectMergeCommits2(Repository repo) throws Exception {
        logger.info("collecting merge commits");
        Git git = new Git(repo);
        List<RevCommit> commits = new ArrayList<>();
        try {
            Iterable<RevCommit> logs = git.log().all().call();
            for(RevCommit commit : logs){
                if(commit.getParentCount() == 2){
                    commits.add(commit);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return commits;
    }

    public List<RevCommit> collectMergeCommits(Repository repo) throws Exception {
        logger.info("collecting merge commits");
        List<Ref> refs = repo.getRefDatabase().getRefs();
        RevWalk walk = new RevWalk(repo);
        List<RevCommit> commits;
        commits = refs.stream().map(r -> {
            try {
                RevObject obj = walk.parseAny(r.getObjectId());
                if(obj instanceof RevCommit && isMergeCommit((RevCommit)obj)){
                    return (RevCommit)obj;
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).toList();
        return commits;
    }

    public List<Ref> collectMergeCommitRefs(Repository repo) throws Exception {
        List<Ref> refs = repo.getRefDatabase().getRefs();
        RevWalk walk = new RevWalk(repo);
        refs = refs.stream().map(r -> {
            try {
                RevObject obj = walk.parseAny(r.getObjectId());
                if(obj instanceof RevCommit && isMergeCommit((RevCommit)obj)){
                    return r;
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).toList();
        return refs;
    }

    public boolean isMergeCommit(RevCommit commit){
        return commit.getParents().length == 2;
    }


    public MergeResult tryReMerge(RevCommit c1, RevCommit c2, Repository repo) throws Exception{
        logger.info("try to remerge {} and {}", c1.getId(), c2.getId());
        Git git = new Git(repo);
        git.checkout().setAllPaths(true).setName(c1.getName()).call();
        MergeResult result = git.merge().include(c2).call();
        return result;
    }

    public void showConflict(MergeResult result){
        Map<String, int[][]> allConflicts = result.getConflicts();
        if(result.getMergeStatus() != MergeResult.MergeStatus.CONFLICTING) return;
        logger.info("conflicts were found in remerge of {} and {}", result.getMergedCommits()[0].getName(),result.getMergedCommits()[1].getName());
        for (String path : allConflicts.keySet()) {
            int[][] c = allConflicts.get(path);
            System.out.println("Conflicts in file " + path);
            for (int i = 0; i < c.length; ++i) {
                System.out.println("  Conflict #" + i);
                for (int j = 0; j < (c[i].length) - 1; ++j) {
                    if (c[i][j] >= 0)
                        System.out.println("    Chunk for "
                                + result.getMergedCommits()[j] + " starts on line #"
                                + c[i][j]);
                }
            }
        }
        result.getConflicts().forEach((key, value) -> {
            System.out.println(key);
            System.out.println(Arrays.deepToString(value));
        });
//        result.getCheckoutConflicts().forEach(System.out::println);
//        result.getFailingPaths().forEach((key, value) -> {
//            System.out.println("path: " + key);
//            System.out.println("reason: " + value);
//        });
    }

    public void conflictingMergeAbort(MergeResult mr, String path) throws IOException, InterruptedException {
        List<String> commands = new ArrayList<>();
        commands.add("cmd.exe");
        commands.add("/c");
        if(mr.getMergeStatus() != MergeResult.MergeStatus.ALREADY_UP_TO_DATE){
            logger.info("status is : {} . execute git merge --abort", mr.getMergeStatus());
            commands.add("D: & cd \"" + path + "\" & git merge --abort");
        } else{
            logger.info("status is : {} . execute git reset --merge", mr.getMergeStatus());
            commands.add("D: & cd \"" + path + "\" & git reset --merge");
        }
        Process p = new ProcessBuilder(commands).start();
        System.out.println(p.waitFor());
    }


    public void statusAndAbort(MergeResult mr, String path) throws Exception {
        logger.info("execute git status");
        ProcessBuilder pb = new ProcessBuilder("git", "status");
        pb.directory(new File(path));
        pb.start().waitFor();

        logger.info("execute git merge --abort");
        ProcessBuilder pb2 = new ProcessBuilder("git", "merge", "--abort");
        pb2.directory(new File(path));
        pb2.start().waitFor();
    }


    public void mergeReset(String path) throws Exception{
        List<String> commands = new ArrayList<>();
        commands.add("cmd.exe");
        commands.add("/c");
        commands.add("D: & cd \"" + path + "\" & git reset --merge");
        Process p = new ProcessBuilder(commands).start();
        System.out.println(p.waitFor());
    }


    public byte[] getFileBytes(String path) throws IOException {
        File file = new File(path);
        if(file.exists()) {
            Path curPath = Paths.get(path);
            return Files.readAllBytes(curPath);
        }else{
            return null;
        }
    }


    public static void main(String[] args) throws Exception{
        run();
    }


    public static void test1() throws Exception{
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings-bak";
//        String project = "tmp";
        String path = gitPath + project + "\\";
        GitService gs = new GitService();
        Repository repo = gs.CloneIfNotExist(path,"");
        ObjectId o1 = repo.resolve("58c95f3cf1f02374306c9ddd21c5d320f4417cd2");
        ObjectId o2 = repo.resolve("d89c58724ce0e2e8f5c1b4a308e5999ceb46ffb5");
        RevWalk walk = new RevWalk(repo);
        RevCommit c1 = walk.parseCommit(o1);
        RevCommit c2 = walk.parseCommit(o2);
        MergeResult mr = gs.tryReMerge(c1, c2, repo);
        gs.showConflict(mr);
        gs.statusAndAbort(mr, path.substring(0, path.length() - 1));
//        gs.conflictingMergeAbort2(mr, path.substring(0,path.length() - 1));
    }


    public static void test2() throws Exception{
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
        String project = "platform_packages_apps_settings-bak";
        String path = gitPath + project ;

    }


    public static void run() throws Exception {
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings-bak";
//        String project = "tmp";
        String path = gitPath + project + "\\";
        GitService gs = new GitService();
        Repository repo = gs.CloneIfNotExist(path,"");

        List<RevCommit> commits = gs.collectMergeCommits3(repo);
        Map<String, MergeScenario> map = new HashMap<>();
        for(RevCommit c : commits){
            MergeResult mr = gs.tryReMerge(c.getParents()[0],c.getParents()[1],repo);
//            if(mr.getMergeStatus() == MergeResult.MergeStatus.CONFLICTING) {
            if(mr.getConflicts() != null && !mr.getConflicts().keySet().isEmpty()) {
                mr.getConflicts().keySet().forEach(conFile -> {
                    if(conFile.endsWith(".java")){
                        map.put(conFile, new MergeScenario(project, c.getName(), conFile));}});
                map.forEach((f, s) -> {
                    try {
                        s.conflicting = gs.getFileBytes(path + f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                RevCommit base = (RevCommit) mr.getBase();
                gs.showConflict(mr);
//                gs.conflictingMergeAbort(mr, path);
                gs.statusAndAbort(mr, path.substring(0, path.length() - 1));
                map.forEach((f, s) -> {
                    Git git = new Git(repo);
                    try {
                        s.ours = gs.getFileBytes(path + f);
                        git.checkout().setAllPaths(true).setName(c.getParents()[1].getName()).call();
                        s.theirs = gs.getFileBytes(path + f);
                        git.checkout().setAllPaths(true).setName(base.getName()).call();
                        s.base = gs.getFileBytes(path + f);
                        git.checkout().setAllPaths(true).setName(c.getName()).call();
                        s.truth = gs.getFileBytes(path + f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                map.forEach((k ,v ) -> {
                    try {
                        v.write2folder(output);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }else{
                gs.mergeReset(path);
            }
        }
    }
}
