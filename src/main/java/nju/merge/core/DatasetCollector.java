package nju.merge.core;

import nju.merge.entity.MergeTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DatasetCollector {

    private static final Logger logger = LoggerFactory.getLogger(DatasetCollector.class);
    public List<MergeTuple> allTuple;

    public DatasetCollector(){
        allTuple = new ArrayList<>();
    }

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
        logger.info("extract from {}", fileName);
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


    public int alignLine(List<String> prefix, List<String> resolve, boolean reverse){
        if(prefix.isEmpty()){
            return reverse ? -1 : resolve.size();
        }
        List<String> snippet = new ArrayList<>();
        List<String> source = new ArrayList<>();
        if(reverse){
            for(int i = prefix.size() - 1; i >= 0; i--) snippet.add(prefix.get(i));
            for(int i = resolve.size() - 1; i >= 0; i--) source.add(resolve.get(i));
        }else{
            snippet.addAll(prefix);
            source.addAll(resolve);
        }
        int ret = 0;
        int maxAlign = -1;
        for(int i = 0; i < source.size(); i++){
            if(maxAlign >= 5) break;
            if(source.get(i).equals(snippet.get(0))){
                int j = i, k = 0;
                while(++j < source.size() && ++k < snippet.size() && source.get(j).equals(snippet.get(k)));
                if(k > maxAlign){
                    maxAlign = k;
                    ret = i;
                }
            }
        }
        if(reverse){
            return source.size() - ret - 1;
        }else return ret;
    }

    public List<String> getCodeSnippets(List<String> source, int start, int end){
        if(start >= end) return new ArrayList<>();
        return source.subList(start + 1, end);
    }


    public void extractFromProject(String dir) throws IOException {
        Path path = Paths.get(dir);
        Files.walkFileTree(path, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.toString().endsWith(".java")) {
                    File[] fs = dir.toFile().listFiles();
                    File conflict = null, resolve = null;
                    for (var f : fs) {
                        if (f.getName().equals("conflict.java")) conflict = f;
                        else if (f.getName().equals("truth.java")) resolve = f;
                    }
                    if (conflict != null && resolve != null) {
                        try {
                            allTuple.addAll(extractMergeTuples(conflict, resolve, "", dir.toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
