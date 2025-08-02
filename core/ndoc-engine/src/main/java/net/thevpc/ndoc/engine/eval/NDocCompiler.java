package net.thevpc.ndoc.engine.eval;

import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.ndoc.api.extension.NDocFunction;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.eval.*;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.parser.NDocNodeParser;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.ndoc.engine.document.NDocItemBag;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.NDocEngineUtils;
import net.thevpc.ndoc.engine.eval.fct.DefaultNDocFunctionContext;
import net.thevpc.ndoc.engine.log.SilentNDocLogger;
import net.thevpc.ndoc.engine.parser.DefaultNDocNodeFactoryParseContext;
import net.thevpc.ndoc.engine.parser.NDocDocumentLoadingResultImpl;
import net.thevpc.ndoc.engine.parser.NDocNodeDefImpl;
import net.thevpc.ndoc.engine.parser.NDocNodeDefParamImpl;
import net.thevpc.ndoc.engine.document.DefaultNDocNode;
import net.thevpc.ndoc.engine.parser.ctrlnodes.*;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.*;


import java.util.*;

public class NDocCompiler {

    private NDocEngine engine;

    public NDocCompiler(NDocEngine engine) {
        this.engine = engine;
    }

    public NDocDocumentLoadingResult compile(NDocument document0) {
        NDocument documentCopy = document0.copy();
        NDocResource source = documentCopy.root().source();
        SilentNDocLogger slog = new SilentNDocLogger();
        try {
            engine.addLog(slog);
            NDocNode root = documentCopy.root();
            List<NDocItem> all = compileNodeTree(root, new NodeHierarchy(null, false, false, new NDocCompilePageContextImpl(engine, documentCopy)));
//            checkNode(root,null);
            NDocItemBag b = new NDocItemBag(all);
            root = b.compress(false, source);
            processRootPages(root);
            if (documentCopy.root() != root) {
                documentCopy.root().reset();
                documentCopy.root().copyFrom(root);
            }
            return new NDocDocumentLoadingResultImpl(documentCopy, source, slog.getErrorCount() == 0);
        } finally {
            engine.removeLog(slog);
        }
    }

    public boolean processRootPages(NDocNode node) {
        switch (node.type()) {
            case NDocNodeType.PAGE_GROUP: {
                boolean someChanges = false;
//                for (NDocNode child : node.children()) {
//                    NDocUtils.checkNode(child);
//                }
                List<NDocNode> children = new ArrayList<>(node.children());
                List<NDocNode> newChildren = new ArrayList<>();
                List<NDocNode> pending = null;
                for (NDocNode c : children) {
                    if (c == null) {
                        someChanges = true;
                    } else {
                        someChanges |= processRootPages(c);
                        if (Objects.equals(c.type(), NDocNodeType.PAGE_GROUP)
                                || Objects.equals(c.type(), NDocNodeType.PAGE)
                                //assign are retained in the same level!
                                || Objects.equals(c.type(), NDocNodeType.CTRL_ASSIGN)
                        ) {
                            if (pending != null && !pending.isEmpty()) {
                                NDocNode newPage = engine.documentFactory().of(NDocNodeType.PAGE);
                                newPage.setSource(node.source());
                                newChildren.add(newPage);
                                newPage.addChildren(pending.toArray(new NDocNode[0]));
                                pending = null;
                            }
                            newChildren.add(c);
                        } else {
                            someChanges = true;
                            if (pending == null) {
                                pending = new ArrayList<>();
                            }
                            pending.add(c);
                        }
                    }
                }
                if (pending != null && pending.size() > 0) {
                    NDocNode newPage = engine.documentFactory().of(NDocNodeType.PAGE);
                    newPage.setSource(node.source());
                    newPage.addChildren(pending.toArray(new NDocNode[0]));
                    newChildren.add(newPage);
                }
                if (someChanges) {
                    node.children().clear();
                    node.addChildren(newChildren.toArray(new NDocNode[0]));
                }
                return someChanges;
            }
            default: {
                return false;
            }
        }
    }

    public NOptional<NDocNodeDef> findDefinition(NDocItem node, String name) {
        NDocItem currNode = node;
        while (currNode != null) {
            if (currNode instanceof NDocNode) {
                for (NDocNodeDef o : ((NDocNode) currNode).definitions()) {
                    if (NNameFormat.equalsIgnoreFormat(o.name(), name)) {
                        return NOptional.of(o);
                    }
                }
            }
            currNode = currNode.parent();
        }
        return NOptional.ofNamedEmpty("definition for " + name);
    }

