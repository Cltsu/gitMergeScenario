package nju.merge.IO;

import java.io.File;

public class PathUtil {
    public static String getFileWithPathSegment(String... segments){
        return getSystemCompatiblePath(String.join(File.separator, segments));
    }

    public static String getSystemCompatiblePath(String path){
        String compatiblePath;
        if(File.separator.equals("/")){
            compatiblePath = path.replace('\\','/');
        }else{
            compatiblePath = path.replace('/','\\');
        }
        return compatiblePath;
    }
}