package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.document.HDocumentLoadingResult;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.api.util.TsonUtils;
import net.thevpc.halfa.engine.control.IfHNodeFlowControlProcessorFactory;
import net.thevpc.halfa.engine.parser.HDocumentLoadingResultImpl;
import net.thevpc.halfa.spi.HNodeFlowControlProcessor;
import net.thevpc.halfa.spi.HNodeFlowControlProcessorContext;
import net.thevpc.halfa.spi.base.model.DefaultHNode;
import net.thevpc.halfa.spi.eval.HNodeEval;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.*;

import java.util.*;
import java.util.stream.Collectors;

public class HDocumentCompiler {

    private HEngine engine;
    private HMessageList messages;
    private IfHNodeFlowControlProcessorFactory flowControlProcessorFactory = new IfHNodeFlowControlProcessorFactory();

    public HDocumentCompiler(HEngine engine, HMessageList messages) {
        this.engine = engine;
        this.messages = messages;
    }

    public HDocumentLoadingResult compile(HDocument document) {
        HDocumentLoadingResultImpl result = new HDocumentLoadingResultImpl(engine.computeSource(document.root()), messages);
        result.setDocument(document);
        HNode root = document.root();
        processUuid(root);
        root = processCalls(root, result);
        root = processInheritance(root, result);
        root = processControlFlow(root, result, new MyHNodeFlowControlProcessorContext(document, messages));
        root = removeDeclarations(root, result);
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
                        if (c.isTemplate()) {
                            //just ignore
                            //newChildren.add(c);
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

    protected HNode processControlFlow(HNode root, HDocumentLoadingResultImpl result, HNodeFlowControlProcessorContext context) {
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

    protected HNode[] processControlFlowCurrent(HNode[] nodes, HDocumentLoadingResultImpl result, HNodeFlowControlProcessorContext context) {
        List<HNode> curr = new ArrayList<>(Arrays.asList(nodes));
        for (HNodeFlowControlProcessor a : flowControlProcessorFactory.list()) {
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

    protected HNode removeDeclarations(HNode node, HDocumentLoadingResultImpl result) {
        List<HNode> children = node.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            HNode child = children.get(i);
            if (child.isTemplate() || HNodeType.DEFINE.equals(child.type())) {
                children.remove(i);
            } else {
                removeDeclarations(child, result);
            }
        }
        return node;
    }

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
            result.messages().addError(NMsg.ofC("invalid ancestor %s for %s : %s", a, net.thevpc.halfa.api.util.HUtils.strSnapshot(node), ex),
                    null,
                    engine.computeSource(node)
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
            result.messages().addMessage(HMessageType.WARNING, NMsg.ofC("missing ancestor '%s' for %s", a,
                            net.thevpc.halfa.api.util.HUtils.strSnapshot(node)
                    ), null,
                    engine.computeSource(node)
            );
            //throw new IllegalArgumentException("ancestor not found " + a + " for " + node);
        }
    }

    protected HNode processCalls(HNode node, HDocumentLoadingResultImpl result) {
        if (HNodeType.CALL.equals(node.type())) {
            String uid = node.getName();
            TsonElement callDeclaration = node.getPropertyValue(HPropName.VALUE).get();
            //                    String uid = HUtils.uid(ee.name());

            HNode currNode = node.parent();
            while (currNode != null) {
                for (HNode objectDefNode : currNode.children()) {
                    if (HNodeType.DEFINE.equals(objectDefNode.type()) && uid.equals(net.thevpc.halfa.api.util.HUtils.uid(objectDefNode.getName()))) {
                        return inlineNodeDefinitionCall(objectDefNode, callDeclaration);
                    }
                }
                currNode = currNode.parent();
            }

        }
        List<HNode> newChildren=new ArrayList<>();
        for (HNode child : node.children()) {
            newChildren.add(processCalls(child, result));
        }
        node.setChildren(newChildren.toArray(new HNode[0]));
        return node;
    }

    private HNode inlineNodeDefinitionCall(HNode objectDefNode, TsonElement callFunction) {
        HNode inlinedNode = new DefaultHNode(HNodeType.STACK);
        TsonArray objectDefArgsItem = (TsonArray) objectDefNode.getPropertyValue("args").orNull();
        TsonElement[] objectDefArgs = objectDefArgsItem==null?new TsonElement[0] :objectDefArgsItem.body().toArray();
        inlinedNode.setSource(objectDefNode.source());
        inlinedNode.setStyleClasses(objectDefNode.getStyleClasses());
        inlinedNode.setProperties(objectDefNode.getProperties().stream().filter(x ->
                HPropName.NAME.equals(x.getName())
                        && !HPropName.ARGS.equals(x.getName())
        ).toArray(HProp[]::new));
        inlinedNode.setRules(objectDefNode.rules());
        TsonElementList passedArgs = callFunction.toContainer().args();
        TsonElement[] passedArgsArr = passedArgs == null ? new TsonElement[0] : passedArgs.toList().toArray(new TsonElement[0]);
        inlinedNode.children().add(newAssign(HPropName.ARGS, Tson.ofArray(passedArgsArr).build()));
        for (int i = 0; i < passedArgsArr.length; i++) {
            TsonElement passedArg = passedArgsArr[i];
            if (passedArg.isSimplePair()) {
                TsonPair pair = passedArg.toPair();
                TsonElement value = pair.value();
                if(HUtils.getCompilerDeclarationPath(value)==null){
                    value=HUtils.addCompilerDeclarationPath(value,HUtils.getCompilerDeclarationPath(pair));
                }
                inlinedNode.children().add(newAssign(pair.key().toStr().stringValue(), value));
            } else {
                if (i < objectDefArgs.length) {
                    String paramName = objectDefArgs[i].toStr().stringValue();
                    inlinedNode.children().add(newAssign(paramName, passedArg));
                } else {
                    NMsg message = NMsg.ofC("[%s] invalid index %s for %s in %s", net.thevpc.halfa.api.util.HUtils.shortName(objectDefNode.source()), (i + 1), objectDefNode.getName(), callFunction);
                    messages.addError(message, objectDefNode.source());
                    throw new NIllegalArgumentException( message);
                }
            }
        }
        //n.setParent(context.node().parent());
        inlinedNode.setParent(objectDefNode.parent());
        if(objectDefNode.children()!=null) {
            for (HNode newBodyElement : objectDefNode.children()) {
                inlinedNode.children().add(newBodyElement.copy());
            }
        }
//        inlinedNode.toString();
        return inlinedNode;
    }

    private HNode newAssign(String name, TsonElement value) {
        HNode n = new DefaultHNode(HNodeType.ASSIGN);
        n.setProperty(HPropName.NAME, Tson.of(name));
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
            node.setProperty(HPropName.ANCESTORS, Tson.of(newAncestors.toArray(new String[0])));
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

    protected HNode findAncestor(HNode node, String name) {
        HNode temp = null;
        HNode parent = node.parent();
        String finalName = net.thevpc.halfa.api.util.HUtils.uid(name);
        while (parent != null) {
            List<HNode> r = new ArrayList<>();
            for (HNode x : parent.children()) {
                if (x.isTemplate()) {
                    if (Objects.equals(HUtils.uid(x.getName()), finalName)) {
                        r.add(x);
                    }
                }
            }
            if (r.size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("too many templates : ").append(finalName);
                for (int i = 0; i < r.size(); i++) {
                    HNode hNode = r.get(i);
                    HResource n = engine.computeSource(hNode);
                    sb.append("\n\t[").append(n).append("] (").append(i + 1).append("/").append(r.size()).append(") : ").append(net.thevpc.halfa.api.util.HUtils.strSnapshot(hNode));
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

    private static class MyHNodeFlowControlProcessorContext implements HNodeFlowControlProcessorContext {
        private HDocument document;
        private HMessageList messages;

        public MyHNodeFlowControlProcessorContext(HDocument document, HMessageList messages) {
            this.document = document;
            this.messages = messages;
        }

        @Override
        public TsonElement evalExpression(HNode node, TsonElement expression) {
            HNodeEval ne = new HNodeEval(node);
            return ne.eval(TsonUtils.toTson(expression));
        }

        @Override
        public TsonElement resolveVarValue(HNode node, String varName) {
            return evalExpression(node, Tson.name("$" + varName));
        }
    }
}