    private NDocNodeDef compileNodeDef(NDocNodeDef item, NodeHierarchy h) {
        List<NDocItem> body = new ArrayList<>();
        for (NDocNode b : item.body()) {
            body.addAll(compileNodeTree(b, h.withParent(h.parent).withDef(item).withHierarchy(h.parent)));
        }
        return new NDocNodeDefImpl(
                (NDocNode) item.parent(),
                item.name(),
                Arrays.copyOf(item.params(), item.params().length),
                new NDocItemBag(body).compressToNodes(h.isInPage, item.source()).toArray(new NDocNode[0]),
                item.source()
        );
    }

    private static class NodeHierarchy {
        NDocNode parent;
        NDocNodeDef def;
        NDocNode hierarchy;
        boolean processPages;
        boolean isInPage;
        NDocCompilePageContext context;

        public NodeHierarchy(NDocNode parent, boolean processPages, boolean isInPage, NDocCompilePageContext context) {
            this.parent = parent;
            this.isInPage = isInPage;
            this.processPages = processPages;
            this.context = context;
        }

        public NodeHierarchy(NDocNode parent, NDocNodeDef def, NDocNode hierarchy, boolean processPages, boolean isInPage, NDocCompilePageContext context) {
            this.parent = parent;
            this.def = def;
            this.hierarchy = hierarchy;
            this.isInPage = isInPage;
            this.processPages = processPages;
            this.context = context;
        }
        public NDocument document(){
            return context.document();
        }
        NDocLogger messages(){
            return context.messages();
        }
        NDocEngine engine(){
            return context.engine();
        }

        public NodeHierarchy copy() {
            return new NodeHierarchy(parent, def, hierarchy , processPages, isInPage, context);
        }

        public NodeHierarchy withParent(NDocNode parent) {
            return new NodeHierarchy(parent, def, hierarchy , processPages, isInPage, context);
        }

        public NodeHierarchy withInPage(boolean isInPage) {
            return new NodeHierarchy(parent, def, hierarchy , processPages, isInPage, context);
        }

        public NodeHierarchy withParentOnly(NDocNode parent) {
            return new NodeHierarchy(parent, null, null, processPages, isInPage, context);
        }

        public NodeHierarchy withParentOnly() {
            return new NodeHierarchy(parent, null, null, processPages, isInPage, context);
        }

        public NodeHierarchy withDef(NDocNodeDef def) {
            return new NodeHierarchy(parent, def, hierarchy , processPages, isInPage, context);
        }

        public NodeHierarchy withHierarchy(NDocNode hierarchy) {
            return new NodeHierarchy(parent, def, hierarchy , processPages, isInPage, context);
        }

        public boolean isDef() {
            return def != null;
        }
    }

    private List<NDocItem> compileNodeTree(NDocItem item, NodeHierarchy h0) {
        NodeHierarchy h=h0;
        NAssert.requireNonNull(h, "h");
        if (item instanceof NDocNode) {
            NDocNode node = (NDocNode) item;
            NDocItem tparent = node.parent();
            if (!h.isInPage && NDocNodeType.PAGE.equals(node.type())) {
                h=h.withInPage(true);
            }
            if (!h.processPages && h.isInPage) {
                return Arrays.asList(node);
            }
            node = node.copy();
            node.addHierarchy(h.hierarchy);
            node.setTemplateDefinition(h.def);
            //temporarely set parent, it will be changed later!!
            NDocUtils.setNodeParent(node, h.parent);
            // compile properties
            List<NDocProp> properties = node.getProperties();
            for (NDocProp property : properties) {
                NElement value0 = property.getValue();
                NElement value = engine.evalExpression(value0, node);
                node.setProperty(property.getName(), value);
            }
            // compile definitions
            NDocNodeDef[] defs = node.definitions();
            for (int i = 0; i < defs.length; i++) {
                defs[i] = compileNodeDef(defs[i], h.withParentOnly(node));
            }
            node.clearDefinitions();
            node.addDefinitions(defs);
            NDocUtils.setNodeParent(node, h.parent);
            switch (node.type()) {
                case NDocNodeType.CTRL_IF:
                    return compileNodeTree_if(node, h.withParentOnly());
                case NDocNodeType.CTRL_NAME:
                    return compileNodeTree_name(node, h.withParentOnly());
                case NDocNodeType.CTRL_INCLUDE:
                    return compileNodeTree_include(node, h.withParentOnly());
                case NDocNodeType.CTRL_FOR:
                    return compileNodeTree_for(node, h.withParentOnly());
                case NDocNodeType.CTRL_ASSIGN:
                    return compileNodeTree_assign(node, h.withParentOnly());
                case NDocNodeType.CTRL_EXPR:
                    return compileNodeTree_expr(node, h.withParentOnly());
                case NDocNodeType.CTRL_CALL:
                    return compileNodeTree_call(node, h.withParentOnly());
            }
            return compileNodeTree_default(node, h);
        } else if (item instanceof NDocItemList) {
            List<NDocItem> all = new ArrayList<>();
            for (NDocItem subItem : ((NDocItemList) item).getItems()) {
                all.addAll(compileNodeTree(subItem, h));
            }
            return all;
        } else if (item instanceof NDocProp) {
            return Arrays.asList(item);
        } else if (item instanceof NDocNodeDef) {
            // should i compile
            return Arrays.asList(item);
        } else if (item instanceof NDocStyleRule) {
            return Arrays.asList(item);
        } else {
            throw new IllegalArgumentException("what are you talking about?");
        }
    }

