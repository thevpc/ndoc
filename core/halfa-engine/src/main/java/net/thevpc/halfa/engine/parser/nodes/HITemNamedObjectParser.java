package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public interface HITemNamedObjectParser {
    String[] ids();

    NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context);
}
