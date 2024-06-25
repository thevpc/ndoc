package net.thevpc.halfa.engine.impl;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.document.HDocumentLoadingResult;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.engine.parser.HDocumentLoadingResultImpl;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.*;
import java.util.stream.Collectors;

public class HDocumentCompiler {

    private HEngine engine;
    private NSession session;

    public HDocumentCompiler(HEngine engine, NSession session) {
        this.engine = engine;
        this.session = session;
    }

    public HDocumentLoadingResult compile(HDocument document, HMessageList messages) {
        HDocumentLoadingResultImpl result = new HDocumentLoadingResultImpl(engine.computeSource(document.root()), messages, session);
        result.setDocument(document);
        HNode root = document.root();
        processUuid(root);
        root = processInheritance(root, result);
        root = removeTemplates(root, result);
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

    protected HNode removeTemplates(HNode node, HDocumentLoadingResultImpl result) {
        List<HNode> children = node.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            HNode child = children.get(i);
            if (child.isTemplate()) {
                children.remove(i);
            } else {
                removeTemplates(child, result);
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
            result.messages().addError(NMsg.ofC("ancestor %s is invalid for %s : %s", a, HUtils.strSnapshot(node), ex),
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
            result.messages().addMessage(HMessageType.WARNING, NMsg.ofC("ancestor not found '%s' for %s", a,
                            HUtils.strSnapshot(node)
                    ), null,
                    engine.computeSource(node)
            );
            //throw new IllegalArgumentException("ancestor not found " + a + " for " + node);
        }
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
            node.setProperty(HPropName.ANCESTORS, newAncestors.toArray(new String[0]));
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
        String finalName = HUtils.uid(name);
        while (parent != null) {
            List<HNode> r = new ArrayList<>();
            for (HNode x : parent.children()) {
                if(x.isTemplate()) {
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
                    sb.append("\n\t[").append(n).append("] (").append(i + 1).append("/").append(r.size()).append(") : ").append(HUtils.strSnapshot(hNode));
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
}
