package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeParser {
    void init(NDocEngine engine);

    String id();

    boolean isContainer();

    String[] aliases();

    NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context);

    NElement toElem(HNode item);

    HNode newNode();

    default boolean validateNode(HNode node) {
        return false;
    }
}
