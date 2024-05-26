package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;

public class HEllipseImpl extends AbstractHNodeTypeFactory {

    public HEllipseImpl() {
        super(false, HNodeType.ELLIPSE);
    }

}
