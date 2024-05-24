package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.model.Int2;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.spi.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.halfa.spi.nodes.HNodeParserFactory;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
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
        register(new SimpleHITemNamedObjectParser("polygon") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofPolygon();
            }

            @Override
            protected boolean processChild(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
                switch (e.type()) {
                    case PAIR: {
                        TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                        NOptional<String> uu = h.asStringOrName();
                        if (uu.isPresent()) {
                            String uid = HParseHelper.uid(uu.get());
                            switch (uid) {
                                case "point": {
                                    NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                                    if (p2d.isPresent()) {
                                        ((HPolygon) p).add(p2d.get());
                                    } else {
                                        return false;
                                    }
                                    break;
                                }
                                default: {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                        break;
                    }
                    case UPLET: {
                        NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                        if (p2d.isPresent()) {
                            ((HPolygon) p).add(p2d.get());
                        } else {
                            return false;
                        }
                        break;
                    }
                }
                return false;
            }
        });
        register(new SimpleHITemNamedObjectParser("polyline") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofPolyline();
            }

            @Override
            protected boolean processChild(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
                switch (e.type()) {
                    case PAIR: {
                        TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                        NOptional<String> uu = h.asStringOrName();
                        if (uu.isPresent()) {
                            String uid = HParseHelper.uid(uu.get());
                            switch (uid) {
                                case "point": {
                                    NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                                    if (p2d.isPresent()) {
                                        ((HPolyline) p).add(p2d.get());
                                    } else {
                                        return false;
                                    }
                                    break;
                                }
                                default: {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                        break;
                    }
                    case UPLET: {
                        NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                        if (p2d.isPresent()) {
                            ((HPolyline) p).add(p2d.get());
                        } else {
                            return false;
                        }
                        break;
                    }
                }
                return false;
            }
        });
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
        register(new SimpleHITemNamedObjectParser("line") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofLine();
            }

            @Override
            protected boolean processChild(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
                switch (e.type()) {
                    case PAIR: {
                        TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                        NOptional<String> uu = h.asStringOrName();
                        if (uu.isPresent()) {
                            String uid = HParseHelper.uid(uu.get());
                            switch (uid) {
                                case "from": {
                                    NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                                    if (p2d.isPresent()) {
                                        ((HLine) p).setFrom(p2d.get());
                                    } else {
                                        return false;
                                    }
                                    break;
                                }
                                case "to": {
                                    NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                                    if (p2d.isPresent()) {
                                        ((HLine) p).setTo(p2d.get());
                                    } else {
                                        return false;
                                    }
                                    break;
                                }
                                case "point": {
                                    NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                                    if (p2d.isPresent()) {
                                        addLinePoint(((HLine) p), p2d.get());
                                    } else {
                                        return false;
                                    }
                                    break;
                                }
                                default: {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                        break;
                    }
                    case UPLET: {
                        NOptional<Double2> p2d = new TsonElementParseHelper(e.toPair().getValue()).asDouble2();
                        if (p2d.isPresent()) {
                            addLinePoint(((HLine) p), p2d.get());
                        } else {
                            return false;
                        }
                        break;
                    }
                }
                return false;
            }
        });
        register(new SimpleHITemNamedObjectParser("image") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofImage();
            }
        });
        register(new SimpleHITemNamedObjectParser("text") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofText();
            }

            @Override
            protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
                switch (e.type()) {
                    case STRING: {
                        ((HText) p).setMessage(e.getString());
                        return true;
                    }
                }
                return false;
            }
        });
        register(new SimpleHITemNamedObjectParser("equation", "eq") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofEquation();
            }

            @Override
            protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
                switch (e.type()) {
                    case STRING: {
                        ((HLatexEquation) p).setLatex(e.getString());
                        return true;
                    }
                }
                return false;
            }
        });
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
        register(new SimpleHITemNamedObjectParser("grid") {
            @Override
            protected HNode node(HDocumentFactory f) {
                return f.ofGrid();
            }

            @Override
            protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
                if (
                        !p.getStyle(HStyleType.COLUMNS).isPresent()
                                && !p.getStyle(HStyleType.ROWS).isPresent()) {
                    NOptional<Int2> dp = new TsonElementParseHelper(e).asInt2();
                    if (dp.isPresent()) {
                        p.set(HStyles.columns(dp.get().getX()));
                        p.set(HStyles.rows(dp.get().getY()));
                        return true;
                    }
                } else if (!p.getStyle(HStyleType.COLUMNS).isPresent()) {
                    NOptional<Integer> dp = new TsonElementParseHelper(e).asInt();
                    p.set(HStyles.columns(dp.get()));
                    return true;
                } else if (!p.getStyle(HStyleType.ROWS).isPresent()) {
                    NOptional<Integer> dp = new TsonElementParseHelper(e).asInt();
                    p.set(HStyles.rows(dp.get()));
                    return true;
                }
                return false;
            }
        });
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

        register(new AbstractHITemNamedObjectParser("import", "include") {
            @Override
            public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
                switch (tsonElement.type()) {
                    case FUNCTION: {
                        List<TsonElement> u = tsonElement.toFunction().all();
                        if (u.size() == 0) {
                            return NOptional.ofError(s -> NMsg.ofC("missing path argument : %s", tsonElement));
                        }
                        HNode[] parents = context.parents();
                        HNode putInto = parents.length == 0 ? null : parents[parents.length - 1];
                        List<HItem> loaded = new ArrayList<>();
                        boolean someLoaded = false;
                        for (TsonElement ee : u) {
                            TsonElementParseHelper t = new TsonElementParseHelper(ee);
                            NOptional<String[]> p = t.asStringOrNameArray();
                            if (p.isPresent()) {
                                someLoaded = true;
                                for (String sp : p.get()) {
                                    HashSet<HNodeType> expected = new HashSet<>(Arrays.asList(HNodeType.values()));
                                    if (putInto != null) {
                                        switch (putInto.type()) {
                                            case PAGE_GROUP: {
                                                break;
                                            }
                                            case PAGE: {
                                                expected.remove(HNodeType.PAGE_GROUP);
                                                break;
                                            }
                                            default: {
                                                expected.remove(HNodeType.PAGE);
                                                expected.remove(HNodeType.PAGE_GROUP);
                                                break;
                                            }
                                        }
                                    }
                                    List<NPath> list = context.resolvePath(sp).walkGlob().toList();
                                    list.sort(Comparator.comparing(NPath::toString));
                                    for (NPath nPath : list) {
                                        if (nPath.isRegularFile()) {
                                            NOptional<HItem> se = context.engine().loadNode(putInto, expected, nPath);
                                            if (se.isPresent()) {
                                                loaded.add(se.get());
                                            } else {
                                                return NOptional.ofError(s -> NMsg.ofC("invalid include. error loading : %s", nPath));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (someLoaded) {
                            return NOptional.of(new HItemList().addAll(loaded));
                        }
                    }
                }
                return NOptional.ofNamedEmpty("include elements");
            }
        });

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


    private static boolean addLinePoint(HLine line, Double2 point) {
        if (point != null) {
            if (line.from() == null) {
                line.setFrom(point);
                return true;
            } else if (line.to() == null) {
                line.setTo(point);
                return true;
            }
        }
        return false;
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
                    }else if(nnn.equals("styles")){
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
