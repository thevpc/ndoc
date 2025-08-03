package net.thevpc.ndoc.engine.eval;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.ndoc.api.eval.NDocVarProvider;
import net.thevpc.nuts.elem.NElement;

public class NDocFunctionArgImpl implements NDocFunctionArg {
    private final NDocNode node;
    private final NElement expression;
    private final NDocEngine engine;
    private final NDocVarProvider varProvider;

    public NDocFunctionArgImpl(NElement expression, NDocNode node, NDocEngine engine, NDocVarProvider varProvider) {
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
