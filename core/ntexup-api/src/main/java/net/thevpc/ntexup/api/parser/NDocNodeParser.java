package net.thevpc.ntexup.api.parser;

import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeParser {
    void init(NDocEngine engine);

    String id();

    boolean isContainer();

    String[] aliases();

    NCallableSupport<NTxItem> parseNode(NDocNodeFactoryParseContext context);

    NElement toElem(NTxNode item);

    NTxNode newNode();

    default boolean validateNode(NTxNode node) {
        return false;
    }
}
