package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.engine.nodes.container.AbstractHContainer;

/**
 * @author vpc
 */
public class HPageImpl extends AbstractHContainer implements HPage {
    @Override
    public HNodeType type() {
        return HNodeType.PAGE;
    }

    @Override
    public HContainer add(HNode a) {
        if (a != null) {
            switch (a.type()) {
                case PAGE:
                case PAGE_GROUP: {
                    throw new IllegalArgumentException("pages cannot be nested. please use page groups");
                }
            }
        }
        return super.add(a);
    }
}
