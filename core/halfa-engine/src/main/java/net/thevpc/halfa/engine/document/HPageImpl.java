package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;

/**
 * @author vpc
 */
public class HPageImpl extends HNodeParserBase {
    public HPageImpl() {
        super(true, HNodeType.PAGE);
    }
}
