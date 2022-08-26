package nju.merge.entity;

import nju.merge.utils.PathUtils;

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

    //    private static final Logger logger = LoggerFactory.getLogger(MergeScenario.class);
    public MergeScenario(String project, String commitID, String fileName){
        this.fileName = fileName;
        this.project = project;
        this.commitID = commitID;
    }

    public void write2folder(String path) throws Exception {
        String absPath = PathUtils.getFileWithPathSegment(path, project, commitID, fileName);
        Path p = Paths.get(absPath);
        Files.createDirectories(p);

//        logger.info("Writing files to path: {}", absPath);

        write(PathUtils.getFileWithPathSegment(absPath, "base.java"), this.base);
        write(PathUtils.getFileWithPathSegment(absPath, "ours.java"), this.ours);
        write(PathUtils.getFileWithPathSegment(absPath, "theirs.java"), this.theirs);
        write(PathUtils.getFileWithPathSegment(absPath, "resolve.java"), this.resolve);
    }

    private void write(String path, byte[] bytes) throws Exception {
        if (bytes == null)
            return;
        File file = new File(path);
        if (file.exists()) {
            if (!file.delete()) {
                throw new Exception("file failed to be deleted");
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes, 0, bytes.length);
        fos.flush();
        fos.close();
    }
}
