package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.engine.NDocEngine;

public interface NDocDocumentRendererFactoryContext {

    String rendererType();

    NDocEngine engine();

}
