package net.thevpc.halfa.engine;

import net.thevpc.nuts.io.NPath;

public class HEngineUtils {
    public static final String HALFA_EXT_NAME = "hd";
    public static final String HALFA_EXT = ".hd";
    public static final String HALFA_EXT_STAR = "*.hd";
    public static final String HALFA_EXT_STAR_STAR = "**/*.hd";

    public static boolean isHalfaFile(NPath path) {
        return path != null && isHalfaFile(path.getName());
    }

    public static boolean isHalfaFile(String name) {
        if (name != null) {
            return name.toLowerCase().endsWith(HALFA_EXT);
        }
        return false;
    }

    public static int comparePaths(NPath o1, NPath o2) {
        String a = o1.toString();
        String b = o2.toString();
        if (isHalfaFile(a) && isHalfaFile(b)) {
            a = a.substring(0, a.length() - HALFA_EXT.length());
            b = b.substring(0, b.length() - HALFA_EXT.length());
        }
        return a.compareTo(b);
    }
}
