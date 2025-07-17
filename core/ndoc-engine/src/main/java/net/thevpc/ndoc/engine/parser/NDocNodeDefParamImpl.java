package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.model.node.NDocNodeDefParam;
import net.thevpc.nuts.elem.NElement;

public class NDocNodeDefParamImpl implements NDocNodeDefParam {
    private final String name;
    private final NElement value;

    public NDocNodeDefParamImpl(String name, NElement value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public NElement value() {
        return value;
    }
}
