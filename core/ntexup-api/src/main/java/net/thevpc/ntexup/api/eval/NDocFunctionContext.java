package net.thevpc.ntexup.api.eval;


import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public interface NDocFunctionContext {
    NDocLogger messages();
    NDocEngine engine();
    NTxNode node();
    NElement eval(NElement expr);
    NOptional<NDocVar> findVar(String varName);
}
