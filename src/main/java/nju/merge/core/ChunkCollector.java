package nju.merge.core;

import difflib.DiffUtils;
import difflib.Patch;
import nju.merge.entity.MergeTuple;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nju.merge.utils.FileUtils.lineFilter;
import static nju.merge.utils.FileUtils.readFile;

public class ChunkCollector {
    private static int[] rec = new int[0];
    public List<MergeTuple> mergeTuples;

    public ChunkCollector(){
        mergeTuples = new ArrayList<>();
    }

    private void alignLines(List<String> conflict, List<String> resolve){
        int n = conflict.size();
        int m = resolve.size();
        if(n > rec.length)
            rec = new int[n];
        Arrays.fill(rec, -1);

        Patch<String> patch = DiffUtils.diff(conflict, resolve);
        List<String> diff = DiffUtils.generateUnifiedDiff("", "", conflict, patch, Math.max(n, m));

        for(int i = 0, j = 0, k = 3; k < diff.size(); ++k){
            char c = diff.get(k).charAt(0);
            if(c == '-')
                i++;
            else if(c == '+')
                j++;
            else{
                rec[i] = j;
                i++;
                j++;
            }
        }
    }

    public List<MergeTuple> extractMergeTuples(File conflictFile, File resolveFile, String fileName) throws Exception {
        List<String> conflict = lineFilter(readFile(conflictFile));
        List<String> resolve = lineFilter(readFile(resolveFile));
        List<String> copy = new ArrayList<>();
        List<MergeTuple> tupleList = new ArrayList<>();

        for (int i = 0, cnt = 0; i < conflict.size(); ++i) {
            if (!conflict.get(i).startsWith("<<<<<<")) {
                copy.add(conflict.get(i));
                cnt++;
                continue;
            }

            MergeTuple tuple = new MergeTuple(fileName);
            tuple.mark = cnt;
            int j = i, k = i;

            while (!conflict.get(k).startsWith("||||||")) {
                k++;
            }
            tuple.ours = getCodeSnippets(conflict, j, k);

            j = k;
            while(!conflict.get(k).startsWith("======")) {
                k++;
            }
            tuple.base = getCodeSnippets(conflict, j, k);

            j = k;
            while(!conflict.get(k).startsWith(">>>>>>")) {
                k++;
            }
            tuple.theirs = getCodeSnippets(conflict, j, k);

            i = k;
            tupleList.add(tuple);
        }

        alignLines(copy, resolve);

        for(MergeTuple tuple : tupleList){
            int mark = tuple.mark;

            if(mark > 0 && mark < copy.size() && rec[mark - 1] != -1 && rec[mark] != -1){
                tuple.resolve = resolve.subList(rec[mark - 1] + 1, rec[mark]);
            }
        }
        return tupleList;
    }

    public List<String> getCodeSnippets(List<String> code, int start, int end){
        if(start >= end)
            return new ArrayList<>();
        return code.subList(start + 1, end);
    }

    public void extractFromProject(String dir) throws IOException {
        Path path = Paths.get(dir);
        Files.walkFileTree(path, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (dir.toString().endsWith(".java")) {
                    File[] files = dir.toFile().listFiles();
                    if (files == null)
                        return FileVisitResult.CONTINUE;

                    File conflict = null, resolve = null;
                    for (File f : files) {
                        if (f.getName().equals("conflict.java"))
                            conflict = f;
                        else if (f.getName().equals("resolve.java"))
                            resolve = f;
                    }
                    if (conflict != null && resolve != null) {

                        try {
                            mergeTuples.addAll(extractMergeTuples(conflict, resolve, dir.toString()));
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
