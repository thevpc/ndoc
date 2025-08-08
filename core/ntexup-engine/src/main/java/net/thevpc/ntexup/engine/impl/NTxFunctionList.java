package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.extension.NTxFunction;

public class NTxFunctionList extends NtxServiceListImpl2<NTxFunction> {
    public NTxFunctionList(DefaultNTxEngine engine) {
        super("function", NTxFunction.class, engine);
    }

    @Override
    protected String idOf(NTxFunction h) {
        return h.name();
    }
}
