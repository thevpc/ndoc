package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.AbstractHNodeTypeFactory;

public class HTriangleImpl extends AbstractHNodeTypeFactory {

    public HTriangleImpl() {
        super(false, HNodeType.TRIANGLE);
    }

}
