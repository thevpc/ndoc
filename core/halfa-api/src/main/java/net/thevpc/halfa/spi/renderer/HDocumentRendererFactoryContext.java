package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.nuts.NSession;

public interface HDocumentRendererFactoryContext {

    String rendererType();

    NSession session();

    HalfaEngine engine();

}
