package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

public class NDocSphereParser extends NDocNodeParserBase {

    public NDocSphereParser() {
        super(false, HNodeType.SPHERE);
    }

}
