package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.engine.nodes.container.AbstractHContainer;

/**
 *
 * @author vpc
 */
public class HPageImpl extends AbstractHContainer implements HPage {
    @Override
    public HNodeType type() {
        return HNodeType.PAGE;
    }

}
