package net.thevpc.ndoc.engine.parser.nodeparsers;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocPageParser extends NDocNodeParserBase {
    public NDocPageParser() {
        super(true, NDocNodeType.PAGE);
    }
}
