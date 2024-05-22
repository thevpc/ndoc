package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.node.HItem;
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
    protected HNode parent;
    private HStyleMap styles = new HStyleMap();

    public String getParentTemplate() {
        NOptional<HStyle> style = getStyle(HStyleType.TEMPLATE_NAME);
        if (style.isEmpty()) {
            return null;
        }
        Object v = style.get().getValue();
        if (v == null) {
            return null;
        }
        return (String) v;
    }


    public AbstractHNode setParentTemplate(String parentTemplate) {
        set(HStyles.templateName(parentTemplate));
        return this;
    }

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
        NOptional<HStyle> style = getStyle(HStyleType.TEMPLATE);
        if (style.isEmpty()) {
            return false;
        }
        Object v = style.get().getValue();
        if (v == null) {
            return false;
        }
        return true;
    }

    @Override
    public HNode setTemplate(boolean template) {
        set(HStyles.template(true));
        return this;
    }

    @Override
    public boolean isDisabled() {
        NOptional<HStyle> style = getStyle(HStyleType.DISABLED);
        if (style.isEmpty()) {
            return false;
        }
        Object v = style.get().getValue();
        if (v == null) {
            return false;
        }
        return true;
    }

    @Override
    public HNode setDisabled(boolean disabled) {
        set(HStyles.template(disabled));
        return this;
    }

    public String name() {
        NOptional<HStyle> style = getStyle(HStyleType.NAME);
        if (style.isEmpty()) {
            return null;
        }
        Object v = style.get().getValue();
        if (v == null) {
            return null;
        }
        return (String) v;
    }

    @Override
    public HNode setName(String name) {
        set(HStyles.name(name));
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


    private Set<String> _oldClassNames() {
        NOptional<HStyle> y = styles.get(HStyleType.STYLE_CLASS);
        Set<String> classNames = new HashSet<>();
        if (y.isPresent()) {
            Object o = y.get().getValue();
            if (o != null) {
                if (o instanceof String) {
                    String e = validateClassName((String) o);
                    if (e != null) {
                        classNames.add(e);
                    }
                } else if (o instanceof String[]) {
                    for (String s : (String[]) o) {
                        String e = validateClassName(s);
                        if (e != null) {
                            classNames.add(e);
                        }
                    }
                }
            }
            return classNames;
        }
        return new HashSet<>();
    }

    @Override
    public HNode addClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            Set<String> s = _oldClassNames();
            s.add(className);
            set(HStyles.styleClasses(s.toArray(new String[0])));
        }
        return this;
    }

    @Override
    public HNode addClasses(String... classNames) {
        if (classNames != null) {
            Set<String> s = _oldClassNames();
            for (String c : classNames) {
                c = validateClassName(c);
                if (c != null) {
                    s.add(c);
                }
            }
            set(HStyles.styleClasses(s.toArray(new String[0])));
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
            Set<String> s = _oldClassNames();
            s.remove(className);
            set(HStyles.styleClasses(s.toArray(new String[0])));
        }
        return this;
    }

    @Override
    public boolean hasClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            return _oldClassNames().contains(className);
        }
        return false;
    }

    @Override
    public Set<String> styleClasses() {
        return _oldClassNames();
    }

    @Override
    public void setParent(HNode parent) {
        this.parent = parent;
    }

    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            if (other instanceof HItemList) {
                for (HItem item : ((HItemList) other).getItems()) {
                    mergeNode(item);
                }
            } else if (other instanceof HStyle) {
                set((HStyle) other);
            } else if (other instanceof HNode) {
                HNode hn = (HNode) other;
                if (this.source == null) {
                    this.source = hn.getSource();
                }
                this.styles.set(hn.styles());
            }
        }
    }

    @Override
    public boolean append(HItem a) {
        if (a != null) {
            if (a instanceof HItemList) {
                boolean b = false;
                for (HItem item : ((HItemList) a).getItems()) {
                    b |= append(item);
                }
                return b;
            }
            if (a instanceof HStyle) {
                set((HStyle) a);
                return true;
            }
        }
        return false;
    }
}
