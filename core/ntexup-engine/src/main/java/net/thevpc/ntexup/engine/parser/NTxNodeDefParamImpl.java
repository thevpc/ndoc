package net.thevpc.ntexup.engine.parser;

import net.thevpc.ntexup.api.document.node.NTxNodeDefParam;
import net.thevpc.nuts.elem.NElement;

public class NTxNodeDefParamImpl implements NTxNodeDefParam {
    private final String name;
    private final NElement value;

    public NTxNodeDefParamImpl(String name, NElement value) {
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
