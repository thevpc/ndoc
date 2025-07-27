package net.thevpc.ndoc.engine.eval;

import net.thevpc.ndoc.api.eval.NDocCompilePageContext;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.document.NDocument;

public class NDocCompilePageContextImpl implements NDocCompilePageContext {
    private final NDocEngine engine;
    private final NDocument document;

    public NDocCompilePageContextImpl(NDocEngine engine, NDocument document) {
        this.engine = engine;
        this.document = document;
    }

    public NDocEngine engine() {
        return engine;
    }

    public NDocument document() {
        return document;
    }

    @Override
    public NDocLogger messages() {
        return engine.log();
    }
}
