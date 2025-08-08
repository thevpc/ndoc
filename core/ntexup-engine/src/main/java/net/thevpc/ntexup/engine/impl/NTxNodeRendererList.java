package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.renderer.NTxNodeRenderer;

import java.util.Arrays;
import java.util.List;

public class NTxNodeRendererList extends NtxServiceListImpl2<NTxNodeRenderer> {
    public NTxNodeRendererList(DefaultNTxEngine engine) {
        super("node renderer", NTxNodeRenderer.class, engine);
    }

    @Override
    protected String idOf(NTxNodeRenderer nTxNodeRenderer) {
        return nTxNodeRenderer.types()[0];
    }

    @Override
    protected List<String> aliasesOf(NTxNodeRenderer nTxNodeRenderer) {
        return Arrays.asList(nTxNodeRenderer.types());
    }
}
