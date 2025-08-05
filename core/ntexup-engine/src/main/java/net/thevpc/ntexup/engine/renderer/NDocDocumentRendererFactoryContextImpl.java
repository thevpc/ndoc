package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.engine.DefaultNTxEngine;
import net.thevpc.ntexup.api.renderer.NDocDocumentRendererFactoryContext;
import net.thevpc.nuts.util.NStringUtils;

public class NDocDocumentRendererFactoryContextImpl implements NDocDocumentRendererFactoryContext {
    private final DefaultNTxEngine hEngine;
    private final String type;

    public NDocDocumentRendererFactoryContextImpl(DefaultNTxEngine hEngine, String type) {
        this.hEngine = hEngine;
        this.type = NStringUtils.trim(type);
    }

    @Override
    public String rendererType() {
        return type;
    }

    @Override
    public NTxEngine engine() {
        return hEngine;
    }

}
