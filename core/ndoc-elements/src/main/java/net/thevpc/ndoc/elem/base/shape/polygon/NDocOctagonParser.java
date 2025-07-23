package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;

public class NDocOctagonParser extends NDocNodeParserBase {

    public NDocOctagonParser() {
        super(false, NDocNodeType.OCTAGON);
    }

}
