package nju.merge.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class IOUtils {
    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

    public static void outputRes2csv(File writeFile, String line) {
        try {
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile, true));
            writeText.append(line);
            writeText.newLine();
            writeText.flush();
            writeText.close();
        } catch (FileNotFoundException e) {
            logger.warn("result output file not found");
        } catch (IOException e) {
            logger.warn("io exception encountered");
        }
    }
}
