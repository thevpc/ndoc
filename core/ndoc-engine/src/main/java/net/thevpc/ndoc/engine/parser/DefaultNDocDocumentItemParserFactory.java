package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.parser.NDocNodeParserFactory;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.parser.ctrlnodes.CtrlNDocNodeCall;
import net.thevpc.ndoc.api.base.model.DefaultNDocNode;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.parser.NDocNodeParser;
import net.thevpc.ndoc.engine.parser.ctrlnodes.CtrlNDocNodeName;
import net.thevpc.ndoc.engine.document.NDocItemBag;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.*;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class DefaultNDocDocumentItemParserFactory
        implements NDocNodeParserFactory {

    public DefaultNDocDocumentItemParserFactory() {
    }

    @Override
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        if (c.annotations().stream().anyMatch(x -> NDocNodeType.CTRL_DEFINE.equals(x.name()))) {
            //this is a node definition
            if (c.isAnyObject() || c.isNamed()) {
                return NCallableSupport.valid(() -> {
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
                                        context.messages().log(NMsg.ofC("invalid definition param, expected var name %s in %s", x, object).asError());
                                        return null;
                                    }
                                }).filter(x -> x != null).collect(Collectors.toList());
                    } else {
                        params = new ArrayList<>();
                    }
                    NDocResource source = context.source();
                    List<NDocNode> defBody=new ArrayList<>();
                    for (NElement child : object.children()) {
                        NDocItem item = engine.newNode(child, context).get();
                        NDocItemBag b=new NDocItemBag(Arrays.asList(item));
                        if(b.isNodes()){
                            defBody.addAll(b.nodes());
                        }else{
                            context.messages().log(NMsg.ofC("expected nodes, but got other items when creating node from %s",NDocUtils.snippet(child)).asError());
                        }
                    }
                    return new NDocNodeDefImpl(
                            context.node(),
                            templateName,
                            params.toArray(new NDocNodeDefParam[0]),
                            defBody.toArray(new NDocNode[0]),
                            source
                    );
                });
            } else {
                return _invalidSupport(NMsg.ofC("invalid defineNode syntax, expected @define <NAME>(...){....}"), context);
            }
        }

        switch (c.type().typeGroup()) {
            case OPERATOR: {
                switch (c.type()) {
                    case OP_EQ: {
                        NOperatorElement p = c.asOperator().get();
                        NElement k = p.first().get();
                        NElement v = p.second().get();
                        NDocObjEx kh = NDocObjEx.of(k);
                        if (k.isName()) {
                            NOptional<String> nn = kh.asStringOrName();
                            if (nn.isPresent()) {
                                String nnn = NStringUtils.trim(nn.get());
                                return NCallableSupport.valid(() -> DefaultNDocNode.ofAssign(nnn, v, context.source()));
                            } else {
                                return _invalidSupport(NMsg.ofC("unable to interpret left hand of assignment as a valid var : %s", k), context);
                            }
                        } else {
                            return _invalidSupport(NMsg.ofC("unable to interpret left hand of assignment as a valid var : %s", k), context);
                        }
                    }
                }
                NOptional<NDocNodeParser> ff = engine.nodeTypeParser(c.type().opSymbol());
                if (ff.isPresent()) {
                    NCallableSupport<NDocItem> uu = ff.get().parseNode(context);
                    if (uu.isValid()) {
                        return uu;
                    }
                }
                break;
            }
            case CONTAINER: {
                switch (c.type()) {
                    case OBJECT:
                    case ARRAY:
                    {
                        return parseNoNameBloc(context);
                    }
                    case UPLET: {
                        NDocNodeParser p = engine.nodeTypeParser(NDocNodeType.TEXT).orNull();
                        return p.parseNode(context);
                    }
                    case NAMED_PARAMETRIZED_OBJECT:
                    case NAMED_OBJECT:
                    case NAMED_UPLET:
                    case NAMED_PARAMETRIZED_ARRAY:
                    case NAMED_ARRAY: {
                        NDocObjEx ee = NDocObjEx.of(c);
                        String uid = NDocUtils.uid(ee.name());
                        NDocNodeParser p = engine.nodeTypeParser(uid).orNull();
                        if (p != null) {
                            return p.parseNode(context);
                        }
                        if (c.isNamedUplet() || c.isAnyObject()) {
                            return NCallableSupport.valid(() -> createCtrlNDocNodeCall(c, context));
                        }
                        return _invalidSupport(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(c)), context);
                    }
                    case PAIR: {
                        if (c.isNamedPair()) {
                            NPairElement p = c.asPair().get();
                            String name = p.key().asStringValue().get();
                            NOptional<NDocNodeParser> ff = engine.nodeTypeParser(name);
                            if (ff.isPresent()) {
                                NCallableSupport<NDocItem> uu = ff.get().parseNode(context);
                                if (uu.isValid()) {
                                    return uu;
                                }
                            }
                        }
                        return _invalidSupport(NMsg.ofC("[%s] unable to resolve node from pair : %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(c)), context);
                    }
                    default: {
                        return _invalidSupport(NMsg.ofC("[%s] unable to resolve node from %s : %s", c.type().id(), NDocUtils.shortName(context.source()), NDocUtils.snippet(c)), context);
                    }
                }
            }
            case NUMBER:
            case TEMPORAL:
            case STRING:
            case REGEX:
            case NULL:
            case BOOLEAN: {
                NDocNodeParser p = engine.nodeTypeParser(NDocNodeType.TEXT).orNull();
                if (p != null) {
                    return p.parseNode(context);
                }
                break;
            }
            case NAME: {
                return NCallableSupport.valid(()->new CtrlNDocNodeName(context.source(),c));
            }
        }
        return _invalidSupport(NMsg.ofC("[%s] unable to resolve node : %s", NDocUtils.shortName(context.source()), NDocUtils.snippet(c)), context);
    }

    private NCallableSupport<NDocItem> _invalidSupport(NMsg msg, NDocNodeFactoryParseContext context) {
        msg = msg.asError();
        context.messages().log(msg.asError());
        return NCallableSupport.invalid(msg);
    }


    private CtrlNDocNodeCall createCtrlNDocNodeCall(NElement c, NDocNodeFactoryParseContext context) {
        CtrlNDocNodeCall cc = new CtrlNDocNodeCall(context.source());
        NDocResource source = context.source();
        String __name = c.asNamed().get().name().get();
        cc.setProperty(NDocPropName.NAME, NDocUtils.addCompilerDeclarationPath(NElement.ofString(NDocUtils.uid(__name)), context.source()));
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
                context.messages().log(NMsg.ofC("unexpected call : %s (ignored)", c).asError(), context.source());
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
                context.messages().log(NMsg.ofC("unexpected call : %s (ignored)", c).asError(), context.source());
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

    private NCallableSupport<NDocItem> parseNoNameBloc(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = engine.documentFactory();
        HashSet<String> allAncestors = null;
        HashSet<String> allStyles = null;
        NDocObjEx ee = NDocObjEx.of(c);
        for (NElementAnnotation a : c.annotations()) {
            String nn = a.name();
            if (!NNameFormat.equalsIgnoreFormat(nn, NDocUtils.COMPILER_DECLARATION_PATH)) {
                if (!NBlankable.isBlank(nn)) {
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
            NDocNode pg = f.ofGroup().setSource(context.source());
            pg.setStyleClasses(allStyles == null ? null : allStyles.toArray(new String[0]));
            for (NElement child : ee.body()) {
                NOptional<NDocItem> u = context.engine().newNode(child, context);
                if (u.isPresent()) {
                    pg.append(u.get());
                } else {
                    return _invalidSupport(NMsg.ofC("invalid %s for %s", NDocUtils.snippet(child),
                            node == null ? "document" : node.type()
                    ), context);
                }
            }
            return NCallableSupport.valid(pg);
        } else {
            NDocItemList pg = new NDocItemList();
            for (NElement child : ee.body()) {
                NOptional<NDocItem> u = context.engine().newNode(child, context);
                if (u.isPresent()) {
                    pg.add(u.get());
                } else {
                    return _invalidSupport(NMsg.ofC("invalid %s for %s : %s", NDocUtils.snippet(child),
                            node == null ? "document" : node.type(),
                            u.getMessage().get()
                    ), context);
                }
            }
            return NCallableSupport.valid(pg);
        }
    }

}
