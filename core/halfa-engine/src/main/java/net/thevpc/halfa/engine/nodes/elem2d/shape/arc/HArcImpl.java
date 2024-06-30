package net.thevpc.halfa.engine.nodes.elem2d.shape.arc;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;

public class HArcImpl extends HNodeParserBase {
    public HArcImpl() {
        super(false, HNodeType.ARC);
    }
}
