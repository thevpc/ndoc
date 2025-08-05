package net.thevpc.ntexup.engine;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.nuts.io.NPath;

public class NDocEngineUtils {
    public static final String NDOC_EXT_NAME = NTxEngine.FILE_EXT;
    public static final String NDOC_EXT = NTxEngine.FILE_DOT_EXT;
    public static final String NDOC_EXT_STAR = "*."+NTxEngine.FILE_EXT;
    public static final String NDOC_EXT_STAR_STAR = "**/*."+NTxEngine.FILE_EXT;

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
