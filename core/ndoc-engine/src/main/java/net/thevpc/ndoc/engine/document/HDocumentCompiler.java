package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.model.node.*;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HStyleRule;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.engine.control.CtrlHNodeCall;
import net.thevpc.ndoc.engine.control.IfHNodeFlowControlProcessorFactory;
import net.thevpc.ndoc.engine.parser.HDocumentLoadingResultImpl;
import net.thevpc.ndoc.engine.parser.HNodeDefParamImpl;
import net.thevpc.ndoc.spi.NDocNodeFlowControlProcessor;
import net.thevpc.ndoc.spi.NDocNodeFlowControlProcessorContext;
import net.thevpc.ndoc.spi.base.model.DefaultHNode;
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
    private HLogger messages;
    private IfHNodeFlowControlProcessorFactory flowControlProcessorFactory = new IfHNodeFlowControlProcessorFactory();

    public HDocumentCompiler(NDocEngine engine, HLogger messages) {
        this.engine = engine;
        this.messages = messages;
    }

    public HDocumentLoadingResult compile(NDocument document) {
        HDocumentLoadingResultImpl result = new HDocumentLoadingResultImpl(engine.computeSource(document.root()), messages);
        result.setDocument(document);
        HNode root = document.root();
        processUuid(root);
//        root = processCalls(root, result);
        root = processInheritance(root, result);
        root = processControlFlow(root, result, new MyNDocNodeFlowControlProcessorContext(document, messages));
        HNode[] all = compileNodeTree(root);
        if (all.length == 0) {
            root = new DefaultHNode(HNodeType.VOID);
        } else if (all.length == 1) {
            root = all[0];
        } else {
            root = new DefaultHNode(HNodeType.PAGE_GROUP);
            root.setChildren(all);
        }
//        root = removeDeclarations(root, result);
        processRootPages(root);
        return result;
    }

    public boolean processRootPages(HNode node) {
        switch (node.type()) {
            case HNodeType.PAGE_GROUP: {
                if (NBlankable.isBlank(node.getUuid())) {
                    node.setUuid(UUID.randomUUID().toString());
                }
                boolean someChanges = false;
                List<HNode> children = new ArrayList<>(node.children());
                List<HNode> newChildren = new ArrayList<>();
                List<HNode> pending = null;
                for (HNode c : children) {
                    if (c == null) {
                        someChanges = true;
                    } else {
                        someChanges |= processRootPages(c);
                        if (Objects.equals(c.type(), HNodeType.PAGE_GROUP)
                                || Objects.equals(c.type(), HNodeType.PAGE)
                                //assign are retained in the same level!
                                || Objects.equals(c.type(), HNodeType.ASSIGN)
                        ) {
                            if (pending != null && !pending.isEmpty()) {
                                HNode newPage = engine.documentFactory().of(HNodeType.PAGE);
                                newChildren.add(newPage);
                                newPage.addAll(pending.toArray(new HNode[0]));
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
                    HNode newPage = engine.documentFactory().of(HNodeType.PAGE);
                    newPage.addAll(pending.toArray(new HNode[0]));
                    newChildren.add(newPage);
                }
                if (someChanges) {
                    node.children().clear();
                    node.addAll(newChildren.toArray(new HNode[0]));
                }
                return someChanges;
            }
            default: {
                return false;
            }
        }
    }

    protected void processUuid(HNode node) {
        if (NBlankable.isBlank(node.getUuid())) {
            node.setUuid(UUID.randomUUID().toString());
        }
        for (HNode child : node.children()) {
            processUuid(child);
        }
    }

    protected HNode processControlFlow(HNode root, HDocumentLoadingResultImpl result, NDocNodeFlowControlProcessorContext context) {
        HNode oldRoot = root;
        HNode[] multiRoot = processControlFlowCurrent(new HNode[]{root}, result, context);
        if (multiRoot.length == 0) {
            root = new DefaultHNode(HNodeType.PAGE);
            root.setSource(oldRoot.source());
        } else {
            root = new DefaultHNode(HNodeType.PAGE_GROUP);
            root.setSource(oldRoot.source());
            root.setChildren(multiRoot);
        }
        return root;
    }

    protected HNode[] processControlFlowCurrent(HNode[] nodes, HDocumentLoadingResultImpl result, NDocNodeFlowControlProcessorContext context) {
        List<HNode> curr = new ArrayList<>(Arrays.asList(nodes));
        for (NDocNodeFlowControlProcessor a : flowControlProcessorFactory.list()) {
            List<HNode> result2 = new ArrayList<>();
            for (int i = 0; i < curr.size(); i++) {
                HNode cc = curr.get(i);
                if (cc != null) {
                    HNode[] p = a.process(cc, context);
                    if (p == null) {
                        result2.add(cc);
                    } else {
                        result2.addAll(Arrays.stream(p).filter(Objects::nonNull).collect(Collectors.toList()));
                    }
                }
            }
            curr = result2;
        }
        return curr.toArray(new HNode[0]);
    }

//    protected HNode removeDeclarations(HNode node, HDocumentLoadingResultImpl result) {
//        List<HNode> children = node.children();
//        for (int i = children.size() - 1; i >= 0; i--) {
//            HNode child = children.get(i);
//            if (child.isTemplate() || HNodeType.DEFINE.equals(child.type())) {
//                children.remove(i);
//            } else {
//                removeDeclarations(child, result);
//            }
//        }
//        return node;
//    }

    protected void prepareInheritanceSingle(String a, HNode node, HDocumentLoadingResultImpl result,
                                            Set<String> newAncestors,
                                            List<HNode> ancestorsList,
                                            List<HNode> inheritedChildren,
                                            HProperties inheritedProps,
                                            List<HStyleRule> inheritedRules
    ) {
        HNode aa = null;
        try {
            aa = findAncestor(node, a);
        } catch (Exception ex) {
            result.messages().log(HMsg.of(
                            NMsg.ofC("invalid ancestor %s for %s : %s", a, net.thevpc.ndoc.api.util.HUtils.strSnapshot(node), ex).asSevere(),
                            engine.computeSource(node)
                    )
            );
        }
        if (aa != null) {
            newAncestors.remove(a);
            for (HProp p : aa.getProperties()) {
                switch (p.getName()) {
                    case HPropName.NAME:
                    case HPropName.TEMPLATE: {
                        break;
                    }
                    default: {
                        inheritedProps.set(p);
                        break;
                    }
                }
            }
            ancestorsList.add(aa);
            for (HNode child : aa.children()) {
                HResource source = computeSource(child);
                child = child.copy();
                child.setSource(source);
                inheritedChildren.add(child);
            }
            inheritedRules.addAll(Arrays.asList(aa.rules()));
        } else {
            result.messages().log(
                    HMsg.of(NMsg.ofC("missing ancestor '%s' for %s", a,
                                    net.thevpc.ndoc.api.util.HUtils.strSnapshot(node)
                            ).asWarning(),
                            engine.computeSource(node)
                    )
            );
            //throw new IllegalArgumentException("ancestor not found " + a + " for " + node);
        }
    }

    public NOptional<HNodeDef> findDefinition(HNode node, String name) {
        HNode currNode = node;
        while (currNode != null) {
            for (HNodeDef o : currNode.definitions()) {
                if (NNameFormat.equalsIgnoreFormat(o.name(), name)) {
                    return NOptional.of(o);
                }
            }
            currNode = currNode.parent();
        }
        return NOptional.ofNamedEmpty("definition for " + name);
    }

    protected HNode[] compileNodeTree(HNode node) {
        if (HNodeType.CALL.equals(node.type())) {
            return _process_call(node);
        }
        node = node.copy();
        List<HNode> newChildren = new ArrayList<>();
        for (HNode child : node.children()) {
            newChildren.addAll(Arrays.asList(compileNodeTree(child)));
        }
        node.setChildren(newChildren.toArray(new HNode[0]));
        return new HNode[]{node};
    }

    private HNode[] _process_call(HNode node) {
        if (HNodeType.CALL.equals(node.type())) {
            CtrlHNodeCall c = (CtrlHNodeCall) node;
            String uid = c.getCallName();
            NElement callDeclaration = c.getCallExpr();
            //                    String uid = HUtils.uid(ee.name());
            HNode currNode = node.parent();
            HNodeDef d = findDefinition(currNode, uid).get();
            List<NElement> callArgs = c.getCallArgs();
            HNodeDefParam[] expectedParams = d.params();
            HNodeDefParam[] effectiveParams = new HNodeDefParam[expectedParams.length];
            Map<String, NElement> extraProperties = new HashMap<>();

            if (callArgs.stream().allMatch(x -> x.isNamedPair())) {
                for (NElement e : callArgs) {
                    NPairElement p = e.asPair().get();
                    String n = p.key().asStringValue().get();
                    int pos = NArrays.indexOfByMatcher(expectedParams, a -> NNameFormat.equalsIgnoreFormat(a.name(), n));
                    if (pos >= 0) {
                        effectiveParams[pos] = new HNodeDefParamImpl(expectedParams[pos].name(), p.value());
                    }
                }
                for (int i = 0; i < expectedParams.length; i++) {
                    if (effectiveParams[i] == null) {
                        NElement v = expectedParams[i].value();
                        if (v != null) {
                            effectiveParams[i] = expectedParams[i];
                        } else {
                            NMsg errMsg = NMsg.ofC("missing param %s for %s in %s", expectedParams[i].name(), uid, callDeclaration);
                            messages.log(HMsg.of(errMsg, node.source()));
                            throw new NIllegalArgumentException(errMsg);
                        }
                    }
                }
            } else if (callArgs.stream().noneMatch(x -> x.isNamedPair())) {
                if (expectedParams.length == callArgs.size()) {
                    for (int i = 0; i < expectedParams.length; i++) {
                        effectiveParams[i] = new HNodeDefParamImpl(expectedParams[i].name(), callArgs.get(i));
                    }
                }
            } else {
                NMsg errMsg = NMsg.ofC("cannot mix named and non named params in %s", callArgs);
                messages.log(HMsg.of(errMsg, node.source()));
                throw new NIllegalArgumentException(errMsg);
            }

            HNode[] body = Arrays.copyOf(d.body(), d.body().length);
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


    private HNode inlineNodeDefinitionCall(HNode objectDefNode, NElement callFunction) {
        HNode inlinedNode = new DefaultHNode(HNodeType.STACK);
        NArrayElement objectDefArgsItem = (NArrayElement) objectDefNode.getPropertyValue("args").orNull();
        NElement[] objectDefArgs = objectDefArgsItem == null ? new NElement[0] : objectDefArgsItem.children().toArray(new NElement[0]);
        inlinedNode.setSource(objectDefNode.source());
        inlinedNode.setStyleClasses(objectDefNode.getStyleClasses());
        inlinedNode.setProperties(objectDefNode.getProperties().stream().filter(x ->
                HPropName.NAME.equals(x.getName())
                        && !HPropName.ARGS.equals(x.getName())
        ).toArray(HProp[]::new));
        inlinedNode.setRules(objectDefNode.rules());
        List<NElement> passedArgs = callFunction.asUplet().map(x -> x.params()).orElse(Collections.emptyList());
        NElement[] passedArgsArr = passedArgs == null ? new NElement[0] : passedArgs.toArray(new NElement[0]);
        inlinedNode.children().add(newAssign(HPropName.ARGS, NElement.ofArray(passedArgsArr)));
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
                    messages.log(HMsg.of(message, objectDefNode.source()));
                    throw new NIllegalArgumentException(message);
                }
            }
        }
        //n.setParent(context.node().parent());
        inlinedNode.setParent(objectDefNode.parent());
        if (objectDefNode.children() != null) {
            for (HNode newBodyElement : objectDefNode.children()) {
                inlinedNode.children().add(newBodyElement.copy());
            }
        }
//        inlinedNode.toString();
        return inlinedNode;
    }

    private HNode newAssign(String name, NElement value) {
        HNode n = new DefaultHNode(HNodeType.ASSIGN);
        n.setProperty(HPropName.NAME, NElement.ofString(name));
        n.setProperty(HPropName.VALUE, value);
        return n;
    }

    protected HNode processInheritance(HNode node, HDocumentLoadingResultImpl result) {
        String[] t = node.getAncestors();
        if (t.length > 0) {
            Set<String> newAncestors = new HashSet<>(Arrays.asList(t));
            HProperties inheritedProps = new HProperties();
            List<HNode> inheritedChildren = new ArrayList<>();
            List<HStyleRule> inheritedRules = new ArrayList<>();
            List<HNode> ancestorsList = new ArrayList<>();
            for (String a : t) {
                prepareInheritanceSingle(a, node, result, newAncestors, ancestorsList, inheritedChildren, inheritedProps, inheritedRules);
            }
            node.setProperty(HPropName.ANCESTORS, NElement.ofStringArray(newAncestors.toArray(new String[0])));
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
                for (HNode child : node.children()) {
                    HResource source = computeSource(child);
                    child = child.copy();
                    child.setSource(source);
                    inheritedChildren.add(child);
                }
                node.setChildren(inheritedChildren.toArray(new HNode[0]));
            }
            //return node;
        }
        List<HNode> children = node.children();
        for (int i = 0; i < children.size(); i++) {
            HNode child = children.get(i);
            HNode child2 = processInheritance(child, result);
            if (child2 != child) {
                node.setChildAt(i, child2);
            }
        }
        return node;
    }

    protected HNode findAncestor(HNode node, String name, HProp... args) {
        HNodeDef ancestor0 = findAncestorDefinition(node, name);
        HNode[] body = ancestor0.body();
        if (body.length == 0) {
            return new DefaultHNode(HNodeType.VOID);
        }
        for (int i = 0; i < body.length; i++) {
            //apply args
            body[i] = applyArgs(body[i], args);
        }
        if (body.length == 1) {
            return body[0];
        }
        DefaultHNode stack = new DefaultHNode(HNodeType.STACK);
        stack.addAll(body);
        return stack;
    }

    private HNode applyArgs(HNode hNode, HProp[] args) {
        // TODO
        return hNode.copy();
    }

    protected HNodeDef findAncestorDefinition(HNode node, String name) {
        HNodeDef temp = null;
        HNode parent = node.parent();
        String finalName = net.thevpc.ndoc.api.util.HUtils.uid(name);
        while (parent != null) {
            List<HNodeDef> r = new ArrayList<>();
            for (HNodeDef x : parent.definitions()) {
                if (Objects.equals(HUtils.uid(x.name()), finalName)) {
                    r.add(x);
                }
            }
            if (r.size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("too many templates : ").append(finalName);
                for (int i = 0; i < r.size(); i++) {
                    HNodeDef hNode = r.get(i);
                    HResource n = hNode.source();
                    sb.append("\n\t[").append(n).append("] (").append(i + 1).append("/").append(r.size()).append(") : ").append(net.thevpc.ndoc.api.util.HUtils.strSnapshot(hNode));
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

    public HResource computeSource(HNode node) {
        while (node != null) {
            HResource s = node.source();
            if (s != null) {
                return s;
            }
            node = node.parent();
        }
        return null;
    }

    public HNode[] compilePage(HNode p) {
        return new HNode[]{p};
    }

    private static class MyNDocNodeFlowControlProcessorContext implements NDocNodeFlowControlProcessorContext {
        private NDocument document;
        private HLogger messages;

        public MyNDocNodeFlowControlProcessorContext(NDocument document, HLogger messages) {
            this.document = document;
            this.messages = messages;
        }

        @Override
        public NElement evalExpression(HNode node, NElement expression) {
            NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(node);
            return ne.eval(NElemUtils.toElement(expression));
        }

        @Override
        public NElement resolveVarValue(HNode node, String varName) {
            return evalExpression(node, NElement.ofName("$" + varName));
        }
    }
}
