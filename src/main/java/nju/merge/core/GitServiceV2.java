package nju.merge.core;

import nju.merge.util.GitService;
import nju.merge.util.MergeScenario;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
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

        DiffFormatter df = new DiffFormatter(new FileOutputStream("D:\\o.txt"));
        df.setRepository(git.getRepository());
        List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);

//        List<DiffEntry> entries = git.diff()
//                .setOutputStream(new FileOutputStream("D:\\o.txt"))
//                .setNewTree(newTreeIter)
//                .setOldTree(oldTreeIter)
//                .call();

        return entries;
    }

    public void showDiffs(List<DiffEntry> entries){
        for( DiffEntry entry : entries ) {
            System.out.println( entry );
        }

    }


    public static void test1() throws Exception {
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings-bak";
        String path = gitPath + project + "\\";
        GitServiceV2 gs = new GitServiceV2();
        Repository repo = gs.CloneIfNotExist(path,"");

        ObjectId o1 = repo.resolve("58c95f3cf1f02374306c9ddd21c5d320f4417cd2");
        ObjectId o2 = repo.resolve("d89c58724ce0e2e8f5c1b4a308e5999ceb46ffb5");
        RevWalk walk = new RevWalk(repo);
        RevCommit c1 = walk.parseCommit(o1);
        RevCommit c2 = walk.parseCommit(o2);
        List<DiffEntry> entries = gs.diffTwoCommits(c1, c2, repo);
        gs.showDiffs(entries);
    }

    public static void run() throws Exception {
        String gitPath = "D:\\OneDrive - 南京大学\\Project\\github\\";
        String output = "D:\\output\\";
        String project = "platform_packages_apps_settings-bak";
        String path = gitPath + project + "\\";
        GitServiceV2 gs = new GitServiceV2();
        Repository repo = gs.CloneIfNotExist(path,"");

        List<RevCommit> commits = gs.collectMergeCommits(repo);
        Map<String, MergeScenario> map = new HashMap<>();
        for(RevCommit c : commits){

        }
    }

    public static void main(String[] args) throws Exception{
        test1();
    }
}
