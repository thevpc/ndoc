package net.thevpc.ndoc.engine.eval.fct;

import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NBlankable;

public class NDocEitherFunction implements NDocFunction {
    @Override
    public String name() {
        return "either";
    }

    @Override
    public NElement invoke(NDocFunctionArg[] args, NDocFunctionContext context) {
        for (NDocFunctionArg arg : args) {
            NElement u = arg.get();
            if (!NBlankable.isBlank(u)) {
                return u;
            }
        }
        return null;
    }
}
