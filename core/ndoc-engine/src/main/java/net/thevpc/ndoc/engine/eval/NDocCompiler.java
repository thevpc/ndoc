package net.thevpc.ndoc.engine.eval;

import net.thevpc.ndoc.api.CompilePageContext;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.ndoc.api.model.node.*;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.NDocProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.MyCompilePageContext;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeCall;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeFor;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeIf;
import net.thevpc.ndoc.engine.eval.fct.DefaultNDocFunctionContext;
import net.thevpc.ndoc.engine.parser.DefaultNDocNodeFactoryParseContext;
import net.thevpc.ndoc.engine.parser.NDocDocumentLoadingResultImpl;
import net.thevpc.ndoc.engine.parser.NDocNodeDefParamImpl;
import net.thevpc.ndoc.spi.base.model.DefaultNDocNode;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.*;


import java.util.*;

public class NDocCompiler {

    private NDocEngine engine;

    public NDocCompiler(NDocEngine engine) {
        this.engine = engine;
    }

    public NDocDocumentLoadingResult compile(NDocument document) {
        document = document.copy();
        NDocResource source = engine.computeSource(document.root());
        NDocDocumentLoadingResultImpl result = new NDocDocumentLoadingResultImpl(source, engine.messages());
        NDocNode root = document.root();
        processUuid(root);
//        root = processCalls(root, result);
//        root = processControlFlow(root, result, new MyNDocNodeFlowControlProcessorContext(document, messages));
        List<NDocNode> all = compileNodeTree(root, false, false, new MyCompilePageContext(engine, document));
        if (all.size() == 0) {
            root = new DefaultNDocNode(NDocNodeType.VOID, source);
        } else if (all.size() == 1) {
            root = all.get(0);
        } else {
            root = new DefaultNDocNode(NDocNodeType.PAGE_GROUP, source);
            root.setChildren(all.toArray(new NDocNode[0]));
        }
//        root = removeDeclarations(root, result);
        processRootPages(root);
        root = root.copy();
        document.root().reset();
        document.root().copyFrom(root);
        result.setDocument(document);
        return result;
    }

    public boolean processRootPages(NDocNode node) {
        switch (node.type()) {
            case NDocNodeType.PAGE_GROUP: {
                if (NBlankable.isBlank(node.getUuid())) {
                    node.setUuid(UUID.randomUUID().toString());
                }
                boolean someChanges = false;
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
                                || Objects.equals(c.type(), NDocNodeType.ASSIGN)
                        ) {
                            if (pending != null && !pending.isEmpty()) {
                                NDocNode newPage = engine.documentFactory().of(NDocNodeType.PAGE);
                                newChildren.add(newPage);
                                newPage.addAll(pending.toArray(new NDocNode[0]));
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
                    newPage.addAll(pending.toArray(new NDocNode[0]));
                    newChildren.add(newPage);
                }
                if (someChanges) {
                    node.children().clear();
                    node.addAll(newChildren.toArray(new NDocNode[0]));
                }
                return someChanges;
            }
            default: {
                return false;
            }
        }
    }

    protected void processUuid(NDocNode node) {
        if (NBlankable.isBlank(node.getUuid())) {
            node.setUuid(UUID.randomUUID().toString());
        }
        for (NDocNode child : node.children()) {
            processUuid(child);
        }
    }

//    protected NDocNode processControlFlow(NDocNode root, NDocDocumentLoadingResultImpl result, NDocNodeFlowControlProcessorContext context) {
//        NDocResource source = engine.computeSource(root);
//        NDocNode oldRoot = root;
//        NDocNode[] multiRoot = processControlFlowCurrent(new NDocNode[]{root}, result, context);
//        if (multiRoot.length == 0) {
//            root = new DefaultNDocNode(NDocNodeType.PAGE, source);
//            root.setSource(oldRoot.source());
//        } else {
//            root = new DefaultNDocNode(NDocNodeType.PAGE_GROUP, source);
//            root.setSource(oldRoot.source());
//            root.setChildren(multiRoot);
//        }
//        return root;
//    }

//    protected NDocNode[] processControlFlowCurrent(NDocNode[] nodes, NDocDocumentLoadingResultImpl result, NDocNodeFlowControlProcessorContext context) {
//        List<NDocNode> curr = new ArrayList<>(Arrays.asList(nodes));
//        for (NDocNodeFlowControlProcessor a : flowControlProcessorFactory.list()) {
//            List<NDocNode> result2 = new ArrayList<>();
//            for (int i = 0; i < curr.size(); i++) {
//                NDocNode cc = curr.get(i);
//                if (cc != null) {
//                    NDocNode[] p = a.process(cc, context);
//                    if (p == null) {
//                        result2.add(cc);
//                    } else {
//                        result2.addAll(Arrays.stream(p).filter(Objects::nonNull).collect(Collectors.toList()));
//                    }
//                }
//            }
//            curr = result2;
//        }
//        return curr.toArray(new NDocNode[0]);
//    }

//    protected NDocNode removeDeclarations(NDocNode node, NDocDocumentLoadingResultImpl result) {
//        List<NDocNode> children = node.children();
//        for (int i = children.size() - 1; i >= 0; i--) {
//            NDocNode child = children.get(i);
//            if (child.isTemplate() || NDocNodeType.DEFINE.equals(child.type())) {
//                children.remove(i);
//            } else {
//                removeDeclarations(child, result);
//            }
//        }
//        return node;
//    }


