package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;

public class NtxTextFlavorList extends NtxServiceListImpl2<NTxTextRendererFlavor> {
    public NtxTextFlavorList(DefaultNTxEngine engine) {
        super("text flavor", NTxTextRendererFlavor.class, engine);
    }

    @Override
    protected String idOf(NTxTextRendererFlavor nTxTextRendererFlavor) {
        return nTxTextRendererFlavor.type();
    }
}
