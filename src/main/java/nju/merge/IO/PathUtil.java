package nju.merge.IO;

import java.io.File;

public class PathUtil {
    public static String getFileWithPathSegment(String... segments){
        return String.join(File.separator, segments);
    }
}