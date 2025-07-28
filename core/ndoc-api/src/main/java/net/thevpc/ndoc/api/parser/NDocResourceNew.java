package net.thevpc.ndoc.api.parser;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
