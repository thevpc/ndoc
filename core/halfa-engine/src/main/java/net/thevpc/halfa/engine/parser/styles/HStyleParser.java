package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.engine.parser.util.HParseHelper;
import net.thevpc.halfa.engine.parser.util.TsonElementParseHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HStyleParser {
    static Map<String, HStyleValueParser> allStyleParsers = new HashMap<>();

    static {
        register(ofColor(HStyleType.BACKGROUND_COLOR, "background-color", "background", "bg"));
        register(ofColor(HStyleType.FOREGROUND_COLOR, "foreground-color", "foreground", "color", "fg"));
        register(ofColor(HStyleType.GRID_COLOR, "grid-color"));
        register(ofColor(HStyleType.LINE_COLOR, "line-color"));
        register(ofInt(HStyleType.COLUMNS, "columns", "cols"));
        register(ofInt(HStyleType.ROWS, "rows"));
        register(ofInt(HStyleType.COLSPAN, "colspan"));
        register(ofInt(HStyleType.ROWSPAN, "rowspan"));
        register(ofPointOrAlign(HStyleType.POSITION, "position", "pos"));
        register(ofPointOrAlign(HStyleType.ORIGIN, "origin"));
        register(ofPointOrAlign(HStyleType.SIZE, "size"));
        register(ofDoubleArr(HStyleType.COLUMNS_WEIGHT, "column-weight", "col-weight", "wcol"));
        register(ofDoubleArr(HStyleType.ROWS_WEIGHT, "row-weight", "wrow"));
        register(ofDoubleSize(HStyleType.FONT_SIZE, "font-size"));
        register(ofString(HStyleType.FONT_FAMILY, "font-family"));
        register(ofBoolean(HStyleType.FONT_BOLD, "bold", "font-bold"));
        register(ofBoolean(HStyleType.FONT_BOLD, "italic", "font-italic"));
        register(ofBoolean(HStyleType.FONT_UNDERLINED, "underlined", "font-underlined"));
        register(ofBoolean(HStyleType.DRAW_CONTOUR, "draw-contour"));
        register(ofBoolean(HStyleType.DRAW_GRID, "draw-grid"));
        register(ofBoolean(HStyleType.DISABLED, "disabled"));
        register(ofBoolean(HStyleType.FILL_BACKGROUND, "fill", "fill-background"));
        register(ofBoolean(HStyleType.PRESERVE_SHAPE_RATIO, "preserve-shape-ratio"));
        register(ofBoolean(HStyleType.RAISED, "raised"));
        register(ofBoolean(HStyleType.THEED, "threed", "three-d"));
        register(ofBoolean(HStyleType.TEMPLATE, "template"));
        register(ofString(HStyleType.NAME, "name"));
        register(ofString(HStyleType.EXTENDS, "extends"));
        register(ofStringArr(HStyleType.STYLE_CLASS, "class", "style-class"));
        register(new HStyleValueParser() {
            @Override
            public String[] ids() {
                return new String[]{"at"};
            }

            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Double2> p = new TsonElementParseHelper(e).asDouble2();
                if (p.isPresent()) {
                    return NOptional.of(
                            new HStyle[]{
                                    HStyles.origin(0, 0),
                                    HStyles.position(p.get().getX(), p.get().getY())
                            }
                    );
                }
                NOptional<HAlign> uu = HAlignEnumParser.parseHAlign(e);
                if (uu.isPresent()) {
                    return NOptional.of(
                            new HStyle[]{
                                    HStyles.origin(uu.get()),
                                    HStyles.position(uu.get())
                            }
                    );
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        });
    }

    private static void register(HStyleValueParser s) {
        for (String id : s.ids()) {
            String uid = HParseHelper.uid(id);
            HStyleValueParser o = allStyleParsers.get(id);
            if (o != null) {
                throw new IllegalArgumentException("clash " + uid);
            }
            allStyleParsers.put(uid, s);
        }
    }

    private static HStyleValueParser ofColor(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Color> r = new TsonElementParseHelper(e).parseColor();
                if (r.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofBoolean(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Boolean> r = new TsonElementParseHelper(e).asBoolean();
                if (r.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofString(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<String> r = new TsonElementParseHelper(e).asStringOrName();
                if (r.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofDoubleSize(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Double> r = new TsonElementParseHelper(e).asDouble();
                if (r.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, r.get())});
                }
                NOptional<String> s = new TsonElementParseHelper(e).asStringOrName();
                if (s.isPresent()) {
                    switch (NNameFormat.LOWER_KEBAB_CASE.format(s.get())) {
                        case "smallest": {
                            return NOptional.of(new HStyle[]{new HStyle(type, 12)});
                        }
                        case "smaller": {
                            return NOptional.of(new HStyle[]{new HStyle(type, 20)});
                        }
                        case "small": {
                            return NOptional.of(new HStyle[]{new HStyle(type, 30)});
                        }
                        case "normal": {
                            return NOptional.of(new HStyle[]{new HStyle(type, 40)});
                        }
                        case "large": {
                            return NOptional.of(new HStyle[]{new HStyle(type, 60)});
                        }
                        case "larger": {
                            return NOptional.of(new HStyle[]{new HStyle(type, 80)});
                        }
                        case "largest": {
                            return NOptional.of(new HStyle[]{new HStyle(type, 100)});
                        }
                    }
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofDoubleArr(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<double[]> r = new TsonElementParseHelper(e).asDoubleArray();
                if (r.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofStringArr(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<String[]> r = new TsonElementParseHelper(e).asStringOrNameArray();
                if (r.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, r.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }

    private static HStyleValueParser ofPointOrAlign(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Double2> p = new TsonElementParseHelper(e).asDouble2();
                if (p.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, p.get())});
                }
                NOptional<HAlign> uu = HAlignEnumParser.parseHAlign(e);
                if (uu.isPresent()) {
                    return NOptional.of(new HStyle[]{new HStyle(type, uu.get())});
                }
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid %s", context.source(), id));
            }
        };
    }


    private static HStyleValueParser ofInt(HStyleType type, String... ids) {
        return new AbstracStyleValueParser(type, ids) {
            @Override
            public NOptional<HStyle[]> parseValue(String id, TsonElement e, HNodeFactoryParseContext context) {
                NOptional<Integer> r = new TsonElementParseHelper(e).asInt();
                if (r.isPresent()) {
                    return NOptional.of(
                            new HStyle[]{new HStyle(HStyleType.COLUMNS, r.get())}
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
                String s = e.toStr().toString();
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
                NOptional<HNodeType> u = HNodeType.parse(n);
                if (u.isPresent()) {
                    return NOptional.of(DefaultHNodeSelector.ofType(u.get()));
                }
                return NOptional.ofEmpty(
                        NMsg.ofC("[%s] invalid style rule selector %s. this is not a valid node type. Classes must start with a dot.", context.source(), e)

                );
            }
            case UPLET: {
                List<String> names = new ArrayList<>();
                List<String> classes = new ArrayList<>();
                List<HNodeType> types = new ArrayList<>();
                for (TsonElement child : e.toUplet().all()) {
                    switch (child.type()) {
                        case PAIR: {
                            TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                            NOptional<String> k = h.asStringOrName();
                            if (k.isPresent()) {
                                switch (HParseHelper.uid(k.get())) {
                                    case "class": {
                                        TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
                                        NOptional<String[]> cc = h2.asStringOrNameArray();
                                        if (cc.isPresent()) {
                                            classes.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", context.source(), e));
                                        }
                                    }
                                    case "name": {
                                        TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
                                        NOptional<String[]> cc = h2.asStringOrNameArray();
                                        if (cc.isPresent()) {
                                            names.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            return NOptional.ofEmpty(
                                                    NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", context.source(), e)
                                            );
                                        }
                                    }
                                    case "type": {
                                        TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
                                        NOptional<String[]> cc = h2.asStringOrNameArray();
                                        if (cc.isPresent()) {
                                            for (String s : cc.get()) {
                                                NOptional<HNodeType> u = HNodeType.parse(s);
                                                if (u.isPresent()) {
                                                    types.add(u.get());
                                                } else {
                                                    return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", context.source(), e));
                                                }
                                            }
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
                                NOptional<HNodeType> u = HNodeType.parse(s);
                                if (u.isPresent()) {
                                    types.add(u.get());
                                } else {
                                    return NOptional.ofEmpty(NMsg.ofC(
                                            "[%s] invalid style rule selector %s. this is not a valid node type. Classes must start with a dot.",
                                            context.source(),
                                            e
                                    ));
                                }
                            }
                            break;
                        }
                        case STRING: {
                            String s = child.toStr().toString().trim();
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
//                NOptional<String> k = h.asStringOrName();
//                if (k.isPresent()) {
//                    switch (HParseHelper.uid(k.get())) {
//                        case "class": {
//                            TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
//                            NOptional<String[]> cc = h2.asStringOrNameArray();
//                            if (cc.isPresent()) {
//                                return NOptional.of(DefaultHNodeSelector.ofClasses(cc.get()));
//                            } else {
//                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
//                            }
//                        }
//                        case "name": {
//                            TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
//                            NOptional<String[]> cc = h2.asStringOrNameArray();
//                            if (cc.isPresent()) {
//                                return NOptional.of(DefaultHNodeSelector.ofName(cc.get()));
//                            } else {
//                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
//                            }
//                        }
//                        case "type": {
//                            TsonElementParseHelper h2 = new TsonElementParseHelper(e.toPair().getValue());
//                            NOptional<String[]> cc = h2.asStringOrNameArray();
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
                        types.toArray(new HNodeType[0]),
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
                        List<HStyle> styles = new ArrayList<>();
                        for (TsonElement el : v.toObject().all()) {
                            NOptional<HStyle[]> s = parseStyle(el, f, context);
                            if (!s.isPresent()) {
                                return NOptional.ofEmpty(s.getMessage());
                            }
                            styles.addAll(Arrays.asList(s.get()));
                        }
                        return NOptional.of(
                                new HStyleRule[]{
                                        DefaultHStyleRule.of(r.get(), styles.toArray(new HStyle[0]))
                                }
                        );
                    }
                }
                break;
            }
        }
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule %s", context.source(), e));
    }

    public static NOptional<HStyle[]> parseStyle(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HParseHelper.uid(u.get());
                    HStyleValueParser p = allStyleParsers.get(uid);
                    if (p != null) {
                        return p.parseValue(u.get(), e.toPair().getValue(), context);
                    }
                }
                break;
            }
            case NAME: {
                TsonElementParseHelper h = new TsonElementParseHelper(e);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HParseHelper.uid(u.get());
                    HStyleValueParser p = allStyleParsers.get(uid);
                    if (p != null) {
                        return p.parseValue(u.get(), Tson.booleanElem(true), context);
                    }
                }
                break;
            }
        }
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style %s. expected key:value format", context.source(), e));
    }

}
