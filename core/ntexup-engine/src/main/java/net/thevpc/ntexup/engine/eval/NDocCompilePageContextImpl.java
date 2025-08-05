package net.thevpc.ntexup.engine.eval;

import net.thevpc.ntexup.api.eval.NDocCompilePageContext;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.NTxDocument;

public class NDocCompilePageContextImpl implements NDocCompilePageContext {
    private final NTxEngine engine;
    private final NTxDocument document;

    public NDocCompilePageContextImpl(NTxEngine engine, NTxDocument document) {
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
    public NDocLogger messages() {
        return engine.log();
    }
}
