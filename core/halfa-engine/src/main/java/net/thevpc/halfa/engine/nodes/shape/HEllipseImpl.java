package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HEllipse;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

public class HEllipseImpl extends AbstractHNode implements HEllipse {

    public HEllipseImpl() {
    }

    @Override
    public HNodeType type() {
        return HNodeType.ELLIPSE;
    }
}