    public NOptional<NDocNodeDef> findDefinition(NDocItem node, String name) {
        NDocItem currNode = node;
        while (currNode != null) {
            if (currNode instanceof NDocNode) {
                for (NDocNodeDef o : ((NDocNode) currNode).nodeDefinitions()) {
                    if (NNameFormat.equalsIgnoreFormat(o.name(), name)) {
                        return NOptional.of(o);
                    }
                }
            }
            currNode = currNode.parent();
        }
        return NOptional.ofNamedEmpty("definition for " + name);
    }

    protected List<NDocNode> compileNodeTree(NDocNode node, boolean processPages, boolean isInPage, CompilePageContext compilePageContext) {
        if (!isInPage && NDocNodeType.PAGE.equals(node.type())) {
            isInPage = true;
        }
        if (!processPages && isInPage) {
            return Arrays.asList(node);
        }
        NDocNode[] nDocNodes0 = new NDocNode[]{node};
        if (NDocNodeType.CALL.equals(node.type())) {
            return compileNodeTree_call(node, processPages, isInPage, compilePageContext);
        } else if (NDocNodeType.IF.equals(node.type())) {
            return compileNodeTree_if(node, processPages, isInPage, compilePageContext);
        } else if (NDocNodeType.FOR.equals(node.type())) {
            return compileNodeTree_for(node, processPages, isInPage, compilePageContext);
        } else if (NDocNodeType.ASSIGN.equals(node.type())) {
            NDocItem p = node.parent();
            NDocNode n=(NDocNode) p;
            String varName = node.getProperty(NDocPropName.NAME).get().getValue().asStringValue().get();
            NElement varExpr = node.getProperty(NDocPropName.VALUE).get().getValue();
            n.setVar(varName,engine.evalExpression(node,varExpr));
            return new ArrayList<>();
        } else if (NDocNodeType.EXPR.equals(node.type())) {
            NElement varExpr = node.getProperty(NDocPropName.VALUE).get().getValue();
            NElement element = engine.evalExpression(node, varExpr);
            NDocItem h = engine.newNode(element, new DefaultNDocNodeFactoryParseContext(
                    compilePageContext.document(),
                    varExpr,
                    engine,
                    NDocUtils.nodePath(node),
                    node.source()

            )).get();
            return engine.compileItem(h, compilePageContext);
        } else {
            List<NDocNode> aa = new ArrayList<>();
            for (NDocNode n : nDocNodes0) {
                n = n.copy();
                n.setParent(node);
                List<NDocProp> properties = n.getProperties();
                for (NDocProp property : properties) {
                    NElement value0 = property.getValue();
                    NElement value = engine.evalExpression(n, value0);
                    n.setProperty(property.getName(), value);
                }
                List<NDocNode> newChildren = new ArrayList<>();
                for (NDocNode child : n.children()) {
                    newChildren.addAll(compileNodeTree(child, processPages, isInPage, compilePageContext));
                }
                n.setChildren(newChildren.toArray(new NDocNode[0]));
                aa.add(n);
            }
            return aa;
        }
    }

