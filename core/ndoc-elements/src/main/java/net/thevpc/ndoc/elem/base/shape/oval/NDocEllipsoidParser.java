package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocEllipsoidParser extends NDocNodeParserBase {

    public NDocEllipsoidParser() {
        super(false, NDocNodeType.ELLIPSOID);
    }

}
