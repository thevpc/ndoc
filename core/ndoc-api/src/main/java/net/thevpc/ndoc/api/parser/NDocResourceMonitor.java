package net.thevpc.ndoc.api.parser;

import net.thevpc.nuts.io.NPath;

public interface NDocResourceMonitor extends NDocResource {
    void clear();

    void remove(NDocResource r);

    void add(NDocResource r);

    void add(NPath r);
}
