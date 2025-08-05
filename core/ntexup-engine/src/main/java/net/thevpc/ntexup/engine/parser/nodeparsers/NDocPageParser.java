package net.thevpc.ntexup.engine.parser.nodeparsers;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.engine.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocPageParser extends NDocNodeParserBase {
    public NDocPageParser() {
        super(true, NTxNodeType.PAGE);
    }
}
