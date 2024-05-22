package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.engine.nodes.container.AbstractHContainer;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

import java.util.*;

public abstract class AbstractHNode implements HNode {
    private Object source;
    private String name;
    private boolean template;
    private boolean disabled;
    protected HNode parent;
    protected Set<String> classNames = new HashSet<>();
    private HStyleMap styles = new HStyleMap();

    public Object getSource() {
        return source;
    }

    @Override
    public List<HStyle> styles() {
        return new ArrayList<>(styles.styles());
    }

    public HNode setSource(Object source) {
        this.source = source;
        return this;
    }

    @Override
    public boolean isTemplate() {
        return template;
    }

    @Override
    public HNode setTemplate(boolean template) {
        this.template = template;
        return this;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public HNode setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public String name() {
        return name;
    }

    @Override
    public HNode setName(String name) {
        this.name = name;
        return this;
    }


    public NOptional<HStyle> getStyle(HStyleType s) {
        return styles.get(s);
    }

    public NOptional<HStyle> computeStyle(HStyleType s) {
        return computeStyleMagnetude(s).map(HStyleAndMagnitude::getStyle);
    }

    public NOptional<HStyleAndMagnitude> computeStyleMagnetude(HStyleType s) {
        NOptional<HStyle> u = styles.get(s);
        if (u.isPresent()) {
            return NOptional.of(
                    new HStyleAndMagnitude(
                            u.get(),
                            new HStyleMagnitude(0, 1, DefaultHNodeSelector.ofImportant())
                    )
            );
        }
        HNode p = parent();
        int distance = 1;
        while (p != null) {
            if (p instanceof HContainer) {
                HStyleRule[] rules = ((AbstractHContainer) p).rules();
                HStyleMagnitude bestMag = null;
                HStyle bestStyle = null;
                for (HStyleRule rule : rules) {
                    HStyleRuleResult ss = rule.styles(this);
                    if (ss.isValid()) {
                        Set<HStyle> ch = ss.value();
                        HStyle u1 = ch.stream().filter(x -> x.getName() == s).findAny().orElse(null);
                        if (u1 != null) {
                            if (bestMag == null || ss.magnitude().compareTo(bestMag) <= 0) {
                                bestMag = ss.magnitude();
                                bestStyle = u1;
                            }
                        }
                    }
                }
                if (bestMag != null) {
                    return NOptional.of(
                            new HStyleAndMagnitude(
                                    bestStyle,
                                    new HStyleMagnitude(
                                            distance,
                                            bestMag.getSupportLevel(),
                                            bestMag.getSelector()
                                    )
                            )
                    );
                }
            }
            distance++;
            p = p.parent();
        }
        return NOptional.ofNamedEmpty("no style : " + s);
    }


    public HNode set(HStyle s) {
        styles.set(s);
        return this;
    }

    public HNode unset(HStyleType s) {
        styles.unset(s);
        return this;
    }

    @Override
    public HNode parent() {
        return parent;
    }


    @Override
    public HNode addClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            classNames.add(className);
        }
        return this;
    }

    @Override
    public HNode addClasses(String... classNames) {
        if (classNames != null) {
            for (String c : classNames) {
                addClass(c);
            }
        }
        return this;
    }

    private static String validateClassName(String className) {
        className = NStringUtils.trimToNull(className);
        if (className != null) {
            className = NNameFormat.LOWER_SNAKE_CASE.format(className);
        }
        return className;
    }

    @Override
    public HNode removeClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            classNames.remove(className);
        }
        return this;
    }

    @Override
    public boolean hasClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            return classNames.contains(className);
        }
        return false;
    }

    @Override
    public Set<String> classes() {
        return new HashSet<>(classNames);
    }

    @Override
    public void setParent(HNode parent) {
        this.parent = parent;
    }
}
