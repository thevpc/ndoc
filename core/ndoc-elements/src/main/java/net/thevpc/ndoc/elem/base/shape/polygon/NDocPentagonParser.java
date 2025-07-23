package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;

public class NDocPentagonParser extends NDocNodeParserBase {

    public NDocPentagonParser() {
        super(false, NDocNodeType.PENTAGON);
    }

}
