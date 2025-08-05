package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.engine.NTxEngine;

public interface NTxDocumentRendererFactoryContext {

    String rendererType();

    NTxEngine engine();

}
