package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.api.model.node.HNodeCtrl;
import net.thevpc.ndoc.spi.base.model.DefaultHNode;

public abstract class CtrlHNodeBase extends DefaultHNode implements HNodeCtrl {
    public CtrlHNodeBase(String nodeType) {
        super(nodeType);
    }
}
