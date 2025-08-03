package net.thevpc.ndoc.api.eval;


import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public interface NDocFunctionContext {
    NDocLogger messages();
    NDocEngine engine();
    NDocNode node();
    NElement eval(NElement expr);
    NOptional<NDocVar> findVar(String varName);
}
