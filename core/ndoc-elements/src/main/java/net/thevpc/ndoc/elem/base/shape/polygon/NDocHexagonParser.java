package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;

public class NDocHexagonParser extends NDocNodeParserBase {

    public NDocHexagonParser() {
        super(false, NDocNodeType.HEXAGON);
    }

}
