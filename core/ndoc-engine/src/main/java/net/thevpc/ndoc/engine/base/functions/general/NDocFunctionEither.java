package net.thevpc.ndoc.engine.base.functions.general;

import net.thevpc.ndoc.api.extension.NDocFunction;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.ndoc.api.eval.NDocFunctionArgs;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NBlankable;

public class NDocFunctionEither implements NDocFunction {
    @Override
    public String name() {
        return "either";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        for (NDocFunctionArg arg : args.args()) {
            NElement u = arg.eval();
            if (!NBlankable.isBlank(u)) {
                return u;
            }
        }
        return null;
    }
}
