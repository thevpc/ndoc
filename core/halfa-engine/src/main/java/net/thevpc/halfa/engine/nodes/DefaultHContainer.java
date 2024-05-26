/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.DefaultHStyleRule;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.tson.TsonElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class DefaultHContainer extends DefaultHNode implements HContainer {

    private List<HNode> children=new ArrayList<>();
    private List<HStyleRule> styleRules = new ArrayList<>();

    public DefaultHContainer(String type) {
        super(type);
    }

    @Override
    public HContainer addAll(HNode... a) {
        if(a!=null) {
            for (HNode n : a) {
                add(n);
            }
        }
        return this;
    }

    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HNode) {
                if (other instanceof HContainer) {
                    HContainer hc = (HContainer) other;
                    addRules(hc.rules());
                    for (HNode child : hc.children()) {
                        add(child);
                    }
                }
            }

        }
    }

    @Override
    public boolean append(HItem a) {
        if (a != null) {
            boolean b = super.append(a);
            if (a instanceof HStyleRule) {
                addRule((HStyleRule) a);
                return true;
            }
            if (a instanceof HNode) {
                add((HNode) a);
                return true;
            }
            return b;
        }
        return false;
    }

    @Override
    public HContainer setName(String name) {
        super.setName(name);
        return this;
    }

    @Override
    public HContainer add(HNode a) {
        if (a != null) {
            children.add(a);
            a.setParent(this);
        }
        return this;
    }

    @Override
    public List<HNode> children() {
        return children;
    }

    @Override
    public HContainer setProperty(HProp s) {
        super.setProperty(s);
        return this;
    }

    @Override
    public HContainer unsetProperty(String s) {
        super.unsetProperty(s);
        return this;
    }

    @Override
    public HContainer set(HProp... styles) {
        if (styles != null) {
            for (HProp s : styles) {
                this.setProperty(s);
            }
        }
        return this;
    }

    @Override
    public HContainer addRule(HStyleRule s) {
        if (s != null) {
            styleRules.add(s);
        }
        return this;
    }

    @Override
    public HContainer addRules(HStyleRule... rules) {
        if (rules != null) {
            for (HStyleRule rule : rules) {
                addRule(rule);
            }
        }
        return this;
    }

    @Override
    public HContainer removeRule(HStyleRule s) {
        styleRules.remove(s);
        return this;
    }

    @Override
    public HContainer clearRules() {
        for (HStyleRule rule : rules()) {
            removeRule(rule);
        }
        return this;
    }

    @Override
    public HStyleRule[] rules() {
        return styleRules.toArray(new HStyleRule[0]);
    }

    @Override
    public HContainer addRule(HProp... s) {
        if (s != null) {
            Set<HProp> ss = Arrays.stream(s).filter(x -> x != null).collect(Collectors.toSet());
            if (!ss.isEmpty()) {
                addRule(DefaultHStyleRule.ofAny(s));
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return type() + "{" +
                "children=" + children +
                '}';
    }
}
