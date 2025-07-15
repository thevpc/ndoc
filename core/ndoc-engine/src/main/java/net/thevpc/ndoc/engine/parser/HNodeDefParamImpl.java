package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.model.node.HNodeDefParam;
import net.thevpc.nuts.elem.NElement;

public class HNodeDefParamImpl implements HNodeDefParam {
    private final String name;
    private final NElement value;

    public HNodeDefParamImpl(String name, NElement value) {
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
