package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.nuts.util.NUtils;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class DefaultHNode implements HNode {
    private Object source;
    protected HNode parent;
    private HProperties properties = new HProperties();
    private String nodeType;

    public DefaultHNode(String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String type() {
        return this.nodeType;
    }

    public String getParentTemplate() {
        NOptional<HProp> style = getProperty(HPropName.EXTENDS);
        if (style.isEmpty()) {
            return null;
        }
        Object v = style.get().getValue();
        if (v == null) {
            return null;
        }
        return (String) v;
    }


    public DefaultHNode setParentTemplate(String parentTemplate) {
        setProperty(HProps.inherits(parentTemplate));
        return this;
    }

    public Object source() {
        return source;
    }

    @Override
    public Object computeSource() {
        Object s= source();
        if(s!=null){
            return s;
        }
        HNode p = parent();
        if(p!=null){
            return p.computeSource();
        }
        return null;
    }

    @Override
    public List<HProp> props() {
        return new ArrayList<>(properties.styles());
    }

    public HNode setSource(Object source) {
        this.source = source;
        return this;
    }

    @Override
    public boolean isTemplate() {
        NOptional<HProp> style = getProperty(HPropName.TEMPLATE);
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
        setProperty(HProps.template(true));
        return this;
    }

    @Override
    public boolean isDisabled() {
        NOptional<HProp> style = getProperty(HPropName.DISABLED);
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
        setProperty(HProps.template(disabled));
        return this;
    }

    public String name() {
        NOptional<HProp> style = getProperty(HPropName.NAME);
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
        setProperty(HProps.name(name));
        return this;
    }

    @Override
    public NOptional<Object> getPropertyValue(String styleType) {
        return getProperty(styleType).map(HProp::getValue);
    }

    public NOptional<HProp> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public NOptional<HProp> computeProperty(String propertyName) {
        return computePropertyMagnetude(propertyName).map(HStyleAndMagnitude::getStyle);
    }

    public NOptional<HStyleAndMagnitude> computePropertyMagnetude(String propertyName) {
        NOptional<HProp> u = properties.get(propertyName);
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
                HStyleRule[] rules = ((DefaultHContainer) p).rules();
                HStyleMagnitude bestMag = null;
                HProp bestStyle = null;
                for (HStyleRule rule : rules) {
                    HStyleRuleResult ss = rule.styles(this);
                    if (ss.isValid()) {
                        Set<HProp> ch = ss.value();
                        HProp u1 = ch.stream().filter(x -> x.getName() == propertyName).findAny().orElse(null);
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
        return NOptional.ofNamedEmpty("no style : " + propertyName);
    }

    @Override
    public HNode setProperty(String name, Object value) {
        properties.set(name,value);
        return this;
    }


    public HNode setProperty(HProp s) {
        properties.set(s);
        return this;
    }

    public HNode unsetProperty(String s) {
        properties.unset(s);
        return this;
    }

    @Override
    public HNode parent() {
        return parent;
    }


    private Set<String> _oldClassNames() {
        NOptional<HProp> y = properties.get(HPropName.STYLE_CLASS);
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
            setProperty(HProps.styleClasses(s.toArray(new String[0])));
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
            setProperty(HProps.styleClasses(s.toArray(new String[0])));
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
            setProperty(HProps.styleClasses(s.toArray(new String[0])));
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
            } else if (other instanceof HProp) {
                setProperty((HProp) other);
            } else if (other instanceof HNode) {
                HNode hn = (HNode) other;
                if (this.source == null) {
                    this.source = hn.source();
                }
                this.properties.set(hn.props());
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
            if (a instanceof HProp) {
                setProperty((HProp) a);
                return true;
            }
        }
        return false;
    }

    @Override
    public HNode setPosition(HAlign align) {
        setProperty(HPropName.POSITION,align);
        return this;
    }

    @Override
    public HNode setPosition(Number x, Number y) {
        setPosition(new Double2(x,y));
        return this;
    }

    @Override
    public HNode setPosition(Double2 d) {
        setProperty(HPropName.POSITION,d);
        return this;
    }

    @Override
    public HNode setOrigin(HAlign align) {
        setProperty(HPropName.POSITION,align);
        return this;
    }

    @Override
    public HNode setOrigin(Number x, Number y) {
        setOrigin(new Double2(x,y));
        return this;
    }

    @Override
    public HNode setOrigin(Double2 d) {
        setProperty(HPropName.POSITION,d);
        return this;
    }

    @Override
    public HNode at(HAlign align) {
        setPosition(align);
        setOrigin(align);
        return this;
    }

    @Override
    public HNode at(Number x, Number y) {
        setPosition(x,y);
        setOrigin(x,y);
        return this;
    }

    @Override
    public HNode at(Double2 d) {
        setPosition(d);
        setOrigin(d);
        return this;
    }

    @Override
    public HNode setSize(Number size) {
        return setSize(new Double2(size,size));
    }

    @Override
    public HNode setSize(Double2 size) {
        HProp old = getProperty(HPropName.SIZE).orNull();
        Double2 oo=old==null?null:(Double2) old.getValue();
        if(oo==null){
            oo=new Double2(null,null);
        }
        Number w=size==null?null:size.getX();
        Number h=size==null?null:size.getY();
        setProperty(HPropName.SIZE,new Double2(
                NUtils.firstNonNull(w,oo.getX()),
                NUtils.firstNonNull(h,oo.getY())
        ));
        return this;
    }

    @Override
    public HNode setSize(Number w,Number h) {
        setSize(new Double2(w,h));
        return this;
    }

    @Override
    public HNode setFontSize(Number w) {
        setProperty(HPropName.FONT_SIZE,w);
        return this;
    }

    @Override
    public HNode setFontFamily(String w) {
        setProperty(HPropName.FONT_FAMILY,w);
        return this;
    }

    @Override
    public HNode setFontBold(Boolean w) {
        setProperty(HPropName.FONT_BOLD,w);
        return this;
    }

    @Override
    public HNode setFontItalic(Boolean w) {
        setProperty(HPropName.FONT_ITALIC,w);
        return this;
    }

    @Override
    public HNode setFontUnderlined(Boolean w) {
        setProperty(HPropName.FONT_UNDERLINED,w);
        return this;
    }

    @Override
    public HNode setForegroundColor(String w) {
        setProperty(HPropName.FOREGROUND_COLOR,w);
        return this;
    }

    @Override
    public HNode setBackgroundColor(String w) {
        setProperty(HPropName.BACKGROUND_COLOR,w);
        return this;
    }

    @Override
    public HNode setLineColor(String w) {
        setProperty(HPropName.LINE_COLOR,w);
        return this;
    }
    @Override
    public HNode setGridColor(String w) {
        setProperty(HPropName.GRID_COLOR,w);
        return this;
    }
}
