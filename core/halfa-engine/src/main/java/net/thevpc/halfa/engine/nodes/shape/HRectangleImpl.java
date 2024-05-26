package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;

public class HRectangleImpl extends AbstractHNodeTypeFactory {

    public HRectangleImpl() {
        super(false, HNodeType.RECTANGLE);
    }

}
