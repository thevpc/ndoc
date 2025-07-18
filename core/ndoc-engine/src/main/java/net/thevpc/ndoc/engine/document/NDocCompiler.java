package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.ndoc.api.model.node.*;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.NDocProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.api.style.HStyleRule;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.engine.control.CtrlNDocNodeCall;
import net.thevpc.ndoc.engine.control.IfNDocFlowControlProcessorFactory;
import net.thevpc.ndoc.engine.parser.NDocDocumentLoadingResultImpl;
import net.thevpc.ndoc.engine.parser.NDocNodeDefParamImpl;
import net.thevpc.ndoc.spi.NDocNodeFlowControlProcessor;
import net.thevpc.ndoc.spi.NDocNodeFlowControlProcessorContext;
import net.thevpc.ndoc.spi.base.model.DefaultNDocNode;
import net.thevpc.ndoc.spi.eval.NDocNodeEvalNDoc;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.NArrayElement;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.*;


import java.util.*;
import java.util.stream.Collectors;

public class NDocCompiler {

    private NDocEngine engine;
    private NDocLogger messages;
    private IfNDocFlowControlProcessorFactory flowControlProcessorFactory = new IfNDocFlowControlProcessorFactory();

    public NDocCompiler(NDocEngine engine, NDocLogger messages) {
        this.engine = engine;
        this.messages = messages;
    }

