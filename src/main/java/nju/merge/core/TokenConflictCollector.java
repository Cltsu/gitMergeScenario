package nju.merge.core;

import com.github.javaparser.GeneratedJavaParserTokenManager;
import com.github.javaparser.SimpleCharStream;
import com.github.javaparser.StringProvider;
import nju.merge.entity.MergeTuple;
import nju.merge.entity.TokenConflict;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static nju.merge.utils.JSONUtils.loadTuplesFromJson;


public class TokenConflictCollector {

    private static final Logger logger = LoggerFactory.getLogger(TokenConflictCollector.class);
    public final static String newLine = "___newLine___";
    public final static String blank = "___blank___";
    private List<MergeTuple> tuples;
    private String output;

    /**
     * @Description 按照json文件路径读取mergeTuple
     * @Param
     * @Return
     **/
    public TokenConflictCollector(String jsonPath, String output) throws Exception {
        this.output = output;
        this.tuples = loadTuplesFromJson(jsonPath);
    }

    /**
     * @Description 对一个每一个tuple，收集token level conflict，最后存入文件
     * @Param
     * @Return
     **/
    public List<TokenConflict> collectTokenConflict() throws Exception{
        List<TokenConflict> tokenConflicts = new ArrayList<>();
        tuples.forEach(t -> {
            try {
                tokenConflicts.addAll(tokenDiff(t));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return tokenConflicts;
    }

    /**
     * @Description 对line-level conflict tokenize然后做token-level merge(diff3)
     * @Param 读取JSON文件得到的tuples list
     * @Return
     **/
    public List<TokenConflict> tokenDiff(MergeTuple tuple) throws IOException {
        String A = flattenRegion(tuple.ours);
        String B = flattenRegion(tuple.theirs);
        String O = flattenRegion(tuple.base);
        String R = flattenRegion(tuple.resolve);

        List<String> aToken = javaParserCodeStr(A);
        List<String> bToken = javaParserCodeStr(B);
        List<String> oToken = javaParserCodeStr(O);
        List<String> rToken = javaParserCodeStr(R);

        File tmpA = new File(this.output + "mergeTmp" + File.separator + "tmpA");
        File tmpB = new File(this.output + "mergeTmp" + File.separator + "tmpB");
        File tmpO = new File(this.output + "mergeTmp" + File.separator + "tmpO");
        FileUtils.writeLines(tmpA, aToken);
        FileUtils.writeLines(tmpB, bToken);
        FileUtils.writeLines(tmpO, oToken);

        ProcessBuilder pb2 = new ProcessBuilder(
                "git",
                "merge-file",
                "--diff3",
                tmpA.getAbsolutePath(),
                tmpO.getAbsolutePath(),
                tmpB.getAbsolutePath()
        );
        try {
            pb2.start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> mergedFile = FileUtils.readLines(tmpA, Charset.defaultCharset());
        List<TokenConflict> tokenConf = splitTokenConflict(mergedFile, rToken);
        showMergedFile(mergedFile, tokenConf, tuple.path);
        FileUtils.delete(tmpA);
        FileUtils.delete(tmpB);
        FileUtils.delete(tmpO);
        return tokenConf;
    }

    private void showMergedFile(List<String> tokens, List<TokenConflict> tokenConf, String path){
        System.out.println(path);
        tokens.replaceAll(token -> token.equals(TokenConflictCollector.newLine) ? "\n" : token);
        System.out.println("Merged file:");
        tokens.forEach(token -> {
            if(token.startsWith(">>>") || token.startsWith("===") || token.startsWith("<<<") || token.startsWith("|||")){
                System.out.print("\n" + token + "\n");
            } else {
                System.out.print(token + " ");
            }
        });
        System.out.println("\ntoken-level conflict:");
        tokenConf.forEach(tc -> {
            System.out.println("---------------");
            System.out.println(tc.toString());
        });

        System.out.println("\n\n------------------------------------------------------------------------------------\n\n");
    }

    /**
     * @Description token merge之后的文件可能含有多个token-level conflict，将它们切分为独立的token-level conflict
     * @Param token merged之后的文件读取为token list
     * @Return 切分之后的list of token-level conflict
     **/
    private List<TokenConflict> splitTokenConflict(List<String> merged, List<String> rToken){
        int start = 0, a = -1, o = -1, b = -1, confEnd = -1, end = 0;
        List<TokenConflict> tcList = new ArrayList<>();
        while(end < merged.size()){
            while(end < merged.size() && !merged.get(end).startsWith("<<<")) end++;
            if(end >= merged.size()) break;
            a = end;
            while(!merged.get(end).startsWith("|||")) end++;
            o = end;
            while(!merged.get(end).startsWith("===")) end++;
            b = end;
            while(!merged.get(end).startsWith(">>>")) end++;
            confEnd = end;
            while(end < merged.size() && !merged.get(end).startsWith("<<<")) end++;
            
            TokenConflict tc = new TokenConflict(
                    merged.subList(start, a),
                    merged.subList(confEnd + 1, end),
                    merged.subList(a + 1, o),
                    merged.subList(o + 1, b),
                    merged.subList(b + 1, confEnd),
                    rToken);
            tcList.add(tc);
            
            start = confEnd + 1;
        }
        return tcList;
    }

    
    private String flattenRegion(List<String> region){
        String flat = "";
        return appendString(flat, region, 0);
    }

    private String appendString(String ret, List<String> candi, int i){
        if(candi.size() - 1 < i) return ret;
        return appendString(ret + " " + TokenConflictCollector.newLine + " " + candi.get(i), candi, i + 1);
    }

    /**
     * 输入：line-Conflict字符串
     * 输出：tokenList列表
     */
    public static List<String> javaParserCodeStr(String lineStr) {

        List<String> tokenList  = new ArrayList<>();

        StringProvider provider = new StringProvider(lineStr);
        SimpleCharStream charStream = new SimpleCharStream(provider);
        GeneratedJavaParserTokenManager tokenGenerate = new GeneratedJavaParserTokenManager(charStream);
        String strToken = tokenGenerate.getNextToken().toString();

        while (!strToken.equals("")){
            tokenList.add(strToken);
            strToken = tokenGenerate.getNextToken().toString();
        }
        return tokenList;
    }

    public static void main(String[] args) throws Exception {
//        TokenConflictCollector tcc = new TokenConflictCollector("G:\\merge\\output\\mergeTuples\\Categories.java.json", "G:\\merge\\output\\");
        TokenConflictCollector tcc = new TokenConflictCollector("G:\\merge\\output\\filteredTuples\\defaultFilter\\junit4.json", "G:\\merge\\output\\");
        tcc.collectTokenConflict();
    }
}
