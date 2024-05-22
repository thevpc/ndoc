package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HRectangle;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

public class HRectangleImpl extends AbstractHNode implements HRectangle {

    public HRectangleImpl() {
    }


    @Override
    public HNodeType type() {
        return HNodeType.RECTANGLE;
    }
}
