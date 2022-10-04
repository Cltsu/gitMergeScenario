package nju.merge.core;

import nju.merge.entity.ResolutionLabel;
import nju.merge.entity.TokenConflict;
import nju.merge.utils.PathUtils;

import java.util.*;

import static nju.merge.utils.StringUtils.concat;

public class LabelAnalysis {

    private final static Map<ResolutionLabel, Integer> count = new HashMap<>();
    private static boolean found = false;

    /**
     * 收集对应 jsonPath 下tuple中的 token conflict，并将收集结果写入conflict，同时静态成员 count 记录不同 resolution label数量
     *
     * @param tuplePath 存放 line-level conflict tuple的地址
     * @param output   用于存放输出（临时文件）的根目录
     */
    public void process(String tuplePath, String projectName, String output) throws Exception {

        TokenConflictCollector collector = new TokenConflictCollector(tuplePath, projectName, output);
        List<TokenConflict> tokenConflicts = collector.collectTokenConflict();

        for (TokenConflict conflict : tokenConflicts) {
            found = false;

            List<String> resolution = conflict.resolution;

            if (query(concat(conflict.prefix, conflict.suffix, conflict.aRegion, conflict.bRegion), resolution))
                setValue(ResolutionLabel.CONCAT_AB, conflict);
            else if (query(concat(conflict.prefix, conflict.suffix, conflict.bRegion, conflict.aRegion), resolution))
                setValue(ResolutionLabel.CONCAT_BA, conflict);
            else if (query(conflict.a, resolution))
                setValue(ResolutionLabel.ACCEPT_A, conflict);
            else if (query(conflict.b, resolution))
                setValue(ResolutionLabel.ACCEPT_B, conflict);
            else if (query(conflict.o, resolution))
                setValue(ResolutionLabel.ACCEPT_BASE, conflict);

            if (!found) {
                List<String> aa = join(conflict.a);
                List<String> bb = join(conflict.b);
                List<String> oo = join(conflict.o);
                List<String> rr = join(conflict.resolution);

                setValue(ResolutionLabel.NONE, conflict);
            }
        }
    }

    private List<String> join(List<String> regions){
        List<String> res = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for(String s : regions){
            if(s.equals("___newLine___")) {
                res.add(builder.toString());
                builder.setLength(0);
            }else{
                builder.append(s);
            }
        }
        return res;
    }

    public static void setValue(ResolutionLabel label, TokenConflict conflict) {
        found = true;
        conflict.label = label;
        count.put(label, count.getOrDefault(label, 0) + 1); // 统计
    }

    public boolean query(List<String> tar, List<String> source) {
        int n = source.size(), m = tar.size();
        for (int i = 0; i + m <= n; ++i) {
            if (source.subList(i, i + m).equals(tar))
                return true;
        }

        return false;
    }

    public static void main(String[] args) throws Exception {
        LabelAnalysis labelAnalysis = new LabelAnalysis();
        String output = "/Users/zhuyihang/Desktop/experiments/output";
        labelAnalysis.process(
                PathUtils.getFileWithPathSegment(output, "filteredTuples", "defaultFilter"),
                "spring-boot",
                PathUtils.getFileWithPathSegment(output, "tokenConflicts"));
        Map<ResolutionLabel, Integer> countMap = LabelAnalysis.count;
        Set<Map.Entry<ResolutionLabel, Integer>> ms = countMap.entrySet();
        for (Map.Entry entry : ms) {
            System.out.print(entry);
            System.out.println("\n----------------------");
        }
    }
}