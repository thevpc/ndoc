package net.thevpc.ndoc.api.source;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public interface NDocResource {
    NOptional<NPath> path();

    void save();

    boolean changed();


    String shortName();
}
