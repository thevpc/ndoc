package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.*;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HStyleParser {
    static Map<String, HStyleValueParser> allStyleParsers = new HashMap<>();

    static {
        register(ofColor(HPropName.BACKGROUND_COLOR, "background-color", "background", "bg"));
        register(ofColor(HPropName.FOREGROUND_COLOR, "foreground-color", "foreground", "color", "fg"));
        register(ofColor(HPropName.GRID_COLOR, "grid-color"));
        register(ofColor(HPropName.LINE_COLOR, "line-color"));
        register(ofInt(HPropName.COLUMNS, "columns", "cols"));
        register(ofBoolean(HPropName.XCOMPACT, "xcompact"));
        register(ofBoolean(HPropName.YCOMPACT, "ycompact"));
        register(ofInt(HPropName.ROWS, "rows"));
        register(ofInt(HPropName.COLSPAN, "colspan"));
        register(ofInt(HPropName.ROWSPAN, "rowspan"));
        register(ofPointOrAlign(HPropName.POSITION, "position", "pos"));
        register(ofPointOrAlign(HPropName.ORIGIN, "origin"));
        register(ofDoubleOrPointOrAlign(HPropName.SIZE, "size"));
        register(ofDoubleArrayOrDouble(HPropName.COLUMNS_WEIGHT, "column-weight", "col-weight", "wcol"));
        register(ofDoubleArrayOrDouble(HPropName.ROWS_WEIGHT, "row-weight", "wrow"));
        register(ofDoubleSize(HPropName.FONT_SIZE, "font-size"));
        register(ofString(HPropName.FONT_FAMILY, "font-family"));
        register(ofBoolean(HPropName.FONT_BOLD, "bold", "font-bold"));
        register(ofBoolean(HPropName.FONT_BOLD, "italic", "font-italic"));
        register(ofBoolean(HPropName.FONT_UNDERLINED, "underlined", "font-underlined"));
        register(ofBoolean(HPropName.DRAW_CONTOUR, new String[]{"draw-contour"}, new String[]{"no-contour"}));
        register(ofBoolean(HPropName.DRAW_GRID, "draw-grid"));
        register(ofBoolean(HPropName.DISABLED, new String[]{"disabled"}, new String[]{"enabled"}));
        register(ofBoolean(HPropName.FILL_BACKGROUND, "fill", "fill-background"));
        register(ofBoolean(HPropName.PRESERVE_SHAPE_RATIO, "preserve-shape-ratio"));
        register(ofBoolean(HPropName.RAISED, "raised"));
        register(ofBoolean(HPropName.THEED, "threed", "three-d"));
        register(ofBoolean(HPropName.TEMPLATE, "template"));
        register(ofString(HPropName.NAME, "name"));
        register(ofStringArrayOrString(HPropName.ANCESTORS, "ancestors"));
        register(ofStringArrayOrString(HPropName.STYLE_CLASS, "class", "style-class"));
        register(ofPadding(HPropName.PADDING, "padding"));
        register(ofRotation(HPropName.ROTATE, "rotate"));
        register(ofTson(HPropName.STROKE, "stroke"));
        register(new AtHStyleValueParser());

        register(ofIntOrBoolean(HPropName.DEBUG, "debug"));
        register(ofColor(HPropName.DEBUG_COLOR, "debug-color"));

    }


    private static void register(HStyleValueParser s) {
        for (String id : s.ids()) {
            String uid = HUtils.uid(id);
            HStyleValueParser o = allStyleParsers.get(uid);
            if (o != null) {
                throw new IllegalArgumentException("clash " + uid+" and "+Arrays.asList(o.ids())+" in "+s+" vs "+o);
            }
            allStyleParsers.put(uid, s);
        }
    }

    private static HStyleValueParser ofColor(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Color> r = new ObjEx(e).parseColor();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofBoolean(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Boolean> r = new ObjEx(e).asBoolean();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofIntOrBoolean(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                ObjEx objEx = new ObjEx(e);
                NOptional<Integer> i = objEx.asInt();
                if (i.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, i.get())});
                }
                NOptional<Boolean> r = objEx.asBoolean();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get() ? 1 : 0)});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofBoolean(String type, String[] trueValues, String[] falseValues) {
        Set<String> acceptable = new HashSet<>();
        Set<String> trueSet = new HashSet<>();
        Set<String> falseSet = new HashSet<>();
        if (trueValues != null) {
            for (String v : trueValues) {
                if (!NBlankable.isBlank(v)) {
                    v = NNameFormat.LOWER_KEBAB_CASE.format(v);
                    acceptable.add(v);
                    trueSet.add(v);
                }
            }
        }
        if (falseValues != null) {
            for (String v : falseValues) {
                if (!NBlankable.isBlank(v)) {
                    v = NNameFormat.LOWER_KEBAB_CASE.format(v);
                    acceptable.add(v);
                    falseSet.add(v);
                }
            }
        }
        return new AbstracStyleValueParser(type, acceptable.toArray(new String[0])) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                String kk = NNameFormat.LOWER_KEBAB_CASE.format(id);
                if (trueSet.contains(kk)) {
                    return NOptional.of(new HProp[]{new HProp(type, true)});
                }
                if (falseSet.contains(kk)) {
                    return NOptional.of(new HProp[]{new HProp(type, false)});
                }
                return NOptional.of(new HProp[]{});
            }
        };
    }

    private static HStyleValueParser ofString(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<String> r = new ObjEx(e).asString();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofDouble4(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Double4> r = new ObjEx(e).asDouble4();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofPadding(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Padding> r = new ObjEx(e).asPadding();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofRotation(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Rotation> r = new ObjEx(e).asRotation();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofTson(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                return NOptional.of(new HProp[]{new HProp(type, e)});
            }
        };
    }

    private static HStyleValueParser ofDoubleSize(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Double> r = new ObjEx(e).asDouble();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                NOptional<String> s = new ObjEx(e).asString();
                if (s.isPresent()) {
                    switch (NNameFormat.LOWER_KEBAB_CASE.format(s.get())) {
                        case "smallest": {
                            return NOptional.of(new HProp[]{new HProp(type, 12)});
                        }
                        case "smaller": {
                            return NOptional.of(new HProp[]{new HProp(type, 20)});
                        }
                        case "small": {
                            return NOptional.of(new HProp[]{new HProp(type, 30)});
                        }
                        case "normal": {
                            return NOptional.of(new HProp[]{new HProp(type, 40)});
                        }
                        case "large": {
                            return NOptional.of(new HProp[]{new HProp(type, 60)});
                        }
                        case "larger": {
                            return NOptional.of(new HProp[]{new HProp(type, 80)});
                        }
                        case "largest": {
                            return NOptional.of(new HProp[]{new HProp(type, 100)});
                        }
                        default: {
                            String ss = NStringUtils.trim(s.get());
                            if (ss.matches("(-)?[0-9]+")) {
                                return NOptional.of(new HProp[]{new HProp(type, Double.parseDouble(ss))});
                            }
                        }
                    }
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofDoubleArrayOrDouble(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<double[]> r = new ObjEx(e).asDoubleArrayOrDouble();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofStringArrayOrString(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<String[]> r = new ObjEx(e).asStringArrayOrString();
                if (r.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofPointOrAlign(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Double2> p = new ObjEx(e).asDouble2();
                if (p.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, p.get())});
                }
                NOptional<HAlign> uu = HAlignEnumParser.parseHAlign(e);
                if (uu.isPresent()) {
                    return NOptional.of(new HProp[]{new HProp(type, uu.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofDoubleOrPointOrAlign(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                {
                    NOptional<Double> p = new ObjEx(e).asDouble();
                    if (p.isPresent()) {
                        return NOptional.of(new HProp[]{new HProp(type, new Double2(p.get(), p.get()))});
                    }
                }
                {
                    NOptional<Double2> p = new ObjEx(e).asDouble2();
                    if (p.isPresent()) {
                        return NOptional.of(new HProp[]{new HProp(type, p.get())});
                    }
                }
                {
                    NOptional<HAlign> p = HAlignEnumParser.parseHAlign(e);
                    if (p.isPresent()) {
                        return NOptional.of(new HProp[]{new HProp(type, p.get())});
                    }
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }


    private static HStyleValueParser ofInt(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Integer> r = new ObjEx(e).asInt();
                if (r.isPresent()) {
                    return NOptional.of(
                            new HProp[]{new HProp(type, r.get())}
                    );
                } else {
                    return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
                }
            }
        };
    }

    private static HStyleValueParser ofDouble(String type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Double> r = new ObjEx(e).asDouble();
                if (r.isPresent()) {
                    return NOptional.of(
                            new HProp[]{new HProp(type, r.get())}
                    );
                } else {
                    return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
                }
            }
        };
    }

    public static NOptional<HStyleRuleSelector> parseStyleRuleSelector(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                String s = e.toStr().getValue();
                if (s.isEmpty() || s.equals("*")) {
                    return NOptional.of(DefaultHNodeSelector.ofAny());
                }
                if (s.startsWith(".")) {
                    return NOptional.of(DefaultHNodeSelector.ofClasses(s.substring(1)));
                }
                return NOptional.of(DefaultHNodeSelector.ofName(s));
            }
            case NAME: {
                String n = e.toName().getName();
                if (n.startsWith(".")) {
                    return NOptional.of(DefaultHNodeSelector.ofClasses(n.substring(1)));
                }
                return NOptional.of(DefaultHNodeSelector.ofType(n));
            }
            case UPLET: {
                List<String> names = new ArrayList<>();
                List<String> classes = new ArrayList<>();
                List<String> types = new ArrayList<>();
                for (TsonElement child : e.toUplet().all()) {
                    switch (child.type()) {
                        case PAIR: {
                            ObjEx h = new ObjEx(e.toPair().getKey());
                            NOptional<String> k = h.asString();
                            if (k.isPresent()) {
                                switch (HUtils.uid(k.get())) {
                                    case "class": {
                                        ObjEx h2 = new ObjEx(e.toPair().getValue());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            classes.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", context.source(), e));
                                        }
                                    }
                                    case "name": {
                                        ObjEx h2 = new ObjEx(e.toPair().getValue());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            names.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            return NOptional.ofEmpty(
                                                    NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", context.source(), e)
                                            );
                                        }
                                    }
                                    case "type": {
                                        ObjEx h2 = new ObjEx(e.toPair().getValue());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            types.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", context.source(), e));
                                        }
                                    }
                                    default: {
                                        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", context.source(), e));
                                    }
                                }
                            } else {
                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", context.source(), e));
                            }
                        }
                        case NAME: {
                            String s = child.toName().toString().trim();
                            if (s.isEmpty() || s.equals("*")) {
                                //
                            } else if (s.startsWith(".")) {
                                classes.add(s.substring(1));
                            } else {
                                types.add(s);
                            }
                            break;
                        }
                        case STRING: {
                            String s = child.toStr().getValue().trim();
                            if (s.isEmpty() || s.equals("*")) {
                                //
                            } else if (s.startsWith(".")) {
                                classes.add(s.substring(1));
                            } else {
                                names.add(s);
                            }
                            break;
                        }
                        default: {
                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
                        }
                    }

                }
//                TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
//                NOptional<String> k = h.asString();
//                if (k.isPresent()) {
//                    switch (HParseHelper.uid(k.get())) {
//                        case "class": {
//                            TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
//                            NOptional<String[]> cc = h2.asStringArray();
//                            if (cc.isPresent()) {
//                                return NOptional.of(DefaultHNodeSelector.ofClasses(cc.get()));
//                            } else {
//                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
//                            }
//                        }
//                        case "name": {
//                            TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
//                            NOptional<String[]> cc = h2.asStringArray();
//                            if (cc.isPresent()) {
//                                return NOptional.of(DefaultHNodeSelector.ofName(cc.get()));
//                            } else {
//                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
//                            }
//                        }
//                        case "type": {
//                            TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
//                            NOptional<String[]> cc = h2.asStringArray();
//                            if (cc.isPresent()) {
//                                List<HNodeType> pp = new ArrayList<>();
//                                for (String s : cc.get()) {
//                                    NOptional<HNodeType> u = HNodeTypeEnumParser.parse(s);
//                                    if (u.isPresent()) {
//                                        pp.add(u.get());
//                                    } else {
//                                        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
//                                    }
//                                }
//                                return NOptional.of(DefaultHNodeSelector.ofType(pp.toArray(new HNodeType[0])));
//                            } else {
//                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
//                            }
//                        }
//                        default: {
//                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
//                        }
//                    }
//                }
//                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
                return NOptional.of(DefaultHNodeSelector.of(
                        names.toArray(new String[0]),
                        types.toArray(new String[0]),
                        classes.toArray(new String[0])
                ));
            }
            default: {
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
            }
        }
    }

    public static NOptional<HStyleRule[]> parseStyleRule(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<HStyleRuleSelector> r = parseStyleRuleSelector(e.toPair().getKey(), f, context);
                if (!r.isPresent()) {
                    return NOptional.ofEmpty(r.getMessage());
                }
                TsonElement v = e.toPair().getValue();
                switch (v.type()) {
                    case OBJECT: {
                        List<HProp> styles = new ArrayList<>();
                        for (TsonElement el : v.toObject().all()) {
                            NOptional<HProp[]> s = parseStyle(el, f, context);
                            if (!s.isPresent()) {
                                s = parseStyle(el, f, context);
                                return NOptional.ofEmpty(s.getMessage());
                            }
                            styles.addAll(Arrays.asList(s.get()));
                        }
                        return NOptional.of(
                                new HStyleRule[]{
                                        DefaultHStyleRule.of(r.get(), styles.toArray(new HProp[0]))
                                }
                        );
                    }
                }
                break;
            }
        }
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule %s", context.source(), e));
    }

    public static boolean acceptStyleName(String e) {
        String uid = HUtils.uid(e);
        return allStyleParsers.containsKey(uid);
    }

    public static NOptional<HProp[]> parseStyle(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                ObjEx h = new ObjEx(e.toPair().getKey());
                NOptional<String> u = h.asString();
                if (u.isPresent()) {
                    String uid = HUtils.uid(u.get());
                    HStyleValueParser p = allStyleParsers.get(uid);
                    if (p != null) {
                        return p.parseValue(u.get(), e.toPair().getValue(), context);
                    }
                }
                break;
            }
            case NAME: {
                ObjEx h = new ObjEx(e);
                NOptional<String> u = h.asString();
                if (u.isPresent()) {
                    String uid = HUtils.uid(u.get());
                    HStyleValueParser p = allStyleParsers.get(uid);
                    if (p != null) {
                        return p.parseValue(u.get(), Tson.ofBoolean(true), context);
                    }
                }
                break;
            }
        }
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style %s. expected key:value format", context.source(), e));
    }

    private static class AtHStyleValueParser implements HStyleValueParser {
        @Override
        public String[] ids() {
            return new String[]{"at"};
        }

        @Override
        public NOptional<HProp[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
            NOptional<Double2> p = new ObjEx(e).asDouble2();
            if (p.isPresent()) {
                return NOptional.of(
                        new HProp[]{
                                HProps.origin(0, 0),
                                HProps.position(p.get().getX(), p.get().getY())
                        }
                );
            }
            NOptional<HAlign> uu = HAlignEnumParser.parseHAlign(e);
            if (uu.isPresent()) {
                return NOptional.of(
                        new HProp[]{
                                HProps.origin(uu.get()),
                                HProps.position(uu.get())
                        }
                );
            }
            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
        }

        @Override
        public String toString() {
            return "HStyleValueParser(at)";
        }
    }
}