    private List<NDocNode> _process_call_node(NDocNodeDef d, CtrlNDocNodeCall c, boolean processPages, boolean isInPage, CompilePageContext compilePageContext) {
        String uid = c.getCallName();
        NElement callDeclaration = c.getCallExpr();
        NDocNodeDefParam[] expectedParams = d.params();
        NDocNodeDefParam[] allExpectedParams = Arrays.copyOf(expectedParams, expectedParams.length + 1);
        allExpectedParams[expectedParams.length] = new NDocNodeDefParamImpl("componentBody", NElement.ofArray(c.getCallBody().toArray(new NElement[0])));
        NDocNodeDefParam[] effectiveParams = new NDocNodeDefParam[allExpectedParams.length];
        List<NElement> callArgs = c.getCallArgs();
        List<NDocProp> extraParams = new ArrayList<>();

        if (callArgs.stream().allMatch(x -> x.isNamedPair())) {
            for (NElement e : callArgs) {
                NPairElement p = e.asPair().get();
                String n = p.key().asStringValue().get();
                int pos = NArrays.indexOfByMatcher(allExpectedParams, a -> NNameFormat.equalsIgnoreFormat(a.name(), n));
                NElement newValue = NDocUtils.addCompilerDeclarationPath(p.value(),c.source());
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
                    engine.messages().log(NDocMsg.of(errMsg, c.source()));
                }
            }
            for (int i = 0; i < allExpectedParams.length; i++) {
                if (effectiveParams[i] == null) {
                    NElement v = allExpectedParams[i].value();
                    if (v != null) {
                        effectiveParams[i] = allExpectedParams[i];
                    } else {
                        NMsg errMsg = NMsg.ofC("missing param %s for %s in %s", allExpectedParams[i].name(), uid, callDeclaration);
                        engine.messages().log(NDocMsg.of(errMsg, c.source()));
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
            engine.messages().log(NDocMsg.of(errMsg, c.source()));
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
                    body[i].setVar(effectiveParams[j].name(), NDocUtils.addCompilerDeclarationPath(effectiveParams[j].value(),NDocUtils.sourceOf(d)));
                }
            }
        }
        List<NDocNode> result = new ArrayList<>();
        for (NDocNode i : body) {
            result.addAll(compileNodeTree(i, processPages, isInPage, compilePageContext));
        }
        return result;
    }

    private List<NDocNode> compileNodeTree_call(NDocNode node, boolean processPages, boolean isInPage, CompilePageContext compilePageContext) {
        if (NDocNodeType.CALL.equals(node.type())) {
            CtrlNDocNodeCall c = (CtrlNDocNodeCall) node;
            if (!isInPage || (isInPage && processPages)) {
                String uid = c.getCallName();
                NDocNode currNode = NDocUtils.firstNodeUp(node.parent());
                NOptional<NDocNodeDef> dd = findDefinition(currNode, uid);
                if (dd.isPresent()) {
                    return _process_call_node(dd.get(), c, processPages, isInPage, compilePageContext);
                } else {
                    NOptional<NDocFunction> t = engine.findFunction(currNode, uid);
                    if (t.isPresent()) {
                        return _process_call_fct(t.get(), c, processPages, isInPage, compilePageContext);
                    }
                    compilePageContext.messages().log(NDocMsg.of(NMsg.ofC("undefined node %s", uid).asError(), c.source()));
                    return new ArrayList<>(Arrays.asList(DefaultNDocNode.ofText((NMsg.ofC("undefined node %s", uid).toString()))));
                }
            }
            return new ArrayList<>(Collections.singleton(node));
        }
        throw new NIllegalArgumentException(NMsg.ofC("unexpected node type %s", node.type()));
    }

    private List<NDocNode> compileNodeTree_if(NDocNode node, boolean processPages, boolean isInPage, CompilePageContext compilePageContext) {
        if (NDocNodeType.IF.equals(node.type())) {
            CtrlNDocNodeIf c = (CtrlNDocNodeIf) node;
            if (!isInPage || (isInPage && processPages)) {
                NElement cond = c.getCond();
                NElement r = engine.evalExpression(c, cond);
                boolean b = NDocUtils.asBoolean(r);
                if (b) {
                    List<NDocNode> tb = c.getTrueBloc();
                    List<NDocNode> tb2 = new ArrayList<>();
                    if (tb != null) {
                        for (NDocNode d : tb) {
                            d = d.copy();
                            d.setParent(c);
                            tb2.addAll(compileNodeTree(d, processPages, isInPage, compilePageContext));
                        }
                    }
                    return tb2;
                } else {
                    List<NDocNode> tb = c.getFalseBloc();
                    List<NDocNode> tb2 = new ArrayList<>();
                    if (tb != null) {
                        for (NDocNode d : tb) {
                            d = d.copy();
                            d.setParent(c);
                            tb2.addAll(compileNodeTree(d, processPages, isInPage, compilePageContext));
                        }
                    }
                    return tb2;
                }
            }
            return new ArrayList<>(Collections.singleton(node));
        }
        throw new NIllegalArgumentException(NMsg.ofC("unexpected node type %s", node.type()));
    }

