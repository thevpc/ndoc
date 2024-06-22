package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.tson.TsonElement;

public interface HNodeTypeFactory {
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
