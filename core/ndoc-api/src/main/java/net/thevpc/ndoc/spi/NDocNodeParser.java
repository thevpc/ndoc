package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.NDocItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeParser {
    void init(NDocEngine engine);

    String id();

    boolean isContainer();

    String[] aliases();

    NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context);

    NElement toElem(NDocNode item);

    NDocNode newNode();

    default boolean validateNode(NDocNode node) {
        return false;
    }
}
