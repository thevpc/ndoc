package net.thevpc.ndoc.engine.eval.fct;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;

public class DefaultNDocFunctionContext implements NDocFunctionContext {
    private NDocEngine engine;
    public DefaultNDocFunctionContext(NDocEngine engine) {
        this.engine = engine;
    }

    public NDocEngine engine() {
        return engine;
    }

    public NDocLogger messages() {
        return engine.messages();
    }
}
