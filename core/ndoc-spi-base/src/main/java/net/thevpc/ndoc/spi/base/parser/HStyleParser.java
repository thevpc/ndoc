package net.thevpc.ndoc.spi.base.parser;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.style.*;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.util.*;

import net.thevpc.nuts.elem.NElement;

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


    public static NOptional<HStyleRuleSelector> parseStyleRuleSelector(NElement e, NDocDocumentFactory f, NDocNodeFactoryParseContext context) {
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
                    return NOptional.of(DefaultHNodeSelector.ofAny());
                }
                if (s.startsWith(".")) {
                    return NOptional.of(DefaultHNodeSelector.ofClasses(s.substring(1)));
                }
                return NOptional.of(DefaultHNodeSelector.ofName(s));
            }
            case NAME: {
                String n = e.asStringValue().get();
                if (n.startsWith(".")) {
                    return NOptional.of(DefaultHNodeSelector.ofClasses(n.substring(1)));
                }
                return NOptional.of(DefaultHNodeSelector.ofType(n));
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
                            NDocObjEx h = NDocObjEx.of(e.asPair().get().key());
                            NOptional<String> k = h.asStringOrName();
                            if (k.isPresent()) {
                                switch (net.thevpc.ndoc.api.util.HUtils.uid(k.get())) {
                                    case "class": {
                                        NDocObjEx h2 = NDocObjEx.of(e.asPair().get().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            classes.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e).asSevere(), context.source()));
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e));
                                        }
                                    }
                                    case "name": {
                                        NDocObjEx h2 = NDocObjEx.of(e.asPair().get().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            names.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e).asSevere(), context.source()));
                                            return NOptional.ofEmpty(
                                                    NMsg.ofC("[%s] invalid style rule selector %s. expected a string or a string array.", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e)
                                            );
                                        }
                                    }
                                    case "type": {
                                        NDocObjEx h2 = NDocObjEx.of(e.asPair().get().value());
                                        NOptional<String[]> cc = h2.asStringArrayOrString();
                                        if (cc.isPresent()) {
                                            types.addAll(Arrays.asList(cc.get()));
                                        } else {
                                            context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e).asSevere(), context.source()));
                                            return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected a valid node type or array", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e));
                                        }
                                    }
                                    default: {
                                        context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e).asSevere(), context.source()));
                                        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e));
                                    }
                                }
                            } else {
                                context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e).asSevere(), context.source()));
                                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s. expected one of 'name', 'class' or 'type'", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e));
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
                            context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule selector %s", context.source(), e).asSevere(), context.source()));
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
                context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule selector %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e).asSevere(), context.source()));
                return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule selector %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e));
            }
        }
    }

    public static NOptional<HStyleRule[]> parseStyleRule(NElement e, NDocDocumentFactory f, NDocNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<HStyleRuleSelector> r = parseStyleRuleSelector(e.asPair().get().key(), f, context);
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
                        List<HProp> styles = new ArrayList<>();
                        for (NElement el : v.toObject().get().children()) {
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
        context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e).asSevere(), context.source()));
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style rule %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e));
    }


    public static NOptional<HProp[]> parseStyle(NElement e, NDocDocumentFactory f, NDocNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NDocObjEx h = NDocObjEx.of(e.asPair().get().key());
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = net.thevpc.ndoc.api.util.HUtils.uid(u.get());
                    return NOptional.of(new HProp[]{new HProp(uid, e.asPair().get().value())});
                }
                break;
            }
            case NAME: {
                NDocObjEx h = NDocObjEx.of(e);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HUtils.uid(u.get());
                    return NOptional.of(new HProp[]{new HProp(uid, NElements.of().ofBoolean(true))});
                }
                break;
            }
        }
        //context.messages().addMessage(HMsg.of(NMsg.ofC("[%s] invalid style %s. expected key:value format", context.source(), e),context.source());
        return NOptional.ofEmpty(NMsg.ofC("[%s] invalid style %s. expected key:value format", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), e));
    }

}
