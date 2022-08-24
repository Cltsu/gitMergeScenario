package nju.merge.entity;

import java.util.ArrayList;
import java.util.List;

public class MergeTuple {
    public int startLine;
    public int endLine;
    public List<String> a;
    public List<String> b;
    public List<String> o;
    public List<String> r;

    public String path;
    public String commitId;

    public MergeTuple(){
        this.a = new ArrayList<>();
        this.b = new ArrayList<>();
        this.o = new ArrayList<>();
        this.r = new ArrayList<>();
    }

    public MergeTuple(String commitId, String filePath){
        this.commitId = commitId;
        this.path = filePath;
        this.a = new ArrayList<>();
        this.b = new ArrayList<>();
        this.o = new ArrayList<>();
        this.r = new ArrayList<>();
    }
}
