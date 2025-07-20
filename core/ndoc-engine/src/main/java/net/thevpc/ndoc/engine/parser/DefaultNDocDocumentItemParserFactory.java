package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.model.node.*;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeCall;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeFor;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeIf;
import net.thevpc.ndoc.engine.parser.nodes.*;
import net.thevpc.ndoc.engine.parser.styles.StylesHITemNamedObjectParser;
import net.thevpc.ndoc.spi.base.model.DefaultNDocNode;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.ndoc.spi.nodes.NDocNodeParserFactory;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.io.NPath;
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
            String uid = NDocUtils.uid(id);
            HITemNamedObjectParser o = allParsers.get(id);
            if (o != null) {
                throw new IllegalArgumentException("clash : " + uid + " is already registered as item parser");
            }
            allParsers.put(uid, s);
        }
    }

    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        if (c.annotations().stream().anyMatch(x -> "define".equals(x.name()))) {
            //this is a node definition
            if (c.isAnyObject() || c.isNamed()) {
                return NCallableSupport.of(1, () -> {
                    NObjectElement object = c.asObject().get();
                    String templateName = object.name().get();
                    List<NDocNodeDefParam> params;
                    if (object.isParametrized()) {
                        params = object.asParametrizedContainer().get().params().get()
                                .stream().map(x -> {
                                    if (x.isNamedPair()) {
                                        NPairElement p = x.asPair().get();
                                        return new NDocNodeDefParamImpl(
                                                p.key().asStringValue().get(),
                                                p.value()
                                        );
                                    } else if (x.isName()) {
                                        return new NDocNodeDefParamImpl(
                                                x.asStringValue().get(),
                                                null
                                        );
                                    } else {
                                        throw new IllegalArgumentException("expected var name : " + x);
                                    }
                                }).collect(Collectors.toList());
                    } else {
                        params = new ArrayList<>();
                    }
                    NDocResource source = context.source();
                    return new NDocNodeDefImpl(
                            context.node(),
                            templateName,
                            params.toArray(new NDocNodeDefParam[0]),
                            object.children().stream().map(x -> engine.newNode(x, context).get()).toArray(NDocNode[]::new),
                            source
                    );
                });
            } else {
                return NCallableSupport.invalid(NMsg.ofC("invalid defineNode syntax, expected @define <NAME>(...){....}"));
            }
        }

        switch (c.type().typeGroup()) {
            case OPERATOR: {
                for (NDocNodeParser ff : engine.nodeTypeFactories()) {
                    NCallableSupport<NDocItem> uu = ff.parseNode(context);
                    if (uu.isValid()) {
                        return uu;
                    }
                }
                break;
            }
            case CONTAINER: {
                switch (c.type()) {
                    case OBJECT:
                    case NAMED_PARAMETRIZED_OBJECT:
                    case PARAMETRIZED_OBJECT:
                    case NAMED_OBJECT: {

                    }

                    case UPLET:
                    case NAMED_UPLET:

                    case ARRAY:
                    case NAMED_PARAMETRIZED_ARRAY:
                    case PARAMETRIZED_ARRAY:
                    case NAMED_ARRAY: {
                        if (c.isNamedUplet() || c.isAnyObject()) {
                            String name = c.asNamed().flatMap(x -> x.name()).orNull();
                            if (name != null) {
                                switch (name) {
                                    case "if": {
                                        return NCallableSupport.of(10, () -> createCtrlNDocNodeIf(c, context));
                                    }
                                    case "for": {
                                        return NCallableSupport.of(10, () -> createCtrlNDocNodeFor(c, context));
                                    }
                                }
                            }
                        }
                        if (c.type() == NElementType.UPLET) {
                            NDocNodeParser p = engine.nodeTypeParser(NDocNodeType.TEXT).orNull();
                            if (p != null) {
                                return p.parseNode(context);
                            }
                            break;
                        }
                        NDocObjEx ee = NDocObjEx.of(c);
                        if (NBlankable.isBlank(ee.name())) {
                            return NCallableSupport.of(10, new Supplier<NDocItem>() {
                                @Override
                                public NDocItem get() {
                                    return parseNoNameBloc(context);
                                }
                            });
                        } else {
                            String uid = NDocUtils.uid(ee.name());
                            NDocNodeParser p = engine.nodeTypeParser(uid).orNull();
                            if (p != null) {
                                return p.parseNode(context);
                            }
                            HITemNamedObjectParser pp = allParsers.get(uid);
                            if (pp != null) {
                                return NCallableSupport.of(10, () -> {
                                    NOptional<NDocItem> styles = pp.parseItem(uid, c, context);
//                            if (!styles.isPresent()) {
//                                pp.parseItem(uid, c, context).get();
//                            }
                                    return styles.get();
                                });
                            }
                        }
                        if (c.isNamedUplet() || c.isAnyObject()) {
                            return NCallableSupport.of(10, () -> createCtrlNDocNodeCall(c, context));
                        }
                        context.messages().log(NDocMsg.of(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), c).asSevere(), context.source()));
                        throw new NIllegalArgumentException(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), c));
                    }
                }
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
            case INT:
            case BIG_DECIMAL:
            case BOOLEAN:
            case LONG:
            case NULL:
            case REGEX:
            case SHORT:
            case LOCAL_TIME:
            case MATRIX:
            case ALIAS: {
                NDocNodeParser p = engine.nodeTypeParser(NDocNodeType.TEXT).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                break;
            }
            case NAME: {
                String name = c.asStringValue().get();
                if (name.startsWith("$")) {
                    return NCallableSupport.of(10,
                            () -> DefaultNDocNode.ofExpr(c, context.source())
                    );
                }
                String uid = NDocUtils.uid(name);
                NDocNodeParser p = engine.nodeTypeParser(uid).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                p = engine.nodeTypeParser(NDocNodeType.TEXT).orNull();
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
                            NOptional<NDocItem> styles = pp.parseItem(nnn, v, context);
                            return styles.get();
                        });
                    }
                }
                for (NDocNodeParser ff : engine.nodeTypeFactories()) {
                    NCallableSupport<NDocItem> uu = ff.parseNode(context);
                    if (uu.isValid()) {
                        return uu;
                    }
                }
                break;
            }
            case OP_EQ: {
                NOperatorElement p = c.asOperator().get();
                NElement k = p.first().get();
                NElement v = p.second().get();
                NDocObjEx kh = NDocObjEx.of(k);
                if (k.isName()) {
                    NOptional<String> nn = kh.asStringOrName();
                    if (nn.isPresent()) {
                        String nnn = NStringUtils.trim(nn.get());
                        if (nnn.startsWith("$")) {
                            nnn = nnn.substring(1);
                        }
                        String finalNnn = nnn;
                        return NCallableSupport.of(10, () -> DefaultNDocNode.ofAssign(finalNnn, v, context.source()));
                    } else {
                        context.messages().log(NDocMsg.of(NMsg.ofC("unable to interpret left signe of assignment as a valid var : %s", k).asError()));
                    }
                } else {
                    context.messages().log(NDocMsg.of(NMsg.ofC("unable to interpret left signe of assignment as a valid var : %s", k).asError()));
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
                    NDocNodeParser p = engine.nodeTypeParser(NDocNodeType.TEXT).orNull();
                    if (p != null) {
                        return p.parseNode(context);
                    }
                    break;
                }
                NDocObjEx ee = NDocObjEx.of(c);
                if (NBlankable.isBlank(ee.name())) {
                    return NCallableSupport.of(10, new Supplier<NDocItem>() {
                        @Override
                        public NDocItem get() {
                            return parseNoNameBloc(context);
                        }
                    });
                } else {
                    String uid = NDocUtils.uid(ee.name());
                    NDocNodeParser p = engine.nodeTypeParser(uid).orNull();
                    if (p != null) {
                        return p.parseNode(context);
                    }
                    HITemNamedObjectParser pp = allParsers.get(uid);
                    if (pp != null) {
                        return NCallableSupport.of(10, () -> {
                            NOptional<NDocItem> styles = pp.parseItem(uid, c, context);
//                            if (!styles.isPresent()) {
//                                pp.parseItem(uid, c, context).get();
//                            }
                            return styles.get();
                        });
                    }
                }
                if (c.isNamedUplet() || c.isAnyObject()) {
                    return NCallableSupport.of(10, () -> createCtrlNDocNodeCall(c, context));
                }
                context.messages().log(NDocMsg.of(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), c).asSevere(), context.source()));
                throw new NIllegalArgumentException(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), c));
            }
        }
        context.messages().log(NDocMsg.of(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), c).asSevere(), context.source()));
        throw new NIllegalArgumentException(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), c));
    }


    private CtrlNDocNodeIf createCtrlNDocNodeIf(NElement c, NDocNodeFactoryParseContext context) {
        NObjectElement obj = c.asObject().get();
        NElement __cond = null;
        List<NDocNode> __trueBloc = new ArrayList<>();
        List<NDocNode> __falseBloc = new ArrayList<>();
        for (NElement child : obj.children()) {
            __trueBloc.add((NDocNode) context.engine().newNode(child, context).get());
        }
        for (NElement e : obj.params().get()) {
            if (e.isNamedPair("$condition")) {
                if (__cond != null) {
                    throw new NIllegalArgumentException(NMsg.ofC("unexpected %s", c));
                }
                __cond = e.asPair().get().value();
            } else if (e.isNamedPair("$else")) {
                NElement ee = e.asPair().get().value();
                if (ee.isAnyObject()) {
                    for (NElement child : ee.asObject().get().children()) {
                        __falseBloc.add((NDocNode) context.engine().newNode(child, context).get());
                    }
                }
            } else {
                if (__cond != null) {
                    throw new NIllegalArgumentException(NMsg.ofC("unexpected %s", c));
                }
                __cond = e;
            }
        }
        CtrlNDocNodeIf cc = new CtrlNDocNodeIf(context.source(), __cond, __trueBloc, __falseBloc);
        cc.setParent(context.node());
        return cc;
    }

    private CtrlNDocNodeFor createCtrlNDocNodeFor(NElement c, NDocNodeFactoryParseContext context) {
        NObjectElement obj = c.asObject().get();
        NElement __varName = null;
        NElement __varDomain = null;
        List<NElement> block = new ArrayList<>();
        for (NElement child : obj.children()) {
            block.add(child);
        }
        for (NElement e : obj.params().get()) {
            if (e.isPair()) {
                __varName = e.asPair().get().key();
                __varDomain = e.asPair().get().value();
            } else {
                throw new NIllegalArgumentException(NMsg.ofC("unexpected %s", c));
            }
        }
        CtrlNDocNodeFor cc = new CtrlNDocNodeFor(context.source(), __varName, __varDomain, block);
        cc.setParent(context.node());
        return cc;
    }

    private CtrlNDocNodeCall createCtrlNDocNodeCall(NElement c, NDocNodeFactoryParseContext context) {
        CtrlNDocNodeCall cc = new CtrlNDocNodeCall(context.source());
        NDocResource source = context.source();
        String __name = c.asNamed().get().name().get();
        cc.setProperty(NDocPropName.NAME, NDocUtils.addCompilerDeclarationPath(NElement.ofString(NDocUtils.uid(__name)),context.source()));
        List<NElement> __callBody = new ArrayList<>();
        List<NElement> __args = new ArrayList<>();
        Map<String, NElement> __bodyVars = new HashMap<>();

        //inline current file path in the TsonElements
        if (source != null && source.path().orNull() != null) {
            NPath sourcePath = source.path().orNull();
            c = NDocUtils.addCompilerDeclarationPath(c, sourcePath.toString());
            if (c.isNamedUplet()) {
                NUpletElementBuilder fb = (NUpletElementBuilder) c.builder();
                for (int i = 0; i < fb.params().size(); i++) {
                    NElement u = NDocUtils.addCompilerDeclarationPath(fb.get(i).orNull(), sourcePath.toString());
                    fb.set(i, u);
                    __args.add(u);
                }
                c = fb.build();
            } else if (c.isAnyObject()) {
                NObjectElementBuilder fb = (NObjectElementBuilder) c.builder();
                List<NElement> args = fb.params().orNull();
                if (args != null) {
                    for (int i = 0; i < args.size(); i++) {
                        NElement u = NDocUtils.addCompilerDeclarationPath(args.get(i), sourcePath.toString());
                        fb.setParamAt(i, u);
                        __args.add(u);
                    }
                }
                for (NElement child : fb.children()) {
                    if (child.isNamedPair() && child.asPair().get().key().isAnnotated("let")) {
                        NPairElement pe = child.asPair().get();
                        __bodyVars.put(pe.key().asStringValue().get(), pe.value());
                    } else {
                        __callBody.add(child);
                    }
                }
                c = fb.build();
            } else {
                throw new IllegalArgumentException("unexpected call : " + c);
            }
        } else {
            if (c.isNamedUplet()) {
                NUpletElementBuilder fb = (NUpletElementBuilder) c.builder();
                for (int i = 0; i < fb.params().size(); i++) {
                    __args.add(fb.get(i).orNull());
                }
            } else if (c.isAnyObject()) {
                NObjectElementBuilder fb = (NObjectElementBuilder) c.builder();
                List<NElement> args = fb.params().orNull();
                if (args != null) {
                    __args.addAll(args);
                }
                for (NElement child : fb.children()) {
                    if (child.isNamedPair() && child.asPair().get().key().isAnnotated("let")) {
                        NPairElement pe = child.asPair().get();
                        __bodyVars.put(pe.key().asStringValue().get(), pe.value());
                    } else {
                        __callBody.add(child);
                    }
                }
            } else {
                throw new IllegalArgumentException("unexpected call : " + c);
            }

        }
        cc.setCallName(__name);
        cc.setCallBody(__callBody);
        cc.setArgs(__args);
        cc.setCallExpr(c);
        cc.setBodyVars(__bodyVars);
        cc.setProperty(NDocPropName.VALUE, c);
        cc.setSource(context.source());
        cc.setParent(context.node());
        return cc;
    }

    private boolean isRootBloc(NDocNodeFactoryParseContext context) {
        NDocNode[] nodes = context.nodePath();
        if (nodes.length == 0) {
            return true;
        }
        if (nodes.length > 1) {
            return false;
        }
        if (nodes.length == 1) {
            if (!Objects.equals(nodes[0].type(), NDocNodeType.PAGE_GROUP)) {
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
            boolean foundNDoc = false;
            boolean foundVersion = false;
            boolean foundOther = false;
            List<NElement> params = a.params();
            if (params != null) {
                for (NElement cls : params) {
                    if (cls.isAnyString()) {
                        if (cls.asStringValue().get().equalsIgnoreCase("ndoc")) {
                            foundNDoc = true;
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
            if (foundNDoc) {
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

    private NDocItem parseNoNameBloc(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = engine.documentFactory();
        HashSet<String> allAncestors = null;
        HashSet<String> allStyles = null;
        NDocObjEx ee = NDocObjEx.of(c);
        for (NElementAnnotation a : c.annotations()) {
            String nn = a.name();
            if (!NNameFormat.equalsIgnoreFormat(nn,"CompilerDeclarationPath")) {
                if(!NBlankable.isBlank(nn)) {
                    if (allAncestors == null) {
                        allAncestors = new HashSet<>();
                    }
                    allAncestors.add(NDocUtils.uid(nn));
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
        }
        NDocNode node = context.node();
        if ((allStyles != null || allAncestors != null) && !isRootBloc(context)) {
            NDocNode pg = f.ofStack().setSource(context.source());
            pg.setStyleClasses(allStyles == null ? null : allStyles.toArray(new String[0]));
            for (NElement child : ee.body()) {
                NOptional<NDocItem> u = context.engine().newNode(child, context);
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
            NDocItemList pg = new NDocItemList();
            for (NElement child : ee.body()) {
                NOptional<NDocItem> u = context.engine().newNode(child, context);
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
