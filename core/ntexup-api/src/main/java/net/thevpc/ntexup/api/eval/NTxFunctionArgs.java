package net.thevpc.ntexup.api.eval;

import net.thevpc.nuts.elem.NElement;

public interface NTxFunctionArgs {
    int size();

    NTxFunctionArg[] args();

    NTxFunctionArg arg(int index);
    NElement src(int index);

    NElement[] eval();

    NElement eval(int index);
}
