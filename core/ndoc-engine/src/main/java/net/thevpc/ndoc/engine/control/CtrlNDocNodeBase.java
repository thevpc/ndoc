package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.api.model.node.NDocNodeCtrl;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.spi.base.model.DefaultNDocNode;

public abstract class CtrlNDocNodeBase extends DefaultNDocNode implements NDocNodeCtrl {
    public CtrlNDocNodeBase(String nodeType, NDocResource source) {
        super(nodeType,source);
    }
}
