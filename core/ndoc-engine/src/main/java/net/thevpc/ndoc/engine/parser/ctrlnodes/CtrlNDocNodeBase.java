package net.thevpc.ndoc.engine.parser.ctrlnodes;

import net.thevpc.ndoc.api.document.node.NDocNodeCtrl;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.engine.document.DefaultNDocNode;

public abstract class CtrlNDocNodeBase extends DefaultNDocNode implements NDocNodeCtrl {
    public CtrlNDocNodeBase(String nodeType, NDocResource source) {
        super(nodeType,source);
    }
}
