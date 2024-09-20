package net.thevpc.halfa.api.resources;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public interface HResource {
    NOptional<NPath> path();

    void save();

    boolean changed();

    Object state();

    String shortName();
}
