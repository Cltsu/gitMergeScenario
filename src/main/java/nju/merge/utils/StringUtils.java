package nju.merge.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    @SafeVarargs
    public static List<String> concat(List<String> prefix, List<String> suffix, List<String>... regions) {
        return new ArrayList<>() {{
            addAll(prefix);
            for (List<String> r : regions) {
                addAll(r);
            }
            addAll(suffix);
        }};
    }
}