    private List<NDocItem> compileNodeTree_default(NDocNode node, NodeHierarchy h) {
        List<NDocItem> newChildren = new ArrayList<>();
        List<NDocNode> initialChildren = new ArrayList<>(node.children());
        //enforce forward definition dependencies
        node.clearChildren();
        for (NDocNode child : initialChildren) {
            newChildren.addAll(compileNodeTree(child, h.withParent(node)));
            // should update for each child, because second child my depend on
            // some include of the previous child
            node.clearChildren();
            node.addAll(newChildren.toArray(new NDocItem[0]));
        }
        return Arrays.asList(node);
    }

    private List<NDocItem> compileNodeTree_expr(NDocNode node, NodeHierarchy h) {
        NElement varExpr = node.getProperty(NDocPropName.VALUE).get().getValue();
        NElement element = engine.evalExpression(varExpr, node);
        NDocItem h2 = engine.newNode(element, createParseContext(
                varExpr,
                node,
                node.source(),
                h)).get();
        return new ArrayList<>(compileNodeTree(h2, h));
    }

    private List<NDocItem> compileNodeTree_assign(NDocNode node, NodeHierarchy h) {
        NDocItem p = node.parent();
        NDocNode n = (NDocNode) p;
        String varName = node.getProperty(NDocPropName.NAME).get().getValue().asStringValue().get();
        NElement varExpr = node.getProperty(NDocPropName.VALUE).get().getValue();
        boolean ifempty = NDocValue.of(node.getProperty("ifempty").map(x->x.getValue()).orNull()).asBoolean().orElse(false);
        if(ifempty) {
            NElement old = n.getVar(varName).orNull();
            if(old==null || old.isNull()) {
                n.setVar(varName, engine.evalExpression(varExpr, node));
            }
        }else{
            n.setVar(varName, engine.evalExpression(varExpr, node));
        }
        return new ArrayList<>();
    }

    private DefaultNDocNodeFactoryParseContext createParseContext(NElement element, NDocNode node, NDocResource resource, NodeHierarchy compilePageContext) {
        return new DefaultNDocNodeFactoryParseContext(
                compilePageContext.document(),
                element,
                engine,
                NDocUtils.nodePath(node),
                resource
        );
    }

    private List<NDocItem> compileNodeTree_name(NDocNode node, NodeHierarchy h) {
        CtrlNDocNodeName nnode = (CtrlNDocNodeName) node;
        NElement c = nnode.getVarName();
        String name = c.asStringValue().get();
        NOptional<NDocVar> v = engine.findVar(name, h.parent);
        if (v.isPresent()) {
            DefaultNDocNode e = DefaultNDocNode.ofExpr(c, h.parent.source());
            e.setParent(h.parent);
            return compileNodeTree(e, h.withParentOnly());
        }else if(NDocUtils.isAnyDefVarName(name)) {
            return Arrays.asList(node);
        }
        NDocNodeParser p = engine.nodeTypeParser(name).orNull();
        if (p != null) {
            h.messages().log(NMsg.ofC("variable '%s' not found, rendering as plain text.  If you meant a component, use '%s()' syntax",name,name).asWarning(),NDocUtils.sourceOf(node));
        }else {
            h.messages().log(NMsg.ofC("variable '%s' not found, rendering as plain text", name).asWarning(),NDocUtils.sourceOf(node));
        }
        DefaultNDocNode t = DefaultNDocNode.ofText(name);
        t.setSource(node.source());
        return Arrays.asList(t);
    }

