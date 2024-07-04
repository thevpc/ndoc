package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

/**
 * @author vpc
 */
public class HPageParser extends HNodeParserBase {
    public HPageParser() {
        super(true, HNodeType.PAGE);
    }
}
