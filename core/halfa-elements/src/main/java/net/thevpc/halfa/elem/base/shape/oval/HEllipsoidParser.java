package net.thevpc.halfa.elem.base.shape.oval;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

public class HEllipsoidParser extends HNodeParserBase {

    public HEllipsoidParser() {
        super(false, HNodeType.ELLIPSOID);
    }

}
