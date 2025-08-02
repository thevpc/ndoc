package net.thevpc.ndoc.api.source;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public interface NDocResourceMonitor extends NDocResource {
    void clear();

    void remove(NDocResource r);

    void add(NDocResource r);

    void add(NPath r);

    NOptional<NDocResource> find(String path);
}
