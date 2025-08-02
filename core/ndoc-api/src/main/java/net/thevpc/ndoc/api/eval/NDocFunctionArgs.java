package net.thevpc.ndoc.api.eval;

import net.thevpc.nuts.elem.NElement;

public interface NDocFunctionArgs {
    int size();

    NDocFunctionArg[] args();

    NDocFunctionArg arg(int index);
    NElement src(int index);

    NElement[] eval();

    NElement eval(int index);
}
