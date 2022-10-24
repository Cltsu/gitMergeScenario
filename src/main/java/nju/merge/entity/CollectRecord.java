package nju.merge.entity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectRecord {
    private static final List<Field> fields = Arrays.stream(CollectRecord.class.getDeclaredFields())
            .filter(f -> !f.getName().equals("fields"))
            .collect(Collectors.toList());

    public static String getHeaders() {
        return fields.stream().map(Field::getName).reduce((a, b) -> a + "," + b).orElse("");
    }

    @Override
    public String toString() {
        return fields.stream().map(field -> {
            field.setAccessible(true);
            try {
                return field.get(this).toString();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).reduce((a, b) -> a + "," + b).orElse("");
    }

    String project_name = "";
    Integer merge_commits = 0;
    Integer total_tuples = 0;
    Integer accept_one_side = 0;
    Integer lack_of_resolution = 0;
    Integer complete_tuples = 0;
    Integer concat = 0;
    Integer mixline = 0;
    Integer out_of_vocabulary = 0;
    Integer no_base = 0;

    public CollectRecord(String project_name) {
        this.project_name = project_name;
    }

    public CollectRecord() {
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public void setMerge_commits(Integer merge_commits) {
        this.merge_commits = merge_commits;
    }

    public void setTotal_tuples(Integer total_tuples) {
        this.total_tuples = total_tuples;
    }

    public void setAccept_one_side(Integer accept_one_side) {
        this.accept_one_side = accept_one_side;
    }

    public void setLack_of_resolution(Integer lack_of_resolution) {
        this.lack_of_resolution = lack_of_resolution;
    }

    public void setComplete_tuples(Integer complete_tuples) {
        this.complete_tuples = complete_tuples;
    }

    public void setConcat(Integer concat) {
        this.concat = concat;
    }

    public void setMixline(Integer mixline) {
        this.mixline = mixline;
    }

    public void setOut_of_vocabulary(Integer out_of_vocabulary) {
        this.out_of_vocabulary = out_of_vocabulary;
    }

    public void setNo_base(Integer no_base) {
        this.no_base = no_base;
    }

    public static void main(String[] args) {
        CollectRecord record = new CollectRecord();
        System.out.println(CollectRecord.getHeaders());
        System.out.println(record);
//        Arrays.stream(CollectRecord.class.getDeclaredFields()).forEach(field -> {
//            try {
//                System.out.println(field.getName());
//                System.out.println(field.get(record));
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        });
    }
}
