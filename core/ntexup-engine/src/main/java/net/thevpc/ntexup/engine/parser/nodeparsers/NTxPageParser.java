package net.thevpc.ntexup.engine.parser.nodeparsers;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.engine.parser.NTxNodeParserBase;

/**
 * @author vpc
 */
public class NTxPageParser extends NTxNodeParserBase {
    public NTxPageParser() {
        super(true, NTxNodeType.PAGE);
    }
}
