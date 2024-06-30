package net.thevpc.halfa.spi;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.tson.TsonElement;

public interface HNodeParser {
    void init(HEngine engine);

    String id();

    boolean isContainer();

    String[] aliases();

    NCallableSupport<HItem> parseNode(HNodeFactoryParseContext context);

    TsonElement toTson(HNode item);

    HNode newNode();

    default boolean validateNode(HNode node) {
        return false;
    }
}
