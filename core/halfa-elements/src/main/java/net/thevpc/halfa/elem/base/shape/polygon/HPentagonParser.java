package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

public class HPentagonParser extends HNodeParserBase {

    public HPentagonParser() {
        super(false, HNodeType.PENTAGON);
    }

}
