package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.halfa.spi.nodes.HNodeParserFactory;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.tson.*;

/**
 * @author vpc
 */
public class DefaultHDocumentItemParserFactory
        implements HNodeParserFactory {


    @Override
    public NCallableSupport<HNode> parseNode(HNodeFactoryParseContext context) {
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
            case ALIAS:
            case PAIR: {
                return NCallableSupport.of(10, () -> {
                    return f.text(0, 0, c.getString());
                });

            }
            case OBJECT: {
                TsonObject ff = c.toObject();
                TsonElementHeader h = ff.getHeader();
                if (h != null) {
                    String name = HParseHelper.uid(h.name());
                    switch (name) {
                        case "include": {
                            return NCallableSupport.of(10, () -> IncludeParser.parseInclude(ff, f, context).get());
                        }
                        case "text": {
                            return NCallableSupport.of(10, () -> HTextParser.parseText(ff, f, context));
                        }
                        case "page": {
                            return NCallableSupport.of(10, () -> {
                                return HPageParser.parsePage(ff, f, context).get();
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
                        case "grid-h":
                        {
                            return NCallableSupport.of(10, () -> {
                                return HStackParser.parseContainer(name,ff, f, context).get();
                            });
                        }
                        case "ul":
                        case "unordered-list":
                        case "ol":
                        case "ordered-list":
                        case "numbered-list":
                        {
                            return NCallableSupport.of(10, () -> {
                                return HStackParser.parseContainer(name,ff, f, context).get();
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
                        case "line":
                        {
                            return NCallableSupport.of(10, () -> {
                                return ShapesParser.parseShape(name,ff, f, context).get();
                            });
                        }
                    }
                } else {
                    //this is a container!
                    if (context.expectedTypes().contains(HNodeType.PAGE)) {
                        return NCallableSupport.of(10, () -> {
                            return HPageGroupParser.readPageGroup(ff, f, context).get();
                        });
                    }else{

                    }
                }

            }
        }
        throw new NIllegalArgumentException(session, NMsg.ofC("unable to resolve as Document: %s", c));
    }


}
