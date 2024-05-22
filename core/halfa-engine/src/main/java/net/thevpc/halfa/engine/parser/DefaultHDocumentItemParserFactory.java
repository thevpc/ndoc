package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.spi.TsonSer;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.halfa.spi.nodes.HNodeParserFactory;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.*;

/**
 * @author vpc
 */
public class DefaultHDocumentItemParserFactory
        implements HNodeParserFactory {


    @Override
    public NCallableSupport<HItem> parseNode(HNodeFactoryParseContext context) {
        TsonElement c = context.element();
        HEngine engine = context.engine();
        HDocumentFactory f = engine.documentFactory();
        NSession session = context.session();
        switch (c.type()) {
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
            case ALIAS: {
                return NCallableSupport.of(10, () -> {
                    return f.text(0, 0, c.getString());
                });
            }
            case PAIR: {
                TsonPair p = c.toPair();
                TsonElement k = p.getKey();
                TsonElement v = p.getValue();
                TsonElementParseHelper kh = new TsonElementParseHelper(k);
                NOptional<String> nn = kh.asStringOrName();
                if (nn.isPresent()) {
                    String nnn = NStringUtils.trim(nn.get());
                    if (nnn.length() > 1 && nnn.startsWith("$")) {
                        return NCallableSupport.of(10, () -> {
                            return f.assign().setLeft(nnn.substring(1)).setRight(
                                    TsonSer.fromTson(v)
                            );
                        });
                    }
                }
                break;
            }
            case OBJECT:
            case FUNCTION:
            case ARRAY: {
                TsonElementExt ee = new TsonElementExt(c);
                if (NBlankable.isBlank(ee.name())) {
                    if (context.expectedTypes().contains(HNodeType.PAGE)) {
                        return NCallableSupport.of(10, () -> {
                            return HItemListParser.readHItemList(c, f, context).get();
                        });
                    }
                } else {
                    switch (ee.name()) {
                        case "import":
                        case "include": {
                            return NCallableSupport.of(10, () -> HImportFileParser.parseImport(c, f, context).get());
                        }
                        case "text": {
                            return NCallableSupport.of(10, () -> HTextParser.parseText(c, f, context).get());
                        }
                        case "eq":
                        case "equation":
                        {
                            return NCallableSupport.of(10, () -> HTextParser.parseEquation(c, f, context).get());
                        }
                        case "page": {
                            return NCallableSupport.of(10, () -> {
                                return HPageParser.parsePage(c, f, context).get();
                            });
                        }
                        case "stack":
                        case "flow":
                        case "grid":
                        case "vgrid":
                        case "v-grid":
                        case "grid-v":
                        case "hgrid":
                        case "h-grid":
                        case "grid-h": {
                            return NCallableSupport.of(10, () -> {
                                return HContainerParser.parseContainer(ee.name(), c, f, context).get();
                            });
                        }
                        case "ul":
                        case "unordered-list":
                        case "ol":
                        case "ordered-list":
                        case "numbered-list": {
                            return NCallableSupport.of(10, () -> {
                                return HContainerParser.parseContainer(ee.name(), c, f, context).get();
                            });
                        }
                        case "rectangle":
                        case "square":
                        case "circle":
                        case "ellipse":
                        case "oval":
                        case "glue":
                        case "hglue":
                        case "h-glue":
                        case "glue-h":
                        case "vglue":
                        case "v-glue":
                        case "glue-v":
                        case "polygon":
                        case "polyline":
                        case "line": {
                            return NCallableSupport.of(10, () -> {
                                return ShapesParser.parseShape(ee.name(), c, f, context).get();
                            });
                        }
                    }
                }
            }
        }
        throw new NIllegalArgumentException(session, NMsg.ofC("[%s] unable to resolve node : %s", context.source(),c));
    }

}
