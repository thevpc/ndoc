package net.thevpc.ndoc.engine.fct;

import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.nuts.util.NBlankable;

public class NDocEitherFunction implements NDocFunction {
    @Override
    public String name() {
        return "either";
    }

    @Override
    public Object invoke(NDocFunctionArg[] args, NDocFunctionContext context) {
        for (NDocFunctionArg arg : args) {
            Object u = arg.get();
            if (!NBlankable.isBlank(u)) {
                return u;
            }
        }
        return null;
    }
}
