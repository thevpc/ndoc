package net.thevpc.ndoc.api.util;

import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.nuts.io.NPath;

import java.io.InputStream;

public class HResourceFactory {
    public static NDocResource of(NPath p) {
        if (p == null) {
            return null;
        }
        return new NPathNDocResource(p);
    }

    public static NDocResource of(InputStream p) {
        if (p == null) {
            return null;
        }
        return new NInputStreamNDocResource(p);
    }
}
