/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.container;

import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.DefaultHStyleRule;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.TsonSer;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public abstract class AbstractHContainer extends AbstractHNode implements HContainer {

    private List<HNode> children;
    private List<HStyleRule> styleRules = new ArrayList<>();

    public AbstractHContainer() {
        this(null);
    }

    public AbstractHContainer(List<HNode> children) {
        this.children = new ArrayList<>();
        if (children != null) {
            for (HNode child : children) {
                add(child);
            }
        }
        this.set(HStyles.origin(HAlign.TOP_LEFT))
                .set(HStyles.size(100, 100));
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
    public HContainer set(HStyle s) {
        super.set(s);
        return this;
    }

    @Override
    public HContainer unset(HStyleType s) {
        super.unset(s);
        return this;
    }

    @Override
    public HContainer set(HStyle... styles) {
        if (styles != null) {
            for (HStyle s : styles) {
                set(s);
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
    public HContainer addRule(HStyle... s) {
        if (s != null) {
            Set<HStyle> ss = Arrays.stream(s).filter(x -> x != null).collect(Collectors.toSet());
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


    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this).build();
    }
}
