package net.thevpc.halfa.engine.nodes.elem2d.shape.triangle;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;

public class HTriangleImpl extends HNodeParserBase {

    public HTriangleImpl() {
        super(false, HNodeType.TRIANGLE);
    }

}
