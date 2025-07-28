package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererFactoryContext;
import net.thevpc.nuts.util.NStringUtils;

public class NDocDocumentRendererFactoryContextImpl implements NDocDocumentRendererFactoryContext {
    private final DefaultNDocEngine hEngine;
    private final String type;

    public NDocDocumentRendererFactoryContextImpl(DefaultNDocEngine hEngine, String type) {
        this.hEngine = hEngine;
        this.type = NStringUtils.trim(type);
    }

    @Override
    public String rendererType() {
        return type;
    }

    @Override
    public NDocEngine engine() {
        return hEngine;
    }

}
