package net.thevpc.ndoc.engine.parser.resources;

import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public class NDocResourceNew implements NDocResource {
    public NDocResourceNew() {

    }

    @Override
    public NOptional<NPath> path() {
        return NOptional.ofEmpty();
    }

    @Override
    public void save() {

    }

    @Override
    public boolean changed() {
        return false;
    }

    @Override
    public String shortName() {
        return "new";
    }
}
