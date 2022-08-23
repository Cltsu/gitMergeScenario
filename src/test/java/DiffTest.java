import difflib.DiffRow;
import difflib.DiffRowGenerator;
import difflib.DiffUtils;
import difflib.Patch;
import nju.merge.entity.MergeTuple;
import org.eclipse.jgit.diff.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nju.merge.Utils.FileUtils.*;


public class DiffTest {

    static List<String> original = new ArrayList<>(){{
        add("A");
        add("B");
        add("C");
        add("D");
    }};

    static List<String> revised = new ArrayList<>(){{
        add("B");
        add("D");
        add("A");
        add("C");
    }};

    @Test
    public void process(){

        Patch<String> patch = DiffUtils.diff(original, revised);


        DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();
        builder.showInlineDiffs(false);
        DiffRowGenerator generator = builder.build();

        List<DiffRow> diffRows = generator.generateDiffRows(original, revised);

        List<String> diff = DiffUtils.generateUnifiedDiff("", "", original, patch, 99999);



        for(var s : diff){
            if(s.endsWith("\n"))
                System.out.print(s);
            else
                System.out.println(s);
        }

//        for(Delta<String> delta : patch.getDeltas()){
//            System.out.println("-".repeat(20));
//            System.out.println(delta);
//            System.out.println(delta.getOriginal().getPosition());
//            System.out.println(delta.getRevised().getPosition());
//        }
    }

    @Test
    public void histogramDiff() throws IOException {
        String dir = "src/main/resources";
        OutputStream out = new ByteArrayOutputStream();
        try {
            RawText rt1 = new RawText(new File(dir + "/conflict.java"));
            RawText rt2 = new RawText(new File(dir + "/truth.java"));

            HistogramDiff algorithm = new HistogramDiff();
            EditList diffList = algorithm.diff(RawTextComparator.DEFAULT, rt1, rt2);

            new DiffFormatter(out).format(diffList, rt1, rt2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] result = out.toString().split("\n");

        for(String s : result){
            System.out.println(s);
        }

    }

    @Test
    public void getDiff() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "git",
                "diff",
                "--patience",
                "--no-index",
                "-U999999999",
                "/Users/zhuyihang/Desktop/GitMergeCollector/src/main/resources/conflict.java",
                "/Users/zhuyihang/Desktop/GitMergeCollector/src/main/resources/truth.java"
        );
        Process process = processBuilder.start();

        try (var reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            List<String> list = reader.lines().collect(Collectors.toList());

            for(String s : list){
                System.out.println(s);
            }
            System.out.println("-------------");
        }
    }

    @Test
    public void lineFilterTest() throws Exception {
        List<String> codes = readFile(new File("src/main/resources/conflict.java"));
        List<String> res = lineFilter(codes);
        writeFile(res, "src/main/resources/tmp.java");
    }

}

