package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

interface HStyleValueParser {
    String[] ids();

    NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context);
}
