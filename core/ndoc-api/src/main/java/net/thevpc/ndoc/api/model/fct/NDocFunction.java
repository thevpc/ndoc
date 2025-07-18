package net.thevpc.ndoc.api.model.fct;

public interface NDocFunction {
    String name();

    Object invoke(NDocFunctionArg[] args, NDocFunctionContext context);
}