    private List<NDocItem> _process_call_node(NDocNodeDef d, CtrlNDocNodeCall c, NodeHierarchy h) {
        String uid = c.getCallName();
        NElement callDeclaration = c.getCallExpr();
        NDocNodeDefParam[] expectedParams = d.params();
        NDocNodeDefParam[] allExpectedParams = Arrays.copyOf(expectedParams, expectedParams.length + 1);
        allExpectedParams[expectedParams.length] = new NDocNodeDefParamImpl(NDocUtils.COMPONENT_BODY_VAR_NAME, NElement.ofArray(c.getCallBody().toArray(new NElement[0])));
        NDocNodeDefParam[] effectiveParams = new NDocNodeDefParam[allExpectedParams.length];
        List<NElement> callArgs = c.getCallArgs();
        List<NDocProp> extraParams = new ArrayList<>();

        if (callArgs.stream().allMatch(x -> x.isNamedPair())) {
            for (NElement e : callArgs) {
                NPairElement p = e.asPair().get();
                String n = p.key().asStringValue().get();
                int pos = NArrays.indexOfByMatcher(allExpectedParams, a -> NNameFormat.equalsIgnoreFormat(a.name(), n));
                NElement newValue = NDocUtils.addCompilerDeclarationPath(p.value(), c.source());
                if (pos >= 0) {
                    effectiveParams[pos] = new NDocNodeDefParamImpl(allExpectedParams[pos].name(), newValue);
                } else {
                    extraParams.add(new NDocProp(n, newValue, c));
                }
            }
            for (Map.Entry<String, NElement> e : c.getBodyVars().entrySet()) {
                String n = e.getKey();
                int pos = NArrays.indexOfByMatcher(allExpectedParams, a -> NNameFormat.equalsIgnoreFormat(a.name(), n));
                if (pos >= 0) {
                    effectiveParams[pos] = new NDocNodeDefParamImpl(allExpectedParams[pos].name(), e.getValue());
                } else {
                    NMsg errMsg = NMsg.ofC("undefined param %s for %s in %s. ignored", n, uid, callDeclaration);
                    engine.log().log(errMsg, c.source());
                }
            }
            for (int i = 0; i < allExpectedParams.length; i++) {
                if (effectiveParams[i] == null) {
                    NElement v = allExpectedParams[i].value();
                    if (v != null) {
                        effectiveParams[i] = allExpectedParams[i];
                    } else {
                        NMsg errMsg = NMsg.ofC("missing param %s for %s in %s", allExpectedParams[i].name(), uid, callDeclaration);
                        engine.log().log(errMsg, c.source());
                        //throw new NIllegalArgumentException(errMsg);
                    }
                }
            }
        } else if (callArgs.stream().noneMatch(x -> x.isNamedPair())) {
            if (expectedParams.length == callArgs.size()) {
                for (int i = 0; i < expectedParams.length; i++) {
                    effectiveParams[i] = new NDocNodeDefParamImpl(allExpectedParams[i].name(), callArgs.get(i));
                }
            }
        } else {
            NMsg errMsg = NMsg.ofC("cannot mix named and non named params in %s", callArgs);
            engine.log().log(errMsg, c.source());
            throw new NIllegalArgumentException(errMsg);
        }

        NDocNode[] body = Arrays.copyOf(d.body(), d.body().length);
        for (int i = 0; i < body.length; i++) {
            body[i] = body[i].copy();
            body[i].setParent(c);
            for (NDocProp extraParam : extraParams) {
                body[i].setProperty(extraParam);
            }
            Map<String, NElement> ee = NDocUtils.inheritedVarsMap(c);
            for (Map.Entry<String, NElement> e : ee.entrySet()) {
                body[i].setVar(e.getKey(), e.getValue());
            }
            for (int j = effectiveParams.length - 1; j >= 0; j--) {
                if (effectiveParams[j] != null) {
                    body[i].setVar(effectiveParams[j].name(), NDocUtils.addCompilerDeclarationPath(effectiveParams[j].value(), NDocUtils.sourceOf(d)));
                }
            }
        }
        List<NDocItem> result = new ArrayList<>();
        for (NDocNode i : body) {
            result.addAll(compileNodeTree(i, h.withDef(d).withHierarchy((NDocNode) d.parent())));
        }
        return result;
    }

