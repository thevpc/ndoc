package net.thevpc.ndoc.api.eval;

import net.thevpc.nuts.elem.NElement;

public interface NDocFunction {
    String name();

    NElement invoke(NDocFunctionArg[] args, NDocFunctionContext context);
}
