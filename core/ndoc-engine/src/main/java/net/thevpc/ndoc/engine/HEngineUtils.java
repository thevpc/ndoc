package net.thevpc.ndoc.engine;

import net.thevpc.nuts.io.NPath;

public class HEngineUtils {
    public static final String NDOC_EXT_NAME = "ndoc";
    public static final String NDOC_EXT = ".ndoc";
    public static final String NDOC_EXT_STAR = "*.ndoc";
    public static final String NDOC_EXT_STAR_STAR = "**/*.ndoc";

    public static boolean isNDocFile(NPath path) {
        return path != null && isNDocFile(path.getName());
    }

    public static boolean isNDocFile(String name) {
        if (name != null) {
            return name.toLowerCase().endsWith(NDOC_EXT);
        }
        return false;
    }

    public static int comparePaths(NPath o1, NPath o2) {
        String a = o1.toString();
        String b = o2.toString();
        if (isNDocFile(a) && isNDocFile(b)) {
            a = a.substring(0, a.length() - NDOC_EXT.length());
            b = b.substring(0, b.length() - NDOC_EXT.length());
        }
        return a.compareTo(b);
    }
}
