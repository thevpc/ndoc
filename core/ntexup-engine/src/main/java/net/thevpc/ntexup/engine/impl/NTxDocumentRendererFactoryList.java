package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.renderer.NTxDocumentRendererFactory;

public class NTxDocumentRendererFactoryList extends NtxServiceListImpl2<NTxDocumentRendererFactory> {
    public NTxDocumentRendererFactoryList(DefaultNTxEngine engine) {
        super("document renderer factory", NTxDocumentRendererFactory.class, engine);
    }

    @Override
    protected String idOf(NTxDocumentRendererFactory nTxDocumentRendererFactory) {
        return nTxDocumentRendererFactory.getClass().getSimpleName();
    }
}
