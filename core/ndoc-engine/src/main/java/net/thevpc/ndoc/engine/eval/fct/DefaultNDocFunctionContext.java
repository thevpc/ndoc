package net.thevpc.ndoc.engine.eval.fct;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.ndoc.api.model.node.NDocItem;

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
