package net.thevpc.ntexup.engine.parser;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.style.*;
import net.thevpc.ntexup.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.nuts.util.*;

import net.thevpc.nuts.elem.NElement;

import java.util.*;
import java.util.List;

import net.thevpc.nuts.text.NTextStyleType;

public class HStyleParser {

    //    static Map<String, HStyleValueParser> allStyleParsers = new HashMap<>();
    static Set<String> COMMON_STYLE_PROPS = new HashSet<>();

    static {
        COMMON_STYLE_PROPS.addAll(Arrays.asList(NDocPropName.STROKE,
                NDocPropName.SHADOW,
                NDocPropName.POSITION,
                NDocPropName.ORIGIN,
                NDocPropName.SIZE,
                NDocPropName.NAME,
                NDocPropName.COLUMNS,
                NDocPropName.ROWS,
                NDocPropName.COLSPAN,
                NDocPropName.ROWSPAN,
                NDocPropName.GRID_COLOR,
//                HPropName.LINE_COLOR,
                NDocPropName.ROTATE,
                NDocPropName.PADDING,
                NDocPropName.MARGIN,
                NDocPropName.FONT_SIZE,
                NDocPropName.DEBUG,
                NDocPropName.DEBUG_COLOR,
                NDocPropName.FONT_FAMILY,
                NDocPropName.RAISED,
                NDocPropName.FONT_BOLD,
                NDocPropName.FONT_ITALIC,
                NDocPropName.FONT_UNDERLINED,
                NDocPropName.FONT_STRIKE,
                NDocPropName.BACKGROUND_COLOR,
                NDocPropName.FOREGROUND_COLOR,
                NDocPropName.FILL_BACKGROUND,
                NDocPropName.HIDE,
                NDocPropName.DRAW_GRID,
                NDocPropName.COLUMNS_WEIGHT,
                NDocPropName.ROWS_WEIGHT,
                NDocPropName.PRESERVE_ASPECT_RATIO,
                NDocPropName.THEED,
                NDocPropName.DRAW_CONTOUR,
                NDocPropName.CLASS,
                NDocPropName.AT
        ));

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


    public static NOptional<NDocStyleRuleSelector> parseStyleRuleSelector(NElement e, NTxDocumentFactory f, NDocNodeFactoryParseContext context) {
        switch (e.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                String s = e.asStringValue().get();
                if (s.isEmpty() || s.equals("*")) {
                    return NOptional.of(DefaultNDocNodeSelector.ofAny());
                }
                if (s.startsWith(".")) {
                    return NOptional.of(DefaultNDocNodeSelector.ofClasses(s.substring(1)));
                }
                return NOptional.of(DefaultNDocNodeSelector.ofName(s));
            }
            case NAME: {
                String n = e.asStringValue().get();
                if (n.startsWith(".")) {
                    return NOptional.of(DefaultNDocNodeSelector.ofClasses(n.substring(1)));
                }
                return NOptional.of(DefaultNDocNodeSelector.ofType(n));
            }
            case UPLET:
            case NAMED_UPLET:
            {
                List<String> names = new ArrayList<>();
                List<String> classes = new ArrayList<>();
                List<String> types = new ArrayList<>();
                for (NElement child : e.asUplet().get().params()) {
                    switch (child.type()) {
                        case PAIR: {
                            NDocValue h = NDocValue.of(e.asPair().get().key());
                            NOptional<String> k = h.asStringOrName();
                            if (k.isPresent()) {
                                switch (NDocUtils.uid(k.get())) {
                                    case "class": {
                                        NDocValue h2 = NDocValue.of(e.asPair().get().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            classes.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().log(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", NDocUtils.shortName(context.source()), e).asSevere(), context.source());
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", NDocUtils.shortName(context.source()), e));
                                        }
                                    }
                                    case "name": {
                                        NDocValue h2 = NDocValue.of(e.asPair().get().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            names.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().log(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", NDocUtils.shortName(context.source()), e).asSevere(), context.source());
                                            return NOptional.ofEmpty(
                                                    NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", NDocUtils.shortName(context.source()), e)
                                            );
                                        }
                                    }
                                    case "type": {
                                        NDocValue h2 = NDocValue.of(e.asPair().get().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            types.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().log(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", NDocUtils.shortName(context.source()), e).asSevere(), context.source());
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", NDocUtils.shortName(context.source()), e));
                                        }
                                    }
                                    default: {
                                        context.messages().log(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", NDocUtils.shortName(context.source()), e).asSevere(), context.source());
                                        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", NDocUtils.shortName(context.source()), e));
                                    }
                                }
                            } else {
                                context.messages().log(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", NDocUtils.shortName(context.source()), e).asSevere(), context.source());
                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", NDocUtils.shortName(context.source()), e));
                            }
                        }
                        case NAME: {
                            String s = child.asStringValue().get().trim();
                            if (s.isEmpty() || s.equals("*")) {
                                //
                            } else if (s.startsWith(".")) {
                                classes.add(s.substring(1));
                            } else {
                                types.add(s);
                            }
                            break;
                        }
                        case DOUBLE_QUOTED_STRING:
                        case SINGLE_QUOTED_STRING:
                        case ANTI_QUOTED_STRING:
                        case TRIPLE_DOUBLE_QUOTED_STRING:
                        case TRIPLE_SINGLE_QUOTED_STRING:
                        case TRIPLE_ANTI_QUOTED_STRING:
                        case LINE_STRING:
                        {
                            String s = child.asStringValue().get().trim();
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
                            context.messages().log(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e).asSevere(), context.source());
                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e));
                        }
                    }

                }
                return NOptional.of(DefaultNDocNodeSelector.of(
                        names.toArray(new String[0]),
                        types.toArray(new String[0]),
                        classes.toArray(new String[0])
                ));
            }
            default: {
                context.messages().log(NMsg.ofC("[%s] invalid style rule selector %s", NDocUtils.shortName(context.source()), e).asSevere(), context.source());
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", NDocUtils.shortName(context.source()), e));
            }
        }
    }

    public static NOptional<NTxStyleRule[]> parseStyleRule(NElement e, NTxDocumentFactory f, NDocNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<NDocStyleRuleSelector> r = parseStyleRuleSelector(e.asPair().get().key(), f, context);
                if (!r.isPresent()) {
                    return NOptional.ofEmpty(r.getMessage());
                }
                NElement v = e.asPair().get().value();
                switch (v.type()) {
                    case OBJECT:
                    case NAMED_PARAMETRIZED_OBJECT:
                    case PARAMETRIZED_OBJECT:
                    case NAMED_OBJECT:
                    {
                        List<NTxProp> styles = new ArrayList<>();
                        for (NElement el : v.toObject().get().children()) {
                            NOptional<NTxProp[]> s = parseStyle(el, f, context);
                            if (!s.isPresent()) {
                                s = parseStyle(el, f, context);
                                return NOptional.ofEmpty(s.getMessage());
                            }
                            styles.addAll(Arrays.asList(s.get()));
                        }
                        return NOptional.of(
                                new NTxStyleRule[]{
                                        DefaultNTxStyleRule.of(context.node(),context.source(),r.get(), styles.toArray(new NTxProp[0]))
                                }
                        );
                    }
                }
                break;
            }
        }
        context.messages().log(NMsg.ofC("[%s] invalid style rule %s", NDocUtils.shortName(context.source()), e).asSevere(), context.source());
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule %s", NDocUtils.shortName(context.source()), e));
    }


    public static NOptional<NTxProp[]> parseStyle(NElement e, NTxDocumentFactory f, NDocNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NDocValue h = NDocValue.of(e.asPair().get().key());
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = NDocUtils.uid(u.get());
                    return NOptional.of(new NTxProp[]{new NTxProp(uid, e.asPair().get().value(),context.node())});
                }
                break;
            }
            case NAME: {
                NDocValue h = NDocValue.of(e);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = NDocUtils.uid(u.get());
                    return NOptional.of(new NTxProp[]{new NTxProp(uid, NElement.ofBoolean(true),context.node())});
                }
                break;
            }
        }
        //context.messages().addMessage(HMsg.of(NMsg.ofC("[%s] invalid style %s. expected key:value format", context.source(), e),context.source());
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style %s. expected key:value format", NDocUtils.shortName(context.source()), e));
    }

}
