package net.thevpc.halfa.elem.base.shape.oval;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

public class HEllipseParser extends HNodeParserBase {

    public HEllipseParser() {
        super(false, HNodeType.ELLIPSE);
    }

}
