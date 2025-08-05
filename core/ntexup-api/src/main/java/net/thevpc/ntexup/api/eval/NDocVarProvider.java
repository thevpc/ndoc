package net.thevpc.ntexup.api.eval;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.util.NOptional;

public interface NDocVarProvider {
    NOptional<NDocVar> findVar(String varName, NTxNode node);
}
