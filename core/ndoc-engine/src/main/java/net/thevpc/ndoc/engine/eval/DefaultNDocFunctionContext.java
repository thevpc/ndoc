package net.thevpc.ndoc.engine.eval;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.eval.NDocVar;
import net.thevpc.ndoc.api.eval.NDocVarProvider;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public class DefaultNDocFunctionContext implements NDocFunctionContext {
    private NDocEngine engine;
    private NDocNode node;
    private NDocVarProvider varProvider;

    public DefaultNDocFunctionContext(NDocEngine engine, NDocNode node, NDocVarProvider varProvider) {
        this.engine = engine;
        this.node = node;
        this.varProvider = varProvider;
    }

    public NElement eval(NElement expr) {
        return engine.evalExpression(expr, node, varProvider);
    }

    @Override
    public NOptional<NDocVar> findVar(String varName) {
        return engine.findVar(varName, node, varProvider);
    }

    @Override
    public NDocNode node() {
        return node;
    }

    public NDocEngine engine() {
        return engine;
    }

    public NDocLogger messages() {
        return engine.log();
    }
}
