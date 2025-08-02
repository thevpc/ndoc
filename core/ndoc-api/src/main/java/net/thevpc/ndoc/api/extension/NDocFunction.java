package net.thevpc.ndoc.api.extension;

import net.thevpc.ndoc.api.eval.NDocFunctionArgs;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;
import net.thevpc.nuts.elem.NElement;

public interface NDocFunction {
    String name();

    NElement invoke(NDocFunctionArgs args, NDocFunctionContext context);
}
