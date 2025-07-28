package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;

public class NDocEllipseParser extends NDocNodeParserBase {

    public NDocEllipseParser() {
        super(false, NDocNodeType.ELLIPSE);
    }

}
