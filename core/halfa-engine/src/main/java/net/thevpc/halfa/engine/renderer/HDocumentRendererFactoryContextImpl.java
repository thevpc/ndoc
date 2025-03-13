package net.thevpc.halfa.engine.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.DefaultHEngine;
import net.thevpc.halfa.spi.renderer.HDocumentRendererFactoryContext;
import net.thevpc.nuts.util.NStringUtils;

public class HDocumentRendererFactoryContextImpl implements HDocumentRendererFactoryContext {
    private final DefaultHEngine hEngine;
    private final String type;

    public HDocumentRendererFactoryContextImpl(DefaultHEngine hEngine, String type) {
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

}