    private List<NDocNode> compileNodeTree_for(NDocNode node, boolean processPages, boolean isInPage, CompilePageContext compilePageContext) {
        if (NDocNodeType.FOR.equals(node.type())) {
            CtrlNDocNodeFor c = (CtrlNDocNodeFor) node;
            if (!isInPage || (isInPage && processPages)) {
                NElement varName = c.getVarName();
                NElement varExpr = c.getVarExpr();
                String varNameStr = null;
                if (varName.isName()) {
                    varNameStr = varName.asStringValue().get();
                } else {
                    throw new NIllegalArgumentException(NMsg.ofC("expected varName in for contruct : %s", node));
                }
                NElement anyVal = engine.evalExpression(node, varExpr);
                List<NElement> b = new ArrayList<>();
                if (anyVal.isAnyArray()) {
                    b.addAll(anyVal.asArray().get().children());
                } else {
                    b.add(anyVal);
                }
                List<NDocNode> result = new ArrayList<>();
                for (NElement o : b) {
                    for (NElement e : c.getBody()) {
                        NDocNode nDocNode = (NDocNode) engine.newNode(e, new DefaultNDocNodeFactoryParseContext(
                                compilePageContext.document(),
                                e,
                                engine,
                                NDocUtils.nodePath(node),
                                node.source()

                        )).get();
                        nDocNode.setVar(varNameStr, o);
                        result.addAll(compileNodeTree(nDocNode, processPages, isInPage, compilePageContext));
                    }
                }
                return result;
            }
            return new ArrayList<>(Collections.singleton(node));
        }
        throw new NIllegalArgumentException(NMsg.ofC("unexpected node type %s", node.type()));
    }

    private List<NDocNode> _process_call_fct(NDocFunction t, CtrlNDocNodeCall c, boolean processPages, boolean isInPage, CompilePageContext compilePageContext) {
        NDocItem currNode = c.parent();
        NDocResource source = engine.computeSource(currNode);
        NDocFunctionContext ctx = new DefaultNDocFunctionContext(engine);
        List<NElement> callArgs = c.getCallArgs();
        NElement result = t.invoke(callArgs.stream().map(x -> engine.createRawArg(c, x)).toArray(NDocFunctionArg[]::new), ctx);
        if (result == null) {
            return new ArrayList<>();
        }
//        if (result instanceof NDocNode) {
//            NDocNode n = (NDocNode) result;
//            return new ArrayList<>(Collections.singleton(n));
//        }
        DefaultNDocNodeFactoryParseContext ctx2 = new DefaultNDocNodeFactoryParseContext(
                compilePageContext.document(),
                result,
                engine,
                NDocUtils.nodePath(c),
                source
        );
        NOptional<NDocItem> n = engine.newNode(result, ctx2);
        if (n.isPresent()) {
            NDocItem nn = n.get();
            if (nn instanceof NDocNode) {
                return compileNodeTree((NDocNode) nn, processPages, isInPage, compilePageContext);
            } else if (nn instanceof NDocItemList && ((NDocItemList) nn).getItems().stream().allMatch(x -> x instanceof NDocItem)) {
                List<NDocNode> r = new ArrayList<>();
                for (NDocItem item : ((NDocItemList) nn).getItems()) {
                    r.addAll(compileNodeTree((NDocNode) item, processPages, isInPage, compilePageContext));
                }
                return r;
            } else {
                engine.newNode(result, ctx2).get();
                NMsg msg = NMsg.ofC("unexpected node type %s", nn.getClass());
                engine.messages().log(NDocMsg.of(msg.asError()));
                return Arrays.asList(DefaultNDocNode.ofText(msg.toString()));
            }
        } else {
            NMsg msg = NMsg.ofC("not found node for %s", result);
            engine.messages().log(NDocMsg.of(msg.asError()));
            return Arrays.asList(DefaultNDocNode.ofText(msg.toString()));
        }
    }


    public NDocResource computeSource(NDocItem node) {
        while (node != null) {
            NDocResource s = node.source();
            if (s != null) {
                return s;
            }
            node = node.parent();
        }
        return null;
    }

    public List<NDocNode> compilePage(NDocNode node, CompilePageContext context) {
        return compileNodeTree(node, true, true, context);
    }

//    private static class MyNDocNodeFlowControlProcessorContext implements NDocNodeFlowControlProcessorContext {
//        private NDocument document;
//        private NDocLogger messages;
//
//        public MyNDocNodeFlowControlProcessorContext(NDocument document, NDocLogger messages) {
//            this.document = document;
//            this.messages = messages;
//        }
//
//        @Override
//        public NElement evalExpression(NDocNode node, NElement expression) {
//            NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(node);
//            return ne.eval(node, NElemUtils.toElement(expression));
//        }
//
//        @Override
//        public NElement resolveVarValue(NDocNode node, String varName) {
//            return evalExpression(node, NElement.ofName("$" + varName));
//        }
//    }
}
