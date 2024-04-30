package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.halfa.api.model.HDocumentPart;
import net.thevpc.halfa.api.model.HPage;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HText;
import net.thevpc.halfa.spi.nodes.HDocumentItemFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.halfa.spi.nodes.HDocumentPartParserFactory;
import net.thevpc.halfa.spi.nodes.HPagePartParserFactory;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementHeader;
import static net.thevpc.tson.TsonElementType.ALIAS;
import static net.thevpc.tson.TsonElementType.BIG_COMPLEX;
import static net.thevpc.tson.TsonElementType.BIG_DECIMAL;
import static net.thevpc.tson.TsonElementType.BIG_INT;
import static net.thevpc.tson.TsonElementType.BOOLEAN;
import static net.thevpc.tson.TsonElementType.BYTE;
import static net.thevpc.tson.TsonElementType.CHAR;
import static net.thevpc.tson.TsonElementType.DATE;
import static net.thevpc.tson.TsonElementType.DATETIME;
import static net.thevpc.tson.TsonElementType.DOUBLE;
import static net.thevpc.tson.TsonElementType.DOUBLE_COMPLEX;
import static net.thevpc.tson.TsonElementType.FLOAT;
import static net.thevpc.tson.TsonElementType.FLOAT_COMPLEX;
import static net.thevpc.tson.TsonElementType.INT;
import static net.thevpc.tson.TsonElementType.LONG;
import static net.thevpc.tson.TsonElementType.MATRIX;
import static net.thevpc.tson.TsonElementType.NAME;
import static net.thevpc.tson.TsonElementType.NULL;
import static net.thevpc.tson.TsonElementType.OBJECT;
import static net.thevpc.tson.TsonElementType.PAIR;
import static net.thevpc.tson.TsonElementType.REGEX;
import static net.thevpc.tson.TsonElementType.SHORT;
import static net.thevpc.tson.TsonElementType.STRING;
import static net.thevpc.tson.TsonElementType.TIME;
import static net.thevpc.tson.TsonElementType.UPLET;
import net.thevpc.tson.TsonObject;
import net.thevpc.tson.TsonPair;

/**
 *
 * @author vpc
 */
public class DefaultHDocumentItemParserFactory
        implements HDocumentPartParserFactory,
        HPagePartParserFactory {

    @Override
    public NCallableSupport<HDocumentPart> parseDocumentPart(HDocumentItemFactoryParseContext context) {
        TsonElement c = context.element();
        HalfaEngine engine = context.engine();
        NSession session = context.session();
        switch (c.getType()) {
            case OBJECT: {
                TsonObject ff = c.toObject();
                TsonElementHeader h = ff.getHeader();
                if (h != null) {
                    String name = h.name();
                    switch (NNameFormat.LOWER_SNAKE_CASE.format(name)) {
                        case "page": {
                            return NCallableSupport.of(10, () -> {
                                HPage p = engine.newPage();
                                for (TsonElement e : ff.getAll()) {
                                    p.addPart(engine.newPagePart(e).get());
                                }
                                return p;
                            });
                        }
                    }
                }
            }
        }
        NOptional<HPagePart> t = engine.newPagePart(c);
        if (t.isPresent()) {
            return NCallableSupport.of(5, () -> {
                HPage p = engine.newPage();
                p.addPart(t.get(session));
                return p;
            });
        }
        return NCallableSupport.invalid(s -> NMsg.ofPlain("not found document item"));
    }

    @Override
    public NCallableSupport<HPagePart> parsePagePart(HDocumentItemFactoryParseContext context) {
        TsonElement c = context.element();
        HalfaEngine engine = context.engine();
        NSession session = context.session();
        switch (c.getType()) {
            case STRING:
            case BIG_COMPLEX:
            case BIG_INT:
            case BYTE:
            case CHAR:
            case DATE:
            case DATETIME:
            case DOUBLE:
            case DOUBLE_COMPLEX:
            case FLOAT:
            case FLOAT_COMPLEX:
            case INT:
            case BIG_DECIMAL:
            case BOOLEAN:
            case LONG:
            case NAME:
            case NULL:
            case REGEX:
            case SHORT:
            case TIME:
            case MATRIX:
            case UPLET:
            case ALIAS:
            case PAIR: {
                return NCallableSupport.of(10, () -> {
                    return engine.newText(c.getString());
                });

            }
            case OBJECT: {
                TsonObject ff = c.toObject();
                TsonElementHeader h = ff.getHeader();
                if (h != null) {
                    String name = h.name();
                    switch (NNameFormat.LOWER_SNAKE_CASE.format(name)) {
                        case "text": {
                            return NCallableSupport.of(10, () -> toText(ff, context));
                        }
                    }
                }
            }
        }
        throw new NIllegalArgumentException(session, NMsg.ofC("unable to resolve as Document: %s", c));
    }

    private HText toText(TsonObject ff, HDocumentItemFactoryParseContext context) {
        TsonElement c = context.element();
        HalfaEngine engine = context.engine();
        NSession session = context.session();
        HText t = engine.newText();
        for (TsonElement e : ff.getAll()) {
            if (!HParseHelper.fillElementHDocumentItem(t, e)) {
                switch (e.getType()) {
                    case PAIR: {
                        TsonPair pair = e.toKeyValue();
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
