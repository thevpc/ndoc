package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HAlignEnumParser {
    public static NOptional<HAlign> parseHAlign(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        NOptional<String> k = new TsonElementParseHelper(e).asStringOrName();
        if (k.isPresent()) {
            switch (HParseHelper.uid(k.get())) {
                case "bottom":
                    return NOptional.of(HAlign.BOTTOM);
                case "bottom-left":
                    return NOptional.of(HAlign.BOTTOM_LEFT);
                case "bottom-right":
                    return NOptional.of(HAlign.BOTTOM_RIGHT);
                case "top-left":
                    return NOptional.of(HAlign.TOP_LEFT);
                case "top-right":
                    return NOptional.of(HAlign.TOP_RIGHT);
                case "left":
                    return NOptional.of(HAlign.LEFT);
                case "right":
                    return NOptional.of(HAlign.RIGHT);
                case "center":
                    return NOptional.of(HAlign.CENTER);
            }
        }
        return NOptional.ofNamedEmpty("HAlign " + e);
    }
}
