package nju.merge.entity;

import java.util.ArrayList;
import java.util.List;

public class MergeTuple {

    public int mark;
    public List<String> ours;
    public List<String> theirs;
    public List<String> base;
    public List<String> resolve;

    public String path;

    public MergeTuple(){
        this.ours = new ArrayList<>();
        this.theirs = new ArrayList<>();
        this.base = new ArrayList<>();
        this.resolve = new ArrayList<>();
    }

    public MergeTuple(String filePath) {
        this();
        this.path = filePath;
    }
}
