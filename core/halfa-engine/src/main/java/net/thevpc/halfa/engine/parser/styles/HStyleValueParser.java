package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public interface HStyleValueParser {
    String[] ids();

    NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context);
}
