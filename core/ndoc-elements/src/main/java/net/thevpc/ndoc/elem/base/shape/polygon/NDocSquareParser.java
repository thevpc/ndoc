package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocSquareParser extends NDocNodeParserBase {

    public NDocSquareParser() {
        super(false, HNodeType.SQUARE);
    }

}
