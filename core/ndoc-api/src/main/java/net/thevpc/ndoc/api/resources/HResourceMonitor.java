package net.thevpc.ndoc.api.resources;

import net.thevpc.nuts.io.NPath;

public interface HResourceMonitor extends HResource {
    void clear();

    void remove(HResource r);

    void add(HResource r);

    void add(NPath r);
}
