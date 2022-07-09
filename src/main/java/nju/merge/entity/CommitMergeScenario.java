package nju.merge.entity;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class CommitMergeScenario {
    public RevCommit base;
    public RevCommit truth;
    public RevCommit ours;
    public RevCommit theirs;

    public String commitId;
    public List<String> conflictFiles;

    public CommitMergeScenario(){
        conflictFiles = new ArrayList<>();
    }
}
