package net.thevpc.ndoc.engine.parser.nodes;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocPageParser extends NDocNodeParserBase {
    public NDocPageParser() {
        super(true, NDocNodeType.PAGE);
    }
}
