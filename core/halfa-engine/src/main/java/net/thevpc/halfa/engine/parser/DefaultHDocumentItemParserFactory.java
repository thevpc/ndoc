package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.parser.nodes.*;
import net.thevpc.halfa.engine.parser.styles.StylesHITemNamedObjectParser;
import net.thevpc.halfa.engine.parser.util.HParseHelper;
import net.thevpc.halfa.engine.parser.util.TsonElementExt;
import net.thevpc.halfa.engine.parser.util.TsonElementParseHelper;
import net.thevpc.halfa.spi.HUtils;
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

import java.util.*;

/**
 * @author vpc
 */
public class DefaultHDocumentItemParserFactory
        implements HNodeParserFactory {
    static Map<String, HITemNamedObjectParser> allParsers = new HashMap<>();

    static {
        register(new SimpleHITemNamedObjectParser("rectangle") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofRectangle();
            }
        });
        register(new SimpleHITemNamedObjectParser("square") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofSquare();
            }
        });
        register(new SimpleHITemNamedObjectParser("ellipse", "oval") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofEllipse();
            }
        });
        register(new SimpleHITemNamedObjectParser("circle") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofCircle();
            }
        });
        register(new PolygonHITemNamedObjectParser());
        register(new PolylineHITemNamedObjectParser());
        register(new SimpleHITemNamedObjectParser("triangle") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofTriangle();
            }
        });
        register(new SimpleHITemNamedObjectParser("hexagon") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofHexagon();
            }
        });
        register(new SimpleHITemNamedObjectParser("octagon") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofOctagon();
            }
        });
        register(new SimpleHITemNamedObjectParser("void") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofVoid();
            }
        });
        register(new SimpleHITemNamedObjectParser("glue") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofGlue();
            }
        });
        register(new SimpleHITemNamedObjectParser("vertical-glue", "vglue") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofGlueV();
            }
        });
        register(new SimpleHITemNamedObjectParser("horizontal-glue", "hglue") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofGlueH();
            }
        });
        register(new LineHITemNamedObjectParser());
        register(new SimpleHITemNamedObjectParser("image") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofImage();
            }
        });
        register(new TextHITemNamedObjectParser());
        register(new LatexEquationHITemNamedObjectParser());
        register(new LatexHITemNamedObjectParser());
        register(new SimpleHITemNamedObjectParser("page") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofPage();
            }
        });
        register(new SimpleHITemNamedObjectParser("stack") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofStack();
            }
        });
        register(new SimpleHITemNamedObjectParser("flow") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofFlow();
            }
        });
        register(new GridHITemNamedObjectParser());
        register(new SimpleHITemNamedObjectParser(
                "vgrid",
                "v-grid",
                "grid-v",
                "vertical-grid"
        ) {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofGridV();
            }
        });
        register(new SimpleHITemNamedObjectParser(
                "hgrid",
                "h-grid",
                "grid-h",
                "horizontal-grid"
        ) {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofGridV();
            }
        });
        register(new SimpleHITemNamedObjectParser(
                "ul", "unordered-list"
        ) {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofUnorderedList();
            }
        });
        register(new SimpleHITemNamedObjectParser(
                "ol",
                "ordered-list",
                "numbered-list"
        ) {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofOrderedList();
            }
        });

        register(new ImportHITemNamedObjectParser());

        register(new StylesHITemNamedObjectParser());
    }

    private static void register(HITemNamedObjectParser s) {
        for (String id : s.ids()) {
            String uid = HParseHelper.uid(id);
            HITemNamedObjectParser o = allParsers.get(id);
            if (o != null) {
                throw new IllegalArgumentException("clash : " + uid + " is already registered as item parser");
            }
            allParsers.put(uid, s);
        }
    }


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
            case NULL:
            case REGEX:
            case SHORT:
            case TIME:
            case MATRIX:
            case UPLET:
            case ALIAS: {
                return NCallableSupport.of(10, () -> {
                    return f.ofText(c.getString());
                });
            }
            case NAME: {
                String name = c.toName().getName();
                String uid = HParseHelper.uid(name);
                HITemNamedObjectParser p = allParsers.get(uid);
                if (p != null) {
                    return NCallableSupport.of(10, () -> {
                        try {
                            return p.parseItem(name, c, context).get();
                        } catch (Exception ex) {
                            return p.parseItem(name, c, context).get();
                        }
                    });
                }
                return NCallableSupport.of(10, () -> {
                    return f.ofText(0, 0, name);
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
                            return f.ofAssign()
                                    .setLeft(nnn.substring(1))
                                    .setRight(HUtils.fromTson(v)
                                    );
                        });
                    } else if (nnn.equals("styles")) {
                        return NCallableSupport.of(10, () -> {
                            HITemNamedObjectParser pp = allParsers.get("styles");
                            NOptional<HItem> styles = pp.parseItem("styles", v, context);
                            return styles.get();
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
                    String uid = HParseHelper.uid(ee.name());
                    HITemNamedObjectParser p = allParsers.get(uid);
                    if (p != null) {
                        return NCallableSupport.of(10, () -> {
                            try {
                                return p.parseItem(ee.name(), c, context).get();
                            } catch (Exception ex) {
                                return p.parseItem(ee.name(), c, context).get();
                            }
                        });
                    }
                }
            }
        }
        throw new NIllegalArgumentException(session, NMsg.ofC("[%s] unable to resolve node : %s", context.source(), c));
    }

}
