package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;

/**
 * @author vpc
 */
public class HPageImpl extends AbstractHNodeTypeFactory {
    public HPageImpl() {
        super(true, HNodeType.PAGE);
    }
}
