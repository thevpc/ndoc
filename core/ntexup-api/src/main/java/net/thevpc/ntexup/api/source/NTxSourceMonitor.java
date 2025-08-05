package net.thevpc.ntexup.api.source;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public interface NTxSourceMonitor extends NTxSource {
    void clear();

    void remove(NTxSource r);

    void add(NTxSource r);

    void add(NPath r);

    NOptional<NTxSource> find(String path);
}