    public NDocDocumentLoadingResult compile(NDocument document) {
        document=document.copy();
        NDocDocumentLoadingResultImpl result = new NDocDocumentLoadingResultImpl(engine.computeSource(document.root()), messages);
        NDocNode root = document.root();
        processUuid(root);
//        root = processCalls(root, result);
        root = processControlFlow(root, result, new MyNDocNodeFlowControlProcessorContext(document, messages));
        NDocNode[] all = compileNodeTree(root);
        if (all.length == 0) {
            root = new DefaultNDocNode(NDocNodeType.VOID);
        } else if (all.length == 1) {
            root = all[0];
        } else {
            root = new DefaultNDocNode(NDocNodeType.PAGE_GROUP);
            root.setChildren(all);
        }
//        root = removeDeclarations(root, result);
        processRootPages(root);
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

    protected NDocNode processControlFlow(NDocNode root, NDocDocumentLoadingResultImpl result, NDocNodeFlowControlProcessorContext context) {
        NDocNode oldRoot = root;
        NDocNode[] multiRoot = processControlFlowCurrent(new NDocNode[]{root}, result, context);
        if (multiRoot.length == 0) {
            root = new DefaultNDocNode(NDocNodeType.PAGE);
            root.setSource(oldRoot.source());
        } else {
            root = new DefaultNDocNode(NDocNodeType.PAGE_GROUP);
            root.setSource(oldRoot.source());
            root.setChildren(multiRoot);
        }
        return root;
    }

    protected NDocNode[] processControlFlowCurrent(NDocNode[] nodes, NDocDocumentLoadingResultImpl result, NDocNodeFlowControlProcessorContext context) {
        List<NDocNode> curr = new ArrayList<>(Arrays.asList(nodes));
        for (NDocNodeFlowControlProcessor a : flowControlProcessorFactory.list()) {
            List<NDocNode> result2 = new ArrayList<>();
            for (int i = 0; i < curr.size(); i++) {
                NDocNode cc = curr.get(i);
                if (cc != null) {
                    NDocNode[] p = a.process(cc, context);
                    if (p == null) {
                        result2.add(cc);
                    } else {
                        result2.addAll(Arrays.stream(p).filter(Objects::nonNull).collect(Collectors.toList()));
                    }
                }
            }
            curr = result2;
        }
        return curr.toArray(new NDocNode[0]);
    }

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


    public NOptional<NDocNodeDef> findDefinition(NDocNode node, String name) {
        NDocNode currNode = node;
        while (currNode != null) {
            for (NDocNodeDef o : currNode.nodeDefinitions()) {
                if (NNameFormat.equalsIgnoreFormat(o.name(), name)) {
                    return NOptional.of(o);
                }
            }
            currNode = currNode.parent();
        }
        return NOptional.ofNamedEmpty("definition for " + name);
    }

    protected NDocNode[] compileNodeTree(NDocNode node) {
        NDocNode[] nDocNodes0=new NDocNode[]{node};
        if (NDocNodeType.CALL.equals(node.type())) {
            nDocNodes0 = _process_call(node);
        }
        List<NDocNode> aa=new ArrayList<>();
        for (NDocNode n : nDocNodes0) {
            n = n.copy();
            List<NDocNode> newChildren = new ArrayList<>();
            for (NDocNode child : n.children()) {
                newChildren.addAll(Arrays.asList(compileNodeTree(child)));
            }
            n.setChildren(newChildren.toArray(new NDocNode[0]));
            aa.add(n);
        }
        return aa.toArray(new NDocNode[0]);
    }

    private NDocNode[] _process_call(NDocNode node) {
        if (NDocNodeType.CALL.equals(node.type())) {
            CtrlNDocNodeCall c = (CtrlNDocNodeCall) node;
            String uid = c.getCallName();
            NElement callDeclaration = c.getCallExpr();
            //                    String uid = HUtils.uid(ee.name());
            NDocNode currNode = node.parent();
            NOptional<NDocNodeDef> dd = findDefinition(currNode, uid);
            List<NElement> callArgs = c.getCallArgs();
            List<NDocProp> extraParams=new ArrayList<>();
            if (dd.isPresent()) {
                NDocNodeDef d = dd.get();
                NDocNodeDefParam[] expectedParams = d.params();
                NDocNodeDefParam[] allExpectedParams = Arrays.copyOf(expectedParams, expectedParams.length + 1);
                allExpectedParams[expectedParams.length]=new NDocNodeDefParamImpl("body",NElement.ofArray(c.getCallBody().toArray(new NElement[0])));
                NDocNodeDefParam[] effectiveParams = new NDocNodeDefParam[allExpectedParams.length];

                if (callArgs.stream().allMatch(x -> x.isNamedPair())) {
                    for (NElement e : callArgs) {
                        NPairElement p = e.asPair().get();
                        String n = p.key().asStringValue().get();
                        int pos = NArrays.indexOfByMatcher(allExpectedParams, a -> NNameFormat.equalsIgnoreFormat(a.name(), n));
                        if (pos >= 0) {
                            effectiveParams[pos] = new NDocNodeDefParamImpl(allExpectedParams[pos].name(), p.value());
                        }else{
                            extraParams.add(new NDocProp(n,p.value()));
                        }
                    }
                    for (Map.Entry<String, NElement> e : c.getBodyVars().entrySet()) {
                        String n = e.getKey();
                        int pos = NArrays.indexOfByMatcher(allExpectedParams, a -> NNameFormat.equalsIgnoreFormat(a.name(), n));
                        if (pos >= 0) {
                            effectiveParams[pos] = new NDocNodeDefParamImpl(allExpectedParams[pos].name(), e.getValue());
                        }else{
                            NMsg errMsg = NMsg.ofC("undefined param %s for %s in %s. ignored", n, uid, callDeclaration);
                            messages.log(NDocMsg.of(errMsg, node.source()));
                        }
                    }
                    for (int i = 0; i < allExpectedParams.length; i++) {
                        if (effectiveParams[i] == null) {
                            NElement v = allExpectedParams[i].value();
                            if (v != null) {
                                effectiveParams[i] = allExpectedParams[i];
                            } else {
                                NMsg errMsg = NMsg.ofC("missing param %s for %s in %s", allExpectedParams[i].name(), uid, callDeclaration);
                                messages.log(NDocMsg.of(errMsg, node.source()));
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
                    messages.log(NDocMsg.of(errMsg, node.source()));
                    throw new NIllegalArgumentException(errMsg);
                }

                NDocNode[] body = Arrays.copyOf(d.body(), d.body().length);
                for (int i = 0; i < body.length; i++) {
                    body[i] = body[i].copy();
                    for (NDocProp extraParam : extraParams) {
                        body[i].setProperty(extraParam);
                    }
                    for (int j = effectiveParams.length - 1; j >= 0; j--) {
                        if(effectiveParams[j]!=null) {
                            body[i].children().add(0, newAssign(effectiveParams[j].name(), effectiveParams[j].value()));
                        }
                    }
                }
                return body;
            } else {
                NOptional<NDocFunction> t = engine.findFunction(currNode, uid);
                if (t.isPresent()) {
                    NDocFunctionContext ctx = engine.createFunctionContext(currNode);
                    Object result = t.get().invoke(callArgs.stream().map(x -> ctx.createRawArg(x)).toArray(NDocFunctionArg[]::new), ctx);
                    if (result == null) {
                        return new NDocNode[0];
                    }
                    if (result instanceof NDocNode) {
                        NDocNode n = (NDocNode) result;
                        return new NDocNode[]{n};
                    }
                    DefaultNDocNode tn = new DefaultNDocNode(NDocNodeType.TEXT);
                    tn.setProperty(NDocPropName.VALUE, NElements.of().toElement(result));
                    return new NDocNode[]{tn};
                }

            }
        }
        throw new NIllegalArgumentException(NMsg.ofC("unexpected node type %s", node.type()));
    }


    private NDocNode inlineNodeDefinitionCall(NDocNode objectDefNode, NElement callFunction) {
        NDocNode inlinedNode = new DefaultNDocNode(NDocNodeType.STACK);
        NArrayElement objectDefArgsItem = (NArrayElement) objectDefNode.getPropertyValue("args").orNull();
        NElement[] objectDefArgs = objectDefArgsItem == null ? new NElement[0] : objectDefArgsItem.children().toArray(new NElement[0]);
        inlinedNode.setSource(objectDefNode.source());
        inlinedNode.setStyleClasses(objectDefNode.getStyleClasses());
        inlinedNode.setProperties(objectDefNode.getProperties().stream().filter(x ->
                NDocPropName.NAME.equals(x.getName())
                        && !NDocPropName.ARGS.equals(x.getName())
        ).toArray(NDocProp[]::new));
        inlinedNode.setRules(objectDefNode.rules());
        List<NElement> passedArgs = callFunction.asUplet().map(x -> x.params()).orElse(Collections.emptyList());
        NElement[] passedArgsArr = passedArgs == null ? new NElement[0] : passedArgs.toArray(new NElement[0]);
        inlinedNode.children().add(newAssign(NDocPropName.ARGS, NElement.ofArray(passedArgsArr)));
        for (int i = 0; i < passedArgsArr.length; i++) {
            NElement passedArg = passedArgsArr[i];
            if (passedArg.isSimplePair()) {
                NPairElement pair = passedArg.asPair().get();
                NElement value = pair.value();
                if (HUtils.getCompilerDeclarationPath(value) == null) {
                    value = HUtils.addCompilerDeclarationPath(value, HUtils.getCompilerDeclarationPath(pair));
                }
                inlinedNode.children().add(newAssign(pair.key().asStringValue().get(), value));
            } else {
                if (i < objectDefArgs.length) {
                    String paramName = objectDefArgs[i].asStringValue().get();
                    inlinedNode.children().add(newAssign(paramName, passedArg));
                } else {
                    NMsg message = NMsg.ofC("[%s] invalid index %s for %s in %s", net.thevpc.ndoc.api.util.HUtils.shortName(objectDefNode.source()), (i + 1), objectDefNode.getName(), callFunction).asSevere();
                    messages.log(NDocMsg.of(message, objectDefNode.source()));
                    throw new NIllegalArgumentException(message);
                }
            }
        }
        //n.setParent(context.node().parent());
        inlinedNode.setParent(objectDefNode.parent());
        if (objectDefNode.children() != null) {
            for (NDocNode newBodyElement : objectDefNode.children()) {
                inlinedNode.children().add(newBodyElement.copy());
            }
        }
//        inlinedNode.toString();
        return inlinedNode;
    }

    private NDocNode newAssign(String name, NElement value) {
        NDocNode n = new DefaultNDocNode(NDocNodeType.ASSIGN);
        n.setProperty(NDocPropName.NAME, NElement.ofString(name));
        n.setProperty(NDocPropName.VALUE, value);
        return n;
    }



    public NDocResource computeSource(NDocNode node) {
        while (node != null) {
            NDocResource s = node.source();
            if (s != null) {
                return s;
            }
            node = node.parent();
        }
        return null;
    }

    public NDocNode[] compilePage(NDocNode p) {
        return new NDocNode[]{p};
    }

    private static class MyNDocNodeFlowControlProcessorContext implements NDocNodeFlowControlProcessorContext {
        private NDocument document;
        private NDocLogger messages;

        public MyNDocNodeFlowControlProcessorContext(NDocument document, NDocLogger messages) {
            this.document = document;
            this.messages = messages;
        }

        @Override
        public NElement evalExpression(NDocNode node, NElement expression) {
            NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(node);
            return ne.eval(NElemUtils.toElement(expression));
        }

        @Override
        public NElement resolveVarValue(NDocNode node, String varName) {
            return evalExpression(node, NElement.ofName("$" + varName));
        }
    }
}