    private List<NDocItem> compileNodeTree_call(NDocNode node, NodeHierarchy h) {
        if (NDocNodeType.CTRL_CALL.equals(node.type())) {
            CtrlNDocNodeCall c = (CtrlNDocNodeCall) node;
            if (!h.isInPage || (h.isInPage && h.processPages)) {
                String uid = c.getCallName();
//                NDocNode currNode = NDocUtils.firstNodeUp(node.parent());
                NOptional<NDocNodeDef> dd = findDefinition(h.parent, uid);
                if (dd.isPresent()) {
                    return _process_call_node(dd.get(), c, h);
                } else {
                    NOptional<NDocFunction> t = engine.findFunction(h.parent, uid);
                    if (t.isPresent()) {
                        return _process_call_fct(t.get(), c, h);
                    }
                    h.messages().log(NMsg.ofC("undefined node %s", uid).asError(), c.source());
                    NDocNode defaultNDocNode = DefaultNDocNode.ofText((NMsg.ofC("undefined node %s", uid).toString()));
                    defaultNDocNode.setSource(node.source());
                    return new ArrayList<>(Arrays.asList(defaultNDocNode));
                }
            }
            return new ArrayList<>(Collections.singleton(node));
        }
        throw new NIllegalArgumentException(NMsg.ofC("unexpected node type %s", node.type()));
    }

    private List<NDocItem> compileNodeTree_include(NDocNode node, NodeHierarchy h) {
        if (NDocNodeType.CTRL_INCLUDE.equals(node.type())) {
            CtrlNDocNodeInclude c = (CtrlNDocNodeInclude) node;
            List<NDocItem> loaded = new ArrayList<>();
            for (NElement callArg : c.getCallArgs()) {
                NElement r = engine.evalExpression(NDocUtils.addCompilerDeclarationPath(callArg, NDocUtils.sourceOf(c)), c);
                NPath path = engine.resolvePath(r, c);
                if (path.isDirectory()) {
                    path = path.resolve(NDocEngineUtils.NDOC_EXT_STAR_STAR);
                }
                h.document().resources().add(path);
                List<NPath> list = path.walkGlob().toList();
                list.sort(NDocEngineUtils::comparePaths);
                if (!list.isEmpty()) {
                    for (NPath nPath : list) {
                        if (nPath.isRegularFile()) {
                            NOptional<NDocItem> se = engine.loadNode(c, nPath, h.document());
                            if (se.isPresent()) {
                                NDocItem item = se.get();
                                NDocUtils.setNodeParent(item, h.parent);
                                List<NDocItem> compiled = compileNodeTree(item, h.withParentOnly());
                                loaded.addAll(compiled);
                            } else {
                                h.messages().log(NMsg.ofC("invalid include. error loading : %s", nPath).asSevere(), NDocUtils.sourceOf(c));
                            }
                        } else {
                            h.messages().log(NMsg.ofC("invalid include. error loading : %s", nPath).asWarning(), NDocUtils.sourceOf(c));
                        }
                    }
                } else {
                    h.messages().log(NMsg.ofC("invalid include. error loading : %s", path).asWarning(), NDocUtils.sourceOf(c));
                }
            }
            return loaded;
        }
        throw new NIllegalArgumentException(NMsg.ofC("expected 'include' node, got %s", node.type()));
    }

    private List<NDocItem> compileNodeTree_if(NDocNode node, NodeHierarchy h) {
        if (NDocNodeType.CTRL_IF.equals(node.type())) {
            CtrlNDocNodeIf c = (CtrlNDocNodeIf) node;
            if (!h.isInPage || (h.isInPage && h.processPages)) {
                NElement cond = c.getCond();
                NElement r = engine.evalExpression(cond, c);
                boolean b = NDocUtils.asBoolean(r);
                if (b) {
                    List<NDocNode> tb = c.getTrueBloc();
                    List<NDocItem> tb2 = new ArrayList<>();
                    if (tb != null) {
                        for (NDocNode d : tb) {
                            d = d.copy();
                            d.setParent(h.parent);
                            tb2.addAll(compileNodeTree(d, h.withParentOnly()));
                        }
                    }
                    return tb2;
                } else {
                    List<NDocNode> tb = c.getFalseBloc();
                    List<NDocItem> tb2 = new ArrayList<>();
                    if (tb != null) {
                        for (NDocNode d : tb) {
                            d = d.copy();
                            d.setParent(h.parent);
                            tb2.addAll(compileNodeTree(d, h.withParentOnly()));
                        }
                    }
                    return tb2;
                }
            }
            return new ArrayList<>(Collections.singleton(node));
        }
        throw new NIllegalArgumentException(NMsg.ofC("unexpected node type %s", node.type()));
    }

