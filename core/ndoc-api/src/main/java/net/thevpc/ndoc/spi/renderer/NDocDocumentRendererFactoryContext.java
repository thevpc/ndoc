package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.NDocEngine;

public interface NDocDocumentRendererFactoryContext {

    String rendererType();

    NDocEngine engine();

}
