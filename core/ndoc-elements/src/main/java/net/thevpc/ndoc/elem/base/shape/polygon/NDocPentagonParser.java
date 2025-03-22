package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocPentagonParser extends NDocNodeParserBase {

    public NDocPentagonParser() {
        super(false, HNodeType.PENTAGON);
    }

}
