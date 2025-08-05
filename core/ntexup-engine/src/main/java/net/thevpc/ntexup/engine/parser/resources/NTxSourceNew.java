package net.thevpc.ntexup.engine.parser.resources;

import net.thevpc.ntexup.api.source.NTxSource;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public class NTxSourceNew implements NTxSource {
    public NTxSourceNew() {

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
