package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public interface HITemNamedObjectParser {
    String[] ids();

    boolean accept(String id, TsonElement tsonElement, HNodeFactoryParseContext context);

    NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context);
}
