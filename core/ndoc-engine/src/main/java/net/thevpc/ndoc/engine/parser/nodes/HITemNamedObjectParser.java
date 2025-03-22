package net.thevpc.ndoc.engine.parser.nodes;

import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public interface HITemNamedObjectParser {
    String[] ids();

    boolean accept(String id, TsonElement tsonElement, NDocNodeFactoryParseContext context);

    NOptional<HItem> parseItem(String id, TsonElement tsonElement, NDocNodeFactoryParseContext context);
}
