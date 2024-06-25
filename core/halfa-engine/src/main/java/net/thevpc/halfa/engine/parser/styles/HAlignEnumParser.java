package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HAlignEnumParser {
    public static NOptional<HAlign> parseHAlign(TsonElement e) {
        NOptional<String> k = ObjEx.of(e).asStringOrName();
        if (k.isPresent()) {
            return HAlign.parse(k.get());
        }
        return NOptional.ofNamedEmpty("HAlign " + e);
    }
}
