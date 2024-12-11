package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.nuts.NSession;

public interface HDocumentRendererFactoryContext {

    String rendererType();

    HEngine engine();

}
