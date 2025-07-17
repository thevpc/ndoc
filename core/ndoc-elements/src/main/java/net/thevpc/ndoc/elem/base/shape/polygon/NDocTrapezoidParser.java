package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocTrapezoidParser extends NDocNodeParserBase {

    public NDocTrapezoidParser() {
        super(false, NDocNodeType.TRAPEZOID);
    }

}
