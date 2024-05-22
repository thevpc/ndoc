package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.spi.renderer.HDocumentRendererFactoryContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NStringUtils;

class HDocumentRendererFactoryContextImpl implements HDocumentRendererFactoryContext {
    private final HEngineImpl hEngine;
    private final String type;

    public HDocumentRendererFactoryContextImpl(HEngineImpl hEngine, String type) {
        this.hEngine = hEngine;
        this.type = type;
    }

    @Override
    public String rendererType() {
        return NStringUtils.trim(type);
    }

    @Override
    public HEngine engine() {
        return hEngine;
    }

    @Override
    public NSession session() {
        return hEngine.getSession();
    }

}
