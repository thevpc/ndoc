package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocNovagonParser extends NDocNodeParserBase {

    public NDocNovagonParser() {
        super(false, NDocNodeType.NONAGON);
    }

}
