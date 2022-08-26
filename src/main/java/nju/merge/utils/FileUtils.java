package nju.merge.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> readFile(File file) throws Exception {
        List<String> codes = new ArrayList<>();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(isr);

        String line;
        while((line = reader.readLine()) != null){
            codes.add(line);
        }
        return codes;
    }

    public static void writeFile(List<String> codes, String path) throws Exception{
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        StringBuilder sb = new StringBuilder();
        for (String line : codes){
            sb.append(line).append("\n");
        }
        writer.write(sb.toString());
        writer.close();
    }

    public static List<String> lineFilter(List<String> codes){
        List<String> res = new ArrayList<>();
        String[] docs = new String[]{"/*", "*", "*/", "//"};

        for(String line : codes){
            String s = line.stripLeading();
            boolean ok = s.length() != 0;
            for(String d : docs){
                if(s.startsWith(d)) {
                    ok = false;
                    break;
                }
            }
            if(ok)
                res.add(line);
        }
        return res;
    }

}
