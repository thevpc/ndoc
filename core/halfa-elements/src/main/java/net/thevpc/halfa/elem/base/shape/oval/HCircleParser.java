package net.thevpc.halfa.elem.base.shape.oval;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

public class HCircleParser extends HNodeParserBase {

    public HCircleParser() {
        super(false, HNodeType.CIRCLE);
    }

}
