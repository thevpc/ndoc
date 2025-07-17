package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocHexagonParser extends NDocNodeParserBase {

    public NDocHexagonParser() {
        super(false, NDocNodeType.HEXAGON);
    }

}
