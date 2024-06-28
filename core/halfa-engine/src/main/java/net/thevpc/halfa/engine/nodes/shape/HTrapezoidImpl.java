package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.AbstractHNodeTypeFactory;

public class HTrapezoidImpl extends AbstractHNodeTypeFactory {

    public HTrapezoidImpl() {
        super(false, HNodeType.TRAPEZOID);
    }

}
