package net.thevpc.ntexup.engine.eval;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.eval.NTxFunctionArg;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.nuts.elem.NElement;

public class NTxFunctionArgImpl implements NTxFunctionArg {
    private final NTxNode node;
    private final NElement expression;
    private final NTxEngine engine;
    private final NDocVarProvider varProvider;

    public NTxFunctionArgImpl(NElement expression, NTxNode node, NTxEngine engine, NDocVarProvider varProvider) {
        this.node = node;
        this.expression = expression;
        this.engine = engine;
        this.varProvider = varProvider;
    }

    public NElement src() {
        return expression;
    }

    @Override
    public NElement eval() {
        NElement u = engine.evalExpression(expression, node, varProvider);
        NElement u2 = engine.evalExpression(u, node, varProvider);
        return u2;
    }

    @Override
    public String toString() {
        return String.valueOf(expression);
    }
}
