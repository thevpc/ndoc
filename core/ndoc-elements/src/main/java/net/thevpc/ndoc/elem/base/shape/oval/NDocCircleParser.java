package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;

public class NDocCircleParser extends NDocNodeParserBase {

    public NDocCircleParser() {
        super(false, NDocNodeType.CIRCLE);
    }

}
