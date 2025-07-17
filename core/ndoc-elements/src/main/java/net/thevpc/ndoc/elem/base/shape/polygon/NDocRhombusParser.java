package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocRhombusParser extends NDocNodeParserBase {

    public NDocRhombusParser() {
        super(false, NDocNodeType.RHOMBUS,"diamond");
    }

}
