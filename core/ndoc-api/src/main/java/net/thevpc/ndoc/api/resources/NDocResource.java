package net.thevpc.ndoc.api.resources;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public interface NDocResource {
    NOptional<NPath> path();

    void save();

    boolean changed();

    Object state();

    String shortName();
}
