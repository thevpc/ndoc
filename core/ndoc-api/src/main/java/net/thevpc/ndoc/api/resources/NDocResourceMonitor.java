package net.thevpc.ndoc.api.resources;

import net.thevpc.nuts.io.NPath;

public interface NDocResourceMonitor extends NDocResource {
    void clear();

    void remove(NDocResource r);

    void add(NDocResource r);

    void add(NPath r);
}
