package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

public class HRhombusParser extends HNodeParserBase {

    public HRhombusParser() {
        super(false, HNodeType.RHOMBUS,"diamond");
    }

}