    private List<NDocItem> compileNodeTree_for(NDocNode node, NodeHierarchy h) {
        if (NDocNodeType.CTRL_FOR.equals(node.type())) {
            CtrlNDocNodeFor c = (CtrlNDocNodeFor) node;
            if (!h.isInPage || (h.isInPage && h.processPages)) {
                NElement varName = c.getVarName();
                NElement varExpr = c.getVarExpr();
                String varNameStr = null;
                if (varName.isName()) {
                    varNameStr = varName.asStringValue().get();
                } else {
                    throw new NIllegalArgumentException(NMsg.ofC("expected varName in for contruct : %s", node));
                }
                NElement anyVal = engine.evalExpression(varExpr, node);
                List<NElement> b = new ArrayList<>();
                if (anyVal.isAnyArray()) {
                    b.addAll(anyVal.asArray().get().children());
                } else {
                    b.add(anyVal);
                }
                List<NDocItem> result = new ArrayList<>();
                for (NElement o : b) {
                    for (NElement e : c.getBody()) {
                        NDocNode nDocNode = (NDocNode) engine.newNode(e, createParseContext(e, node, node.source(), h)).get();
                        nDocNode.setVar(varNameStr, o);
                        NDocUtils.setNodeParent(nDocNode, h.parent);
                        result.addAll(compileNodeTree(nDocNode, h.withParentOnly()));
                    }
                }
                return result;
            }
            return new ArrayList<>(Collections.singleton(node));
        }
        throw new NIllegalArgumentException(NMsg.ofC("unexpected node type %s", node.type()));
    }

    private List<NDocItem> _process_call_fct(NDocFunction t, CtrlNDocNodeCall c, NodeHierarchy h) {
        NDocResource source = NDocUtils.sourceOf(c);
        NDocFunctionContext ctx = new DefaultNDocFunctionContext(engine);
        NElement result = t.invoke(new NDocFunctionArgsImpl(c.getCallArgs(),c,engine), ctx);
        if (result == null) {
            return new ArrayList<>();
        }
        DefaultNDocNodeFactoryParseContext ctx2 = createParseContext(result, c, source, h);
        NOptional<NDocItem> n = engine.newNode(result, ctx2);
        if (n.isPresent()) {
            NDocItem nn = n.get();
            if (nn instanceof NDocNode) {
                return compileNodeTree(nn, h);
            } else if (nn instanceof NDocItemList && ((NDocItemList) nn).getItems().stream().allMatch(x -> x instanceof NDocItem)) {
                List<NDocItem> r = new ArrayList<>();
                for (NDocItem item : ((NDocItemList) nn).getItems()) {
                    r.addAll(compileNodeTree((NDocNode) item, h));
                }
                return r;
            } else {
                engine.newNode(result, ctx2).get();
                NMsg msg = NMsg.ofC("unexpected node type %s", nn.getClass());
                engine.log().log(msg.asError());
                return Arrays.asList(DefaultNDocNode.ofText(msg.toString()));
            }
        } else {
            NMsg msg = NMsg.ofC("not found node for %s : %s", result.type().id(), NDocUtils.snippet(result));
            engine.log().log(msg.asError());
            return Arrays.asList(DefaultNDocNode.ofText(msg.toString()));
        }
    }


    public List<NDocNode> compilePageNode(NDocNode node, NDocCompilePageContext context) {
        NDocNode p = (NDocNode) node.parent();
        NDocUtils.checkNode(node, p);
        ArrayList<NDocNode> y = new ArrayList<>();
        for (NDocItem d : compileNodeTree(node, new NodeHierarchy(p,true, true, context))) {
            y.add((NDocNode) d);
        }
        return y;
    }

}
