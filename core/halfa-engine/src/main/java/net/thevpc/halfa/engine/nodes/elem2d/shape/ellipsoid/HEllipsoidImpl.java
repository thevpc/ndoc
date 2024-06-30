package net.thevpc.halfa.engine.nodes.elem2d.shape.ellipsoid;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;

public class HEllipsoidImpl extends HNodeParserBase {

    public HEllipsoidImpl() {
        super(false, HNodeType.ELLIPSOID);
    }

}
