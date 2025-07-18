package net.thevpc.ndoc.api.model.fct;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public interface NDocFunctionContext {
    Object invokeFunction(String name, NDocFunctionArg[] args);

    NDocFunctionArg createNodeArg(NDocNode node);

    NDocFunctionArg createUserObjectArg(Object node);
    NDocFunctionArg createRawArg(NElement node);
}
