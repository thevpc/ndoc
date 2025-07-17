package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.model.node.*;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.HProp;
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
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.*;


import java.util.*;
import java.util.stream.Collectors;

public class HDocumentCompiler {

    private NDocEngine engine;
    private NDocLogger messages;
    private IfNDocFlowControlProcessorFactory flowControlProcessorFactory = new IfNDocFlowControlProcessorFactory();

    public HDocumentCompiler(NDocEngine engine, NDocLogger messages) {
        this.engine = engine;
        this.messages = messages;
    }

    public NDocDocumentLoadingResult compile(NDocument document) {
        NDocDocumentLoadingResultImpl result = new NDocDocumentLoadingResultImpl(engine.computeSource(document.root()), messages);
        result.setDocument(document);
        NDocNode root = document.root();
        processUuid(root);
//        root = processCalls(root, result);
        root = processInheritance(root, result);
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

    protected void prepareInheritanceSingle(String a, NDocNode node, NDocDocumentLoadingResultImpl result,
                                            Set<String> newAncestors,
                                            List<NDocNode> ancestorsList,
                                            List<NDocNode> inheritedChildren,
                                            NDocProperties inheritedProps,
                                            List<HStyleRule> inheritedRules
    ) {
        NDocNode aa = null;
        try {
            aa = findAncestor(node, a);
        } catch (Exception ex) {
            result.messages().log(NDocMsg.of(
                            NMsg.ofC("invalid ancestor %s for %s : %s", a, net.thevpc.ndoc.api.util.HUtils.strSnapshot(node), ex).asSevere(),
                            engine.computeSource(node)
                    )
            );
        }
        if (aa != null) {
            newAncestors.remove(a);
            for (HProp p : aa.getProperties()) {
                switch (p.getName()) {
                    case NDocPropName.NAME:
                    case NDocPropName.TEMPLATE: {
                        break;
                    }
                    default: {
                        inheritedProps.set(p);
                        break;
                    }
                }
            }
            ancestorsList.add(aa);
            for (NDocNode child : aa.children()) {
                NDocResource source = computeSource(child);
                child = child.copy();
                child.setSource(source);
                inheritedChildren.add(child);
            }
            inheritedRules.addAll(Arrays.asList(aa.rules()));
        } else {
            result.messages().log(
                    NDocMsg.of(NMsg.ofC("missing ancestor '%s' for %s", a,
                                    net.thevpc.ndoc.api.util.HUtils.strSnapshot(node)
                            ).asWarning(),
                            engine.computeSource(node)
                    )
            );
            //throw new IllegalArgumentException("ancestor not found " + a + " for " + node);
        }
    }

    public NOptional<NDocNodeDef> findDefinition(NDocNode node, String name) {
        NDocNode currNode = node;
        while (currNode != null) {
            for (NDocNodeDef o : currNode.definitions()) {
                if (NNameFormat.equalsIgnoreFormat(o.name(), name)) {
                    return NOptional.of(o);
                }
            }
            currNode = currNode.parent();
        }
        return NOptional.ofNamedEmpty("definition for " + name);
    }

    protected NDocNode[] compileNodeTree(NDocNode node) {
        if (NDocNodeType.CALL.equals(node.type())) {
            return _process_call(node);
        }
        node = node.copy();
        List<NDocNode> newChildren = new ArrayList<>();
        for (NDocNode child : node.children()) {
            newChildren.addAll(Arrays.asList(compileNodeTree(child)));
        }
        node.setChildren(newChildren.toArray(new NDocNode[0]));
        return new NDocNode[]{node};
    }

    private NDocNode[] _process_call(NDocNode node) {
        if (NDocNodeType.CALL.equals(node.type())) {
            CtrlNDocNodeCall c = (CtrlNDocNodeCall) node;
            String uid = c.getCallName();
            NElement callDeclaration = c.getCallExpr();
            //                    String uid = HUtils.uid(ee.name());
            NDocNode currNode = node.parent();
            NDocNodeDef d = findDefinition(currNode, uid).get();
            List<NElement> callArgs = c.getCallArgs();
            NDocNodeDefParam[] expectedParams = d.params();
            NDocNodeDefParam[] effectiveParams = new NDocNodeDefParam[expectedParams.length];
            Map<String, NElement> extraProperties = new HashMap<>();

            if (callArgs.stream().allMatch(x -> x.isNamedPair())) {
                for (NElement e : callArgs) {
                    NPairElement p = e.asPair().get();
                    String n = p.key().asStringValue().get();
                    int pos = NArrays.indexOfByMatcher(expectedParams, a -> NNameFormat.equalsIgnoreFormat(a.name(), n));
                    if (pos >= 0) {
                        effectiveParams[pos] = new NDocNodeDefParamImpl(expectedParams[pos].name(), p.value());
                    }
                }
                for (int i = 0; i < expectedParams.length; i++) {
                    if (effectiveParams[i] == null) {
                        NElement v = expectedParams[i].value();
                        if (v != null) {
                            effectiveParams[i] = expectedParams[i];
                        } else {
                            NMsg errMsg = NMsg.ofC("missing param %s for %s in %s", expectedParams[i].name(), uid, callDeclaration);
                            messages.log(NDocMsg.of(errMsg, node.source()));
                            throw new NIllegalArgumentException(errMsg);
                        }
                    }
                }
            } else if (callArgs.stream().noneMatch(x -> x.isNamedPair())) {
                if (expectedParams.length == callArgs.size()) {
                    for (int i = 0; i < expectedParams.length; i++) {
                        effectiveParams[i] = new NDocNodeDefParamImpl(expectedParams[i].name(), callArgs.get(i));
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
                for (int j = effectiveParams.length - 1; j >= 0; j--) {
                    body[i].children().add(0, newAssign(effectiveParams[i].name(), effectiveParams[i].value()));
                }
            }
            return body;
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
        ).toArray(HProp[]::new));
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

    protected NDocNode processInheritance(NDocNode node, NDocDocumentLoadingResultImpl result) {
        String[] t = node.getAncestors();
        if (t.length > 0) {
            Set<String> newAncestors = new HashSet<>(Arrays.asList(t));
            NDocProperties inheritedProps = new NDocProperties();
            List<NDocNode> inheritedChildren = new ArrayList<>();
            List<HStyleRule> inheritedRules = new ArrayList<>();
            List<NDocNode> ancestorsList = new ArrayList<>();
            for (String a : t) {
                prepareInheritanceSingle(a, node, result, newAncestors, ancestorsList, inheritedChildren, inheritedProps, inheritedRules);
            }
            node.setProperty(NDocPropName.ANCESTORS, NElement.ofStringArray(newAncestors.toArray(new String[0])));
            for (HProp p : inheritedProps.toList()) {
                NOptional<HProp> u = node.getProperty(p.getName());
                if (!u.isPresent()) {
                    node.setProperty(p);
                }
            }
            if (!inheritedRules.isEmpty()) {
                //must add them upfront
                inheritedRules.addAll(Arrays.asList(node.rules()));
                node.setRules(inheritedRules.toArray(new HStyleRule[0]));
            }
            if (!inheritedChildren.isEmpty()) {
                for (NDocNode child : node.children()) {
                    NDocResource source = computeSource(child);
                    child = child.copy();
                    child.setSource(source);
                    inheritedChildren.add(child);
                }
                node.setChildren(inheritedChildren.toArray(new NDocNode[0]));
            }
            //return node;
        }
        List<NDocNode> children = node.children();
        for (int i = 0; i < children.size(); i++) {
            NDocNode child = children.get(i);
            NDocNode child2 = processInheritance(child, result);
            if (child2 != child) {
                node.setChildAt(i, child2);
            }
        }
        return node;
    }

    protected NDocNode findAncestor(NDocNode node, String name, HProp... args) {
        NDocNodeDef ancestor0 = findAncestorDefinition(node, name);
        NDocNode[] body = ancestor0.body();
        if (body.length == 0) {
            return new DefaultNDocNode(NDocNodeType.VOID);
        }
        for (int i = 0; i < body.length; i++) {
            //apply args
            body[i] = applyArgs(body[i], args);
        }
        if (body.length == 1) {
            return body[0];
        }
        DefaultNDocNode stack = new DefaultNDocNode(NDocNodeType.STACK);
        stack.addAll(body);
        return stack;
    }

    private NDocNode applyArgs(NDocNode nn, HProp[] args) {
        // TODO
        return nn.copy();
    }

    protected NDocNodeDef findAncestorDefinition(NDocNode node, String name) {
        NDocNodeDef temp = null;
        NDocNode parent = node.parent();
        String finalName = net.thevpc.ndoc.api.util.HUtils.uid(name);
        while (parent != null) {
            List<NDocNodeDef> r = new ArrayList<>();
            for (NDocNodeDef x : parent.definitions()) {
                if (Objects.equals(HUtils.uid(x.name()), finalName)) {
                    r.add(x);
                }
            }
            if (r.size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("too many templates : ").append(finalName);
                for (int i = 0; i < r.size(); i++) {
                    NDocNodeDef nn = r.get(i);
                    NDocResource n = nn.source();
                    sb.append("\n\t[").append(n).append("] (").append(i + 1).append("/").append(r.size()).append(") : ").append(net.thevpc.ndoc.api.util.HUtils.strSnapshot(nn));
                }
                throw new IllegalArgumentException(sb.toString());
            }
            if (r.size() == 1) {
                temp = r.get(0);
                break;
            }
            parent = parent.parent();
        }
        return temp;
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
