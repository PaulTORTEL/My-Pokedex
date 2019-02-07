package tortel.fr.mypokedex.utils;

import java.util.List;

public class StringUtils {

    public static String capitalize(String name) {
        String result = name.toLowerCase();
        return result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
    }
    public static String clean(String str) {
        return capitalize(str.replace("-", " "));
    }

    public static String toStringList(List<String> list) {

        if (list == null || list.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String s : list) {
            sb.append(s);
            sb.append(", ");
        }

        return sb.substring(0, sb.length() - 2);
    }
}
