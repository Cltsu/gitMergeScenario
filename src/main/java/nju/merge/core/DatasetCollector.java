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

    public List<MergeTuple> extractMergeTuples(File conflict, File resolve) throws Exception {
        List<String> conf = file2StringList(conflict);
        List<String> res = file2StringList(resolve);
        return null;
    }
}
