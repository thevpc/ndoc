package net.thevpc.ntexup.engine.eval;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.eval.NDocVar;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.eval.NDocFunctionContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public class DefaultNDocFunctionContext implements NDocFunctionContext {
    private NTxEngine engine;
    private NTxNode node;
    private NDocVarProvider varProvider;

    public DefaultNDocFunctionContext(NTxEngine engine, NTxNode node, NDocVarProvider varProvider) {
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
    public NTxNode node() {
        return node;
    }

    public NTxEngine engine() {
        return engine;
    }

    public NDocLogger messages() {
        return engine.log();
    }
}
