package net.thevpc.ndoc.api.eval;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.util.NOptional;

public interface NDocVarProvider {
    NOptional<NDocVar> findVar(String varName, NDocNode node);
}
