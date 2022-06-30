package nju.merge.core;

import nju.merge.util.MergeTuple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatasetCollector {
    private List<String> file2StringList(File file) throws Exception {
        List<String> lines = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        BufferedReader bReader = new BufferedReader(reader);
        String tmp = null;
        while((tmp = bReader.readLine()) != null){
            lines.add(tmp);
        }
        return lines;
    }

    public List<MergeTuple> extractMergeTuples(File conflict, File resolve, String commitId, String fileName) throws Exception {
        List<String> conf = file2StringList(conflict);
        List<String> res = file2StringList(resolve);

        List<MergeTuple> tuples = new ArrayList<>();
        for(int i = 0; i < conf.size(); i ++){
            if(conf.get(i).startsWith("<<<<<<")){
                MergeTuple tmp = new MergeTuple(commitId, fileName);
                int j = i, k = i;
                tmp.startLine = j;
                while(!conf.get(++j).startsWith("||||||"));
                tmp.a = getCodeSnippets(conf, k, j);
                k = j;
                while(!conf.get(++j).startsWith("======"));
                tmp.o = getCodeSnippets(conf, k, j);
                k = j;
                while(!conf.get(++j).startsWith(">>>>>>"));
                tmp.b = getCodeSnippets(conf, k, j);
                tmp.endLine = j;
                tuples.add(tmp);
                i = j;
            }
        }
        tuples.forEach(tuple -> {
            List<String> prefix = getCodeSnippets(conf, -1, tuple.startLine);
            List<String> suffix = getCodeSnippets(conf, tuple.endLine, conf.size());
            int startLineRes = alignLine(prefix, res, true);
            int endLineRes = alignLine(suffix, res, false);
            tuple.r = getCodeSnippets(res, startLineRes, endLineRes);
        });

        return tuples;
    }

    public int alignLine(List<String> snippet, List<String> source, boolean reverse){
        return 0;
    }

    public List<String> getCodeSnippets(List<String> source, int start, int end){
        return null;
    }

    public void writeTuples2Json(List<MergeTuple> tuples, String project, String output){

    }


    public void extractFromProject(String project, String path){

    }


    public static void run(){

    }


    public static void main(String[] args) throws Exception{
        run();
    }
}
