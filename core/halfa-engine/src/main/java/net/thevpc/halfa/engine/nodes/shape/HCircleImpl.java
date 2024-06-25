package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;

public class HCircleImpl extends AbstractHNodeTypeFactory {

    public HCircleImpl() {
        super(false, HNodeType.CIRCLE);
    }

}
