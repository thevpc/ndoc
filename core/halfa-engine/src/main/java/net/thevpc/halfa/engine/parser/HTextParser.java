package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonObject;
import net.thevpc.tson.TsonPair;

public class HTextParser {
    public static HText parseText(TsonObject ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        TsonElement c = context.element();
        HEngine engine = context.engine();
        NSession session = context.session();
        HText t = engine.documentFactory().text(0, 0, "");
        for (TsonElement e : ff.getAll()) {
            if (!HParseHelper.fillElementHDocumentItem(t, e)) {
                switch (e.type()) {
                    case PAIR: {
                        TsonPair pair = e.toPair();
                        TsonElement k = pair.getKey();
                        TsonElement v = pair.getKey();
                        String name = HParseHelper.toString(k, ParseMode.ERROR);
                        switch (name) {
                            case "value": {
                                t.setMessage(HParseHelper.toString(v, ParseMode.BEST_ERROR));
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("unsupported " + e);
                            }
                        }
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("unsupported " + e);
                    }
                }
            }
        }
        return t;
    }
}
