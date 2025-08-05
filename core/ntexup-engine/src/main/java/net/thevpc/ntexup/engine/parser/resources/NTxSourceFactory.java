package net.thevpc.ntexup.engine.parser.resources;

import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.nuts.io.NPath;

import java.io.InputStream;

public class NTxSourceFactory {
    public static NTxSource of(NPath p) {
        if (p == null) {
            return null;
        }
        return new NTxSourceFromNPath(p);
    }

    public static NTxSource of(InputStream p) {
        if (p == null) {
            return null;
        }
        return new NTxSourceFromNInputStream(p);
    }
}
