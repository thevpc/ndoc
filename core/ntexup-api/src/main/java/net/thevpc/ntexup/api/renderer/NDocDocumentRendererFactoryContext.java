package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.engine.NTxEngine;

public interface NDocDocumentRendererFactoryContext {

    String rendererType();

    NTxEngine engine();

}
