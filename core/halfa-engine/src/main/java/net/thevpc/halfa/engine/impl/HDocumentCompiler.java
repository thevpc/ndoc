package net.thevpc.halfa.engine.impl;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;

import java.util.*;
import java.util.stream.Collectors;

public class HDocumentCompiler {
    private HEngine engine;

    public HDocumentCompiler(HEngine engine) {
        this.engine = engine;
    }

    public HDocument compile(HDocument document) {
        HNode root = document.root();
        processUuid(root);
        root= processInheritance(root);
        processRootPages(root);
        return document;
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

    protected HNode processInheritance(HNode node) {
        String[] t = node.getAncestors();
        if (t.length > 0) {
            Set<String> newAncestors = new HashSet<>(Arrays.asList(t));
            HProperties inheritedProps=new HProperties();
            List<HNode> inheritedChildren=new ArrayList<>();
            List<HStyleRule> inheritedRules=new ArrayList<>();
            List<HNode> ancestorsList=new ArrayList<>();
            for (String a : t) {
                HNode aa = findAncestor(node, a);
                if (aa != null) {
                    newAncestors.remove(a);
                    for (HProp p : aa.getProperties()) {
                        switch (p.getName()){
                            case HPropName.NAME:
                            case HPropName.TEMPLATE:{
                                break;
                            }
                            default:{
                                inheritedProps.set(p);
                                break;
                            }
                        }
                    }
                    ancestorsList.add(aa);
                    for (HNode child : aa.children()) {
                        Object source = computeSource(child);
                        child=child.copy();
                        child.setSource(source);
                        inheritedChildren.add(child);
                    }
                    inheritedRules.addAll(Arrays.asList(aa.rules()));
                } else {
                    throw new IllegalArgumentException("ancestor not found " + a + " for " + node);
                }
            }
            node.setProperty(HPropName.ANCESTORS, newAncestors.toArray(new String[0]));
            for (HProp p : inheritedProps.toList()) {
                NOptional<HProp> u = node.getProperty(p.getName());
                if (!u.isPresent()) {
                    node.setProperty(p);
                }
            }
            if(!inheritedRules.isEmpty()){
                //must add them upfront
                inheritedRules.addAll(Arrays.asList(node.rules()));
                node.setRules(inheritedRules.toArray(new HStyleRule[0]));
            }
            if(!inheritedChildren.isEmpty()){
                for (HNode child : node.children()) {
                    Object source = computeSource(child);
                    child=child.copy();
                    child.setSource(source);
                    inheritedChildren.add(child);
                }
                node.setChildren(inheritedChildren.toArray(new HNode[0]));
            }
            return node;
        }
        List<HNode> children = node.children();
        for (int i = 0; i < children.size(); i++) {
            HNode child = children.get(i);
            HNode child2 = processInheritance(child);
            if (child2!=child){
                node.setChildAt(i,child2);
            }
        }
        return node;
    }

    protected HNode findAncestor(HNode node, String name) {
        HNode temp = null;
        HNode parent = node.parent();
        String finalName = HUtils.uid(name);
        while (parent != null) {
            List<HNode> r = parent.children().stream().filter(x -> x.isTemplate()
                    && Objects.equals(HUtils.uid(x.getName()), finalName)
            ).collect(Collectors.toList());
            if (r.size() > 1) {
                throw new IllegalArgumentException("too many templates : " + finalName + " : " + r);
            }
            if (r.size() == 1) {
                temp = r.get(0);
                break;
            }
            parent = parent.parent();
        }
        return temp;
    }



    public Object computeSource(HNode node) {
        while(node!=null) {
            Object s = node.source();
            if (s != null) {
                return s;
            }
            node = node.parent();
        }
        return null;
    }
}
