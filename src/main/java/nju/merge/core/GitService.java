package nju.merge.core;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);

    public GitService(){}

    public Repository cloneIfNotExist(String path, String url) throws Exception {
        File gitFolder = new File(path);
        Repository repo;
        if(gitFolder.exists()) {
            logger.info("git repo {} is found...........", path);
        } else{
            logger.info("git cloning to {} from {} ...... ", path, url);
            ProcessBuilder pb = new ProcessBuilder(
                    "git",
                    "clone",
                    url,
                    path);
            pb.start().waitFor();
        }
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repo = builder.setGitDir(new File(gitFolder, ".git"))
                .readEnvironment()
                .findGitDir()
                .build();
        return repo;
    }


    public List<RevCommit> getMergeCommits(Repository repository) throws Exception {
        logger.info("Collecting merge commits");

        List<RevCommit> commits = new ArrayList<>();

        try (RevWalk revWalk = new RevWalk(repository)) {
            for (Ref ref : repository.getRefDatabase().getRefs()) {
                revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
            }
            for (RevCommit commit : revWalk) {
                if (commit.getParentCount() == 2) {
                    commits.add(commit);
                }
            }
        }
        System.out.println("=============================================================" + commits.size());
        return commits;
    }


}
