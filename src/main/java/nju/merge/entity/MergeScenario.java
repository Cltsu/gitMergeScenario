package nju.merge.entity;

import nju.merge.core.GitService;
import nju.merge.utils.PathUtils;
import org.eclipse.jgit.api.MergeCommand;
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
    static public int count = 0;

    //    private static final Logger logger = LoggerFactory.getLogger(MergeScenario.class);
    public MergeScenario(String project, String commitID, String fileName){
        this.fileName = fileName;
        this.project = project;
        this.commitID = commitID;
    }

    public void write2folder(String path) throws Exception {
        String absPath = PathUtils.getFileWithPathSegment(path, project);
        Path p = Paths.get(absPath);
        Files.createDirectories(p);
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
//        logger.info("Writing files to path: {}", absPath);
        String basePath = PathUtils.getFileWithPathSegment(absPath, count + "_base" + suffix);
        String aPath = PathUtils.getFileWithPathSegment(absPath, count + "_a" + suffix);
        String bPath = PathUtils.getFileWithPathSegment(absPath, count + "_b" + suffix);
        String rPath = PathUtils.getFileWithPathSegment(absPath, count + "_resolved" + suffix);
        write(basePath, this.base);
        write(aPath, this.ours);
        write(bPath, this.theirs);
        write(rPath, this.resolve);
        File baseF = new File(basePath);
        File bF = new File(bPath);
        File aF = new File(aPath);
        File rF = new File(rPath);
        if (baseF.exists() && bF.exists() && aF.exists() && rF.exists()) {
            write(PathUtils.getFileWithPathSegment(absPath, count + "_merged" + suffix), this.ours);
            ProcessBuilder pb2 = new ProcessBuilder(
                    "git",
                    "merge-file",
                    "--diff3",
                    PathUtils.getFileWithPathSegment(absPath, count + "_merged" + suffix),
                    basePath,
                    bPath
            );
            try {
                pb2.start().waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        count++;
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
