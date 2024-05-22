package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HLatexEquation;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonObject;
import net.thevpc.tson.TsonPair;

public class HTextParser {
    public static NOptional<HText> parseText(TsonElement ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HEngine engine = context.engine();
        HText t = engine.documentFactory().text(0, 0, "");
        for (TsonElement e : new TsonElementExt(ff).args()) {
            if (!HParseHelper.fillElementHDocumentItem(t, e)) {
                switch (e.type()) {
                    case STRING: {
                        t.setMessage(e.toStr().getString());
                        break;
                    }
                    case PAIR: {
                        NOptional<HItem> u = HStyleParser.parseStyle(e, f, context);
                        if (u.isPresent()) {
                            t.append(u.get());
                        }else{
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
                                    return NOptional.ofNamedError("invalid "+e+" for text");
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        return NOptional.ofNamedError("invalid "+e+" for text");
                    }
                }
            }
        }
        return NOptional.of(t);
    }
    public static NOptional<HLatexEquation> parseEquation(TsonElement ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HEngine engine = context.engine();
        HLatexEquation t = engine.documentFactory().equation();
        for (TsonElement e : new TsonElementExt(ff).args()) {
            if (!HParseHelper.fillElementHDocumentItem(t, e)) {
                switch (e.type()) {
                    case STRING: {
                        t.setLatex(e.toStr().getString());
                        break;
                    }
                    case PAIR: {
                        NOptional<HItem> u = HStyleParser.parseStyle(e, f, context);
                        if (u.isPresent()) {
                            t.append(u.get());
                        }else{
                            TsonPair pair = e.toPair();
                            TsonElement k = pair.getKey();
                            TsonElement v = pair.getKey();
                            String name = HParseHelper.toString(k, ParseMode.ERROR);
                            switch (name) {
                                case "value":
                                case "latex":
                                {
                                    t.setLatex(e.toStr().getString());
                                    break;
                                }
                                default: {
                                    return NOptional.ofNamedError("invalid "+e+" for text");
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        return NOptional.ofNamedError("invalid "+e+" for text");
                    }
                }
            }
        }
        return NOptional.of(t);
    }
}
