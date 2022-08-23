package nju.merge.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MergeScenario {

    public byte[] base;
    public byte[] ours;
    public byte[] theirs;
    public byte[] resolve;

    public String fileName;
    public String project;
    public String commitID;

    private static final Logger logger = LoggerFactory.getLogger(MergeScenario.class);

    public MergeScenario(String project, String commitID, String fileName){
        this.fileName = fileName;
        this.project = project;
        this.commitID = commitID;
    }

    public void write2folder(String path) throws Exception {
        String absPath = path + project + "/" + commitID + "/" + fileName + "/";
        Path p = Paths.get(absPath);
        Files.createDirectories(p);

        logger.info("Writing files to path: {}", absPath);

        write(absPath + "base.java", this.base);
        write(absPath + "ours.java", this.ours);
        write(absPath + "theirs.java", this.theirs);
        write(absPath + "resolve.java", this.resolve);
    }

    private void write(String path, byte[] bytes) throws Exception {
        if(bytes == null)
            return;
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes, 0, bytes.length);
        fos.flush();
        fos.close();
    }
}
