package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocHeptagonParser extends NDocNodeParserBase {

    public NDocHeptagonParser() {
        super(false, HNodeType.HEPTAGON);
    }

}
