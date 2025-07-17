package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocEllipseParser extends NDocNodeParserBase {

    public NDocEllipseParser() {
        super(false, NDocNodeType.ELLIPSE);
    }

}
