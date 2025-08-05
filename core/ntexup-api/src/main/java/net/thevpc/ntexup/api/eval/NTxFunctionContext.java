package net.thevpc.ntexup.api.eval;


import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.log.NTxLogger;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public interface NTxFunctionContext {
    NTxLogger messages();
    NTxEngine engine();
    NTxNode node();
    NElement eval(NElement expr);
    NOptional<NTxVar> findVar(String varName);
}
