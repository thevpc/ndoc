package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;

public class HEllipsoidImpl extends AbstractHNodeTypeFactory {

    public HEllipsoidImpl() {
        super(false, HNodeType.SPHERE);
    }

}
