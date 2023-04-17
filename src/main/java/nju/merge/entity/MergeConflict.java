package nju.merge.entity;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class MergeConflict {
    public RevCommit base;
    public RevCommit resolve;
    public RevCommit ours;
    public RevCommit theirs;

    public String suffix;
    public String commitId;
    public List<String> conflictFiles;

    public MergeConflict(){
        conflictFiles = new ArrayList<>();
    }
}
