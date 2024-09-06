package net.thevpc.halfa.spi.base.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.*;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.*;
import java.util.List;

import net.thevpc.nuts.text.NTextStyleType;

public class HStyleParser {

    //    static Map<String, HStyleValueParser> allStyleParsers = new HashMap<>();
    static Set<String> COMMON_STYLE_PROPS = new HashSet<>();

    static {
        COMMON_STYLE_PROPS.addAll(Arrays.asList(HPropName.STROKE,
                HPropName.SHADOW,
                HPropName.POSITION,
                HPropName.ORIGIN,
                HPropName.SIZE,
                HPropName.NAME,
                HPropName.COLUMNS,
                HPropName.ROWS,
                HPropName.COLSPAN,
                HPropName.ROWSPAN,
                HPropName.GRID_COLOR,
//                HPropName.LINE_COLOR,
                HPropName.ROTATE,
                HPropName.PADDING,
                HPropName.MARGIN,
                HPropName.FONT_SIZE,
                HPropName.DEBUG,
                HPropName.DEBUG_COLOR,
                HPropName.FONT_FAMILY,
                HPropName.RAISED,
                HPropName.FONT_BOLD,
                HPropName.FONT_ITALIC,
                HPropName.FONT_UNDERLINED,
                HPropName.FONT_STRIKE,
                HPropName.BACKGROUND_COLOR,
                HPropName.FOREGROUND_COLOR,
                HPropName.FILL_BACKGROUND,
                HPropName.HIDE,
                HPropName.DRAW_GRID,
                HPropName.COLUMNS_WEIGHT,
                HPropName.ROWS_WEIGHT,
                HPropName.PRESERVE_ASPECT_RATIO,
                HPropName.THEED,
                HPropName.DRAW_CONTOUR,
                HPropName.CLASS,
                HPropName.ANCESTORS,
                HPropName.AT,
                HPropName.TEMPLATE));

        for (NTextStyleType z : NTextStyleType.values()) {
            if (!z.basic()) {
                COMMON_STYLE_PROPS.addAll(Arrays.asList(new String[]{
                        "source-" + z.id() + "-color",
                        "source-" + z.id() + "-background",
                        "source-" + z.id() + "-font-family",
                        "source-" + z.id() + "-font-bold",
                        "source-" + z.id() + "-font-italic",
                        "source-" + z.id() + "-font-underlined",
                }));
            }
        }
        COMMON_STYLE_PROPS.addAll(
                Arrays.asList(
                        "color",
                        "background",
                        "foreground",
                        "bg",
                        "fg",
                        "show",
                        "visible",
                        "fill",
                        "contour"
                )
        );
    }

    public static boolean isCommonStyleProperty(String s) {
        if (NBlankable.isBlank(s)) {
            return false;
        }
        s = HUtils.uid(s);
        return COMMON_STYLE_PROPS.contains(s);
    }


    public static NOptional<HStyleRuleSelector> parseStyleRuleSelector(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                String s = e.toStr().stringValue();
                if (s.isEmpty() || s.equals("*")) {
                    return NOptional.of(DefaultHNodeSelector.ofAny());
                }
                if (s.startsWith(".")) {
                    return NOptional.of(DefaultHNodeSelector.ofClasses(s.substring(1)));
                }
                return NOptional.of(DefaultHNodeSelector.ofName(s));
            }
            case NAME: {
                String n = e.toName().value();
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
                            ObjEx h = ObjEx.of(e.toPair().key());
                            NOptional<String> k = h.asStringOrName();
                            if (k.isPresent()) {
                                switch (HUtils.uid(k.get())) {
                                    case "class": {
                                        ObjEx h2 = ObjEx.of(e.toPair().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            classes.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().addError(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", HUtils.shortName(context.source()), e), context.source());
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", HUtils.shortName(context.source()), e));
                                        }
                                    }
                                    case "name": {
                                        ObjEx h2 = ObjEx.of(e.toPair().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            names.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().addError(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", HUtils.shortName(context.source()), e), context.source());
                                            return NOptional.ofEmpty(
                                                    NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", HUtils.shortName(context.source()), e)
                                            );
                                        }
                                    }
                                    case "type": {
                                        ObjEx h2 = ObjEx.of(e.toPair().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            types.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().addError(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", HUtils.shortName(context.source()), e), context.source());
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", HUtils.shortName(context.source()), e));
                                        }
                                    }
                                    default: {
                                        context.messages().addError(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", HUtils.shortName(context.source()), e), context.source());
                                        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", HUtils.shortName(context.source()), e));
                                    }
                                }
                            } else {
                                context.messages().addError(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", HUtils.shortName(context.source()), e), context.source());
                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", HUtils.shortName(context.source()), e));
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
                            String s = child.toStr().stringValue().trim();
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
                            context.messages().addError(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e), context.source());
                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
                        }
                    }

                }
                return NOptional.of(DefaultHNodeSelector.of(
                        names.toArray(new String[0]),
                        types.toArray(new String[0]),
                        classes.toArray(new String[0])
                ));
            }
            default: {
                context.messages().addError(NMsg.ofC("[%s] invalid style rule selector %s", HUtils.shortName(context.source()), e), context.source());
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", HUtils.shortName(context.source()), e));
            }
        }
    }

    public static NOptional<HStyleRule[]> parseStyleRule(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<HStyleRuleSelector> r = parseStyleRuleSelector(e.toPair().key(), f, context);
                if (!r.isPresent()) {
                    return NOptional.ofEmpty(r.getMessage());
                }
                TsonElement v = e.toPair().value();
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
        context.messages().addError(NMsg.ofC("[%s] invalid style rule %s", HUtils.shortName(context.source()), e), context.source());
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule %s", HUtils.shortName(context.source()), e));
    }


    public static NOptional<HProp[]> parseStyle(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                ObjEx h = ObjEx.of(e.toPair().key());
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HUtils.uid(u.get());
                    return NOptional.of(new HProp[]{new HProp(uid, e.toPair().value())});
                }
                break;
            }
            case NAME: {
                ObjEx h = ObjEx.of(e);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HUtils.uid(u.get());
                    return NOptional.of(new HProp[]{new HProp(uid, Tson.of(true))});
                }
                break;
            }
        }
        //context.messages().addError(NMsg.ofC("[%s] invalid style %s. expected key:value format", context.source(), e),context.source());
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style %s. expected key:value format", HUtils.shortName(context.source()), e));
    }

}
