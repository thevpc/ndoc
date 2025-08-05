package net.thevpc.ntexup.api.extension;

import net.thevpc.ntexup.api.eval.NDocFunctionArgs;
import net.thevpc.ntexup.api.eval.NDocFunctionContext;
import net.thevpc.nuts.elem.NElement;

public interface NTxFunction {
    String name();

    NElement invoke(NDocFunctionArgs args, NDocFunctionContext context);
}
