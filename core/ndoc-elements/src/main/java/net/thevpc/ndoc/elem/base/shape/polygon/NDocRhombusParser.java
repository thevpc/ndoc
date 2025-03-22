package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocRhombusParser extends NDocNodeParserBase {

    public NDocRhombusParser() {
        super(false, HNodeType.RHOMBUS,"diamond");
    }

}
