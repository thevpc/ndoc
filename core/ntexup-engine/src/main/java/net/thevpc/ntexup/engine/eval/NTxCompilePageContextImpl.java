package net.thevpc.ntexup.engine.eval;

import net.thevpc.ntexup.api.eval.NTxCompilePageContext;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.log.NTxLogger;
import net.thevpc.ntexup.api.document.NTxDocument;

public class NTxCompilePageContextImpl implements NTxCompilePageContext {
    private final NTxEngine engine;
    private final NTxDocument document;

    public NTxCompilePageContextImpl(NTxEngine engine, NTxDocument document) {
        this.engine = engine;
        this.document = document;
    }

    public NTxEngine engine() {
        return engine;
    }

    public NTxDocument document() {
        return document;
    }

    @Override
    public NTxLogger messages() {
        return engine.log();
    }
}
