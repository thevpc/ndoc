package net.thevpc.ndoc.api.parser;

import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
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
