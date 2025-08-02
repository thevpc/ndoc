package net.thevpc.ndoc.engine;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.nuts.elem.NElement;

public class NDocFunctionArgImpl implements NDocFunctionArg {
    private final NDocNode node;
    private final NElement expression;
    private final NDocEngine engine;

    public NDocFunctionArgImpl(NDocNode node, NElement expression, NDocEngine engine) {
        this.node = node;
        this.expression = expression;
        this.engine = engine;
    }

    public NElement src() {
        return expression;
    }

    @Override
    public NElement eval() {
        NElement u = engine.evalExpression(expression, node);
        NElement u2 = engine.evalExpression(u, node);
        return u2;
    }

    @Override
    public String toString() {
        return String.valueOf(expression);
    }
}
