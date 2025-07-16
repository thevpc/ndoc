package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.node.*;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.engine.control.CtrlHNodeCall;
import net.thevpc.ndoc.engine.parser.nodes.*;
import net.thevpc.ndoc.engine.parser.styles.StylesHITemNamedObjectParser;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.ndoc.spi.nodes.NDocNodeParserFactory;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.*;


import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class DefaultNDocDocumentItemParserFactory
        implements NDocNodeParserFactory {
    static Map<String, HITemNamedObjectParser> allParsers = new HashMap<>();

    static {
        register(new ImportHITemNamedObjectParser());
//        register(new DefineHITemNamedObjectParser());
        register(new StylesHITemNamedObjectParser());
    }

    private static void register(HITemNamedObjectParser s) {
        for (String id : s.ids()) {
            String uid = HUtils.uid(id);
            HITemNamedObjectParser o = allParsers.get(id);
            if (o != null) {
                throw new IllegalArgumentException("clash : " + uid + " is already registered as item parser");
            }
            allParsers.put(uid, s);
        }
    }

    @Override
    public NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        if (c.annotations().stream().anyMatch(x -> "define".equals(x.name()))) {
            //this is a node definition
            if (c.isAnyObject() || c.isNamed()) {
                return NCallableSupport.of(1, () -> {
                    NObjectElement object = c.asObject().get();
                    String templateName = object.name().get();
                    List<HNodeDefParam> params;
                    if (object.isParametrized()) {
                        params = object.asParametrizedContainer().get().params().get()
                                .stream().map(x -> {
                                    if(x.isNamedPair()) {
                                        NPairElement p = x.asPair().get();
                                        return new HNodeDefParamImpl(
                                                p.key().asStringValue().get(),
                                                p.value()
                                        );
                                    }else if(x.isName()){
                                        return new HNodeDefParamImpl(
                                                x.asStringValue().get(),
                                                null
                                        );
                                    }else{
                                        throw new IllegalArgumentException("expected var name : "+x);
                                    }
                                }).collect(Collectors.toList());
                    } else {
                        params = new ArrayList<>();
                    }
                    HResource source = context.source();
                    return new HNodeDefImpl(templateName,
                            params.toArray(new HNodeDefParam[0]),
                            object.children().stream().map(x -> engine.newNode(x, context).get()).toArray(HNode[]::new),
                            source
                    );
                });
            } else {
                return NCallableSupport.invalid(NMsg.ofC("invalid defineNode syntax, expected @define <NAME>(...){....}"));
            }
        }
        switch (c.type().typeGroup()) {
            case OPERATOR:{
                for (NDocNodeParser ff : engine.nodeTypeFactories()) {
                    NCallableSupport<HItem> uu = ff.parseNode(context);
                    if (uu.isValid()) {
                        return uu;
                    }
                }
                break;
            }
        }
        switch (c.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            case BIG_COMPLEX:
            case BIG_INT:
            case BYTE:
            case CHAR:
            case LOCAL_DATE:
            case LOCAL_DATETIME:
            case DOUBLE:
            case DOUBLE_COMPLEX:
            case FLOAT:
            case FLOAT_COMPLEX:
            case INTEGER:
            case BIG_DECIMAL:
            case BOOLEAN:
            case LONG:
            case NULL:
            case REGEX:
            case SHORT:
            case LOCAL_TIME:
            case MATRIX:
            case ALIAS: {
                NDocNodeParser p = engine.nodeTypeFactory(HNodeType.TEXT).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                break;
            }
            case NAME: {
                String name = c.asStringValue().get();
                String uid = HUtils.uid(name);
                NDocNodeParser p = engine.nodeTypeFactory(uid).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                p = engine.nodeTypeFactory(HNodeType.TEXT).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                break;
            }
            case PAIR: {
                NPairElement p = c.asPair().get();
                NElement k = p.key();
                NElement v = p.value();
                NDocObjEx kh = NDocObjEx.of(k);
                NOptional<String> nn = kh.asStringOrName();
                if (nn.isPresent()) {
                    String nnn = NStringUtils.trim(nn.get());
                    HITemNamedObjectParser pp = allParsers.get(nnn);
                    if (pp != null) {
                        return NCallableSupport.of(10, () -> {
                            NOptional<HItem> styles = pp.parseItem(nnn, v, context);
                            return styles.get();
                        });
                    }
                }
                for (NDocNodeParser ff : engine.nodeTypeFactories()) {
                    NCallableSupport<HItem> uu = ff.parseNode(context);
                    if (uu.isValid()) {
                        return uu;
                    }
                }
                break;
            }
            case OBJECT:
            case NAMED_PARAMETRIZED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_OBJECT:

            case UPLET:
            case NAMED_UPLET:

            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY: {
                if (c.type() == NElementType.UPLET) {
                    NDocNodeParser p = engine.nodeTypeFactory(HNodeType.TEXT).orNull();
                    if (p != null) {
                        return p.parseNode(context);
                    }
                    break;
                }
                NDocObjEx ee = NDocObjEx.of(c);
                if (NBlankable.isBlank(ee.name())) {
                    return NCallableSupport.of(10, new Supplier<HItem>() {
                        @Override
                        public HItem get() {
                            return parseNoNameBloc(context);
                        }
                    });
                } else {
                    String uid = HUtils.uid(ee.name());
                    NDocNodeParser p = engine.nodeTypeFactory(uid).orNull();
                    if (p != null) {
                        return p.parseNode(context);
                    }
                    HITemNamedObjectParser pp = allParsers.get(uid);
                    if (pp != null) {
                        return NCallableSupport.of(10, () -> {
                            NOptional<HItem> styles = pp.parseItem(uid, c, context);
//                            if (!styles.isPresent()) {
//                                pp.parseItem(uid, c, context).get();
//                            }
                            return styles.get();
                        });
                    }
                }
                if (c.isNamedUplet() || c.isAnyObject()) {
                    return NCallableSupport.of(10, () -> new CtrlHNodeCall(c, context.source()));
                }
                context.messages().log(HMsg.of(NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c).asSevere(), context.source()));
                throw new NIllegalArgumentException(NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c));
            }
        }
        context.messages().log(HMsg.of(NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c).asSevere(), context.source()));
        throw new NIllegalArgumentException(NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c));
    }


    private boolean isRootBloc(NDocNodeFactoryParseContext context) {
        HNode[] nodes = context.nodePath();
        if (nodes.length == 0) {
            return true;
        }
        if (nodes.length > 1) {
            return false;
        }
        if (nodes.length == 1) {
            if (!Objects.equals(nodes[0].type(), HNodeType.PAGE_GROUP)) {
                return false;
            }
        }
        NElement c = context.element();
//        HEngine engine = context.engine();
        for (NElementAnnotation a : c.annotations()) {
            String nn = a.name();
            if (!NBlankable.isBlank(nn)) {
                return false;
            }
            boolean foundHalfa = false;
            boolean foundVersion = false;
            boolean foundOther = false;
            List<NElement> params = a.params();
            if (params != null) {
                for (NElement cls : params) {
                    if (cls.isAnyString()) {
                        if (cls.asStringValue().get().equalsIgnoreCase("ndoc")) {
                            foundHalfa = true;
                        } else if (isVersionString(cls.asStringValue().get())) {
                            foundVersion = true;
                        } else {
                            foundOther = true;
                        }
                    } else {
                        if (cls.type().isAnyNumber()) {
                            BigDecimal bi = cls.asNumber().get().bigDecimalValue();
                            foundVersion = true;
                        } else {
                            foundOther = true;
                        }
                        break;

                    }
                }
            }
            if (foundHalfa) {
                return true;
            }
        }
        return false;
    }

    private boolean isVersionString(String value) {
        if (value != null) {
            value = value.trim();
            if (value.length() > 0) {
                if (value.charAt(0) >= '0' && value.charAt(0) >= '9') {
                    for (char c : value.toCharArray()) {
                        if (!Character.isAlphabetic(c) && !Character.isDigit(c)
                                && c != '.' && c != '_' && c != '-'

                        ) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private HItem parseNoNameBloc(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = engine.documentFactory();
        HashSet<String> allAncestors = null;
        HashSet<String> allStyles = null;
        NDocObjEx ee = NDocObjEx.of(c);
        for (NElementAnnotation a : c.annotations()) {
            String nn = a.name();
            if (!NBlankable.isBlank(nn)) {
                if (allAncestors == null) {
                    allAncestors = new HashSet<>();
                }
                allAncestors.add(HUtils.uid(nn));
            }
            // add classes as well

            List<NElement> params = a.params();
            if (params != null) {
                for (NElement cls : params) {
                    if (allStyles == null) {
                        allStyles = new HashSet<>();
                    }
                    NOptional<String[]> ss = NDocObjEx.of(cls).asStringArrayOrString();
                    if (ss.isPresent()) {
                        allStyles.addAll(Arrays.asList(ss.get()));
                    }
                }
            }
        }
        HNode node = context.node();
        if ((allStyles != null || allAncestors != null) && !isRootBloc(context)) {
            HNode pg = f.ofStack();
            pg.setAncestors(allAncestors == null ? null : allAncestors.toArray(new String[0]));
            pg.setStyleClasses(allStyles == null ? null : allStyles.toArray(new String[0]));
            for (NElement child : ee.body()) {
                NOptional<HItem> u = context.engine().newNode(child, context);
                if (u.isPresent()) {
                    pg.append(u.get());
                } else {
                    throw new IllegalArgumentException(NMsg.ofC("invalid %s for %s", child,
                            node == null ? "document" : node.type()
                    ).toString());
                }
            }
            return pg;
        } else {
            HItemList pg = new HItemList();
            for (NElement child : ee.body()) {
                NOptional<HItem> u = context.engine().newNode(child, context);
                if (u.isPresent()) {
                    pg.add(u.get());
                } else {
                    throw new IllegalArgumentException(NMsg.ofC("invalid %s for %s : %s", child,
                            node == null ? "document" : node.type(),
                            u.getMessage().get()
                    ).toString());
                }
            }
            return pg;
        }
    }

}
