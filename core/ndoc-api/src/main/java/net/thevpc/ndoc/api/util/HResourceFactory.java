package net.thevpc.ndoc.api.util;

import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.nuts.io.NPath;

import java.io.InputStream;

public class HResourceFactory {
    public static HResource of(NPath p) {
        if (p == null) {
            return null;
        }
        return new NPathHResource(p);
    }

    public static HResource of(InputStream p) {
        if (p == null) {
            return null;
        }
        return new NInputStreamHResource(p);
    }
}
