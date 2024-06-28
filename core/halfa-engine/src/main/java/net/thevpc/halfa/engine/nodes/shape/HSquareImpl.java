package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.AbstractHNodeTypeFactory;

public class HSquareImpl extends AbstractHNodeTypeFactory {

    public HSquareImpl() {
        super(false, HNodeType.SQUARE);
    }

}
