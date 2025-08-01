package net.thevpc.ndoc.api.parser;

import net.thevpc.nuts.io.NPath;

import java.io.InputStream;

public class NDocResourceFactory {
    public static NDocResource of(NPath p) {
        if (p == null) {
            return null;
        }
        return new NDocResourceFromNPath(p);
    }

    public static NDocResource of(InputStream p) {
        if (p == null) {
            return null;
        }
        return new NDocResourceFromNInputStream(p);
    }
}
