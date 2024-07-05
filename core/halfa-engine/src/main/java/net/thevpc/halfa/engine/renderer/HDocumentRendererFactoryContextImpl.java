package net.thevpc.halfa.engine.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRendererFactoryContext;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererConfig;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NStringUtils;

public class HDocumentRendererFactoryContextImpl implements HDocumentRendererFactoryContext {
    private final HEngineImpl hEngine;
    private final String type;

    public HDocumentRendererFactoryContextImpl(HEngineImpl hEngine, String type) {
        this.hEngine = hEngine;
        this.type = NStringUtils.trim(type);
    }

    @Override
    public String rendererType() {
        return type;
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
