package net.thevpc.ndoc.spi.base.model;

import net.thevpc.ndoc.api.model.node.HItemList;
import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.elem2d.HAlign;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.style.*;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;
import net.thevpc.nuts.util.*;
import net.thevpc.tson.*;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultHNode implements HNode {
    private String uuid;
    private String nodeType;
    private HResource source;
    protected HNode parent;
    private HProperties properties = new HProperties();
    private Map<String,NElement> vars = new LinkedHashMap<>();
    private List<HNode> children = new ArrayList<>();
    private List<HStyleRule> styleRules = new ArrayList<>();

    public DefaultHNode(String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public HNode setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public String type() {
        return this.nodeType;
    }

    public String[] getAncestors() {
        NOptional<NElement> style = getPropertyValue(HPropName.ANCESTORS);
        if (style.isEmpty()) {
            return new String[0];
        }
        NElement v = style.get();
        if (v == null) {
            return new String[0];
        }
        String[] v1 = NDocObjEx.of(v).asStringArray().orNull();
        if (v1 == null) {
            return new String[0];
        }
        return Arrays.stream(v1).filter(x -> !NBlankable.isBlank(x))
                .map(net.thevpc.ndoc.api.util.HUtils::uid)
                .distinct()
                .toArray(String[]::new)
                ;
    }

    @Override
    public String[] getStyleClasses() {
        NOptional<NElement> style = getPropertyValue(HPropName.CLASS);
        if (style.isEmpty()) {
            return new String[0];
        }
        String[] v = NDocObjEx.of(style.get()).asStringArrayOrString().orNull();
        if (v == null) {
            return new String[0];
        }
        return Arrays.stream(v).filter(x -> !NBlankable.isBlank(x))
                .map(HUtils::uid)
                .distinct()
                .toArray(String[]::new)
                ;
    }

    public DefaultHNode setAncestors(String[] parentTemplate) {
        setProperty(HProps.ancestors(parentTemplate));
        return this;
    }

    public HResource source() {
        return source;
    }


    @Override
    public List<HProp> props() {
        return new ArrayList<>(properties.toSet());
    }

    public HNode setSource(HResource source) {
        this.source = source;
        return this;
    }

    @Override
    public boolean isTemplate() {
        NOptional<NElement> style = getPropertyValue(HPropName.TEMPLATE);
        if (style.isEmpty()) {
            return false;
        }
        NElement v = style.get();
        if (v == null) {
            return false;
        }
        NDocObjEx oo = NDocObjEx.of(v);
        if (oo.isBoolean()) {
            return oo.asBoolean().get();
        }
        if (oo.isStringOrName()) {
            return true;
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
        NOptional<NElement> style = getPropertyValue(HPropName.HIDE);
        if (style.isEmpty()) {
            return false;
        }
        NElement v = style.get();
        if (v == null) {
            return false;
        }
        return v.booleanValue();
    }

    @Override
    public HNode setDisabled(boolean disabled) {
        setProperty(HProps.template(disabled));
        return this;
    }

    public String name() {
        NOptional<NElement> style = getPropertyValue(HPropName.NAME);
        if (style.isEmpty()) {
            return null;
        }
        NElement v = style.get();
        if (v == null) {
            return null;
        }
        return v.toStr().value();
    }

    @Override
    public HNode setName(String name) {
        setProperty(HProps.name(name));
        return this;
    }

    @Override
    public NOptional<NElement> getPropertyValue(String... propertyNames) {
        return getProperty(propertyNames).map(x -> x.getValue());
    }

    @Override
    public NOptional<NElement> getVar(String property) {
        return NOptional.ofNamed(vars.get(property),property);
    }

    public NOptional<HProp> getProperty(String... propertyNames) {
        return properties.get(propertyNames);
    }

    @Override
    public HNode setProperty(String name, NElement value) {
        properties.set(name, value);
        return this;
    }

    @Override
    public HNode setProperty(String name, NToElement value) {
        properties.set(name, value == null ? null : value.toElement());
        return this;
    }

    @Override
    public HNode setVar(String name, NElement value) {
        if(value==null){
            vars.remove(name);
        }else{
            vars.put(name, value);
        }
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
        NOptional<HProp> y = properties.get(HPropName.CLASS);
        if (y.isPresent()) {
            return new LinkedHashSet<>(Arrays.asList(NDocObjEx.of(y.get().getValue()).asStringArray().orElse(new String[0])).stream()
                    .filter(x -> x != null && x.trim().length() > 0)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }
        return new HashSet<>();
    }

    @Override
    public HNode addStyleClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            Set<String> s = _oldClassNames();
            s.add(className);
            setProperty(HProps.styleClasses(s.toArray(new String[0])));
        }
        return this;
    }

    @Override
    public HItem setStyleClasses(String[] classNames) {
        Set<String> s = new HashSet<>();
        if (classNames != null) {
            for (String c : classNames) {
                c = validateClassName(c);
                if (c != null) {
                    s.add(c);
                }
            }
        }
        setProperty(HProps.styleClasses(s.toArray(new String[0])));
        return this;
    }

    @Override
    public HNode addStyleClasses(String... classNames) {
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
    public HNode setParent(HNode parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public HNode mergeNode(HItem other) {
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
                addRules(hn.rules());
                for (HNode child : hn.children()) {
                    add(child);
                }
            }
        }
        return this;
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
            } else if (a instanceof HProp) {
                setProperty((HProp) a);
                return true;
            } else if (a instanceof HStyleRule) {
                addRule((HStyleRule) a);
                return true;
            } else if (a instanceof HNode) {
                add((HNode) a);
                return true;
            }
        }
        return false;
    }

    @Override
    public HNode setPosition(HAlign align) {
        setProperty(HPropName.POSITION, align);
        return this;
    }

    @Override
    public HNode setPosition(Number x, Number y) {
        setPosition(new Double2(x, y));
        return this;
    }

    @Override
    public HNode setPosition(Double2 d) {
        setProperty(HPropName.POSITION, d);
        return this;
    }

    @Override
    public HNode setOrigin(HAlign align) {
        setProperty(HPropName.ORIGIN, align);
        return this;
    }

    @Override
    public HNode setOrigin(Number x, Number y) {
        setOrigin(new Double2(x, y));
        return this;
    }

    @Override
    public HNode setOrigin(Double2 d) {
        setProperty(HPropName.ORIGIN, d);
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
        setPosition(x, y);
        setOrigin(x, y);
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
        return setSize(new Double2(size, size));
    }

    @Override
    public HNode setSize(Double2 size) {
        NElement old = getPropertyValue(HPropName.SIZE).orNull();
        Double2 oo = old == null ? null : NDocObjEx.of(old).asDouble2().orNull();
        if (oo == null) {
            oo = new Double2(null, null);
        }
        Number w = size == null ? null : size.getX();
        Number h = size == null ? null : size.getY();
        setProperty(HPropName.SIZE, new Double2(
                NUtils.firstNonNull(w, oo.getX()),
                NUtils.firstNonNull(h, oo.getY())
        ));
        return this;
    }

    @Override
    public HNode setSize(Number w, Number h) {
        setSize(new Double2(w, h));
        return this;
    }

    @Override
    public HNode setFontSize(Number w) {
        setProperty(HPropName.FONT_SIZE, NElements.of().of(w));
        return this;
    }

    @Override
    public HNode setFontFamily(String w) {
        setProperty(HPropName.FONT_FAMILY, NElements.of().of(w));
        return this;
    }

    @Override
    public HNode setFontBold(Boolean w) {
        setProperty(HPropName.FONT_BOLD, NElements.of().of(w));
        return this;
    }

    @Override
    public HNode setFontItalic(Boolean w) {
        setProperty(HPropName.FONT_ITALIC, NElements.of().of(w));
        return this;
    }

    @Override
    public HNode setFontUnderlined(Boolean w) {
        setProperty(HPropName.FONT_UNDERLINED, NElements.of().of(w));
        return this;
    }

    @Override
    public HNode setFontStrike(Boolean w) {
        setProperty(HPropName.FONT_STRIKE, NElements.of().of(w));
        return this;
    }

    @Override
    public HNode setForegroundColor(String w) {
        setProperty(HPropName.FOREGROUND_COLOR, NElements.of().of(w));
        return this;
    }

    @Override
    public HNode setBackgroundColor(String w) {
        setProperty(HPropName.BACKGROUND_COLOR, NElements.of().of(w));
        return this;
    }

//    @Override
//    public HNode setLineColor(String w) {
//        setProperty(HPropName.LINE_COLOR, w);
//        return this;
//    }

    @Override
    public HNode setGridColor(String w) {
        setProperty(HPropName.GRID_COLOR, NElements.of().of(w));
        return this;
    }

    @Override
    public List<HProp> getProperties() {
        return new ArrayList<>(properties.toSet());
    }

    @Override
    public HNode setProperties(HProp... props) {
        if (props != null) {
            for (HProp prop : props) {
                setProperty(prop);
            }
        }
        return this;
    }

    @Override
    public HNode setChildren(HNode... children) {
        this.children.clear();
        addAll(children);
        return this;
    }

    @Override
    public HNode addAll(HNode... a) {
        if (a != null) {
            for (HNode n : a) {
                add(n);
            }
        }
        return this;
    }

    @Override
    public HNode add(HNode a) {
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
    public HNode addRule(HStyleRule s) {
        if (s != null) {
            styleRules.add(s);
        }
        return this;
    }

    @Override
    public HNode setRules(HStyleRule[] rules) {
        styleRules.clear();
        addRules(rules);
        return this;
    }

    @Override
    public HNode addRules(HStyleRule... rules) {
        if (rules != null) {
            for (HStyleRule rule : rules) {
                addRule(rule);
            }
        }
        return this;
    }

    @Override
    public HNode removeRule(HStyleRule s) {
        styleRules.remove(s);
        return this;
    }

    @Override
    public HNode clearRules() {
        for (HStyleRule rule : rules()) {
            removeRule(rule);
        }
        return this;
    }

    @Override
    public HStyleRule[] rules() {
        return styleRules.toArray(new HStyleRule[0]);
    }

    public HNode copy() {
        DefaultHNode o = new DefaultHNode(nodeType);
        o.setUuid(UUID.randomUUID().toString());
        o.setSource(source());
        o.setProperties(properties.toArray());
        o.addAll(children().stream().map(HNode::copy).toArray(HNode[]::new));
        o.addRules(Arrays.stream(rules()).toArray(HStyleRule[]::new));
        return o;
    }

    //    @Override
//    public HNode addRule(HProp... s) {
//        if (s != null) {
//            Set<HProp> ss = Arrays.stream(s).filter(x -> x != null).collect(Collectors.toSet());
//            if (!ss.isEmpty()) {
//                addRule(DefaultHStyleRule.ofAny(s));
//            }
//        }
//        return this;
//    }
//
    private NElement toTson0() {
        if (HNodeType.ASSIGN.equals(type())) {
            return Tson.ofPair("$" + getName(), getPropertyValue(HPropName.VALUE).orNull());
        }
        String[] a = getAncestors();
        String[] styleClasses = getStyleClasses();
        if (!styleRules.isEmpty() || !children.isEmpty()) {
            TsonObjectBuilder o = Tson.ofObjectBuilder(nodeType);
            if (a.length > 0) {
                for (int i = 0; i < a.length; i++) {
                    String s = a[i];
                    if (i == a.length - 1) {
                        o.annotation(s, Arrays.stream(styleClasses).map(x -> Tson.ofString(x)).toArray(TsonElementBase[]::new));
                    } else {
                        o.annotation(s);
                    }
                }
            } else if (styleClasses.length > 0) {
                o.annotation(null, Arrays.stream(styleClasses).map(x -> Tson.ofString(x)).toArray(TsonElementBase[]::new));
            }
            if (source != null) {
                o.add(Tson.ofPair("source", Tson.ofString(source.shortName())));
            }
            for (HProp p : properties.toList()) {
                switch (p.getName()) {
                    case HPropName.ANCESTORS:
                    case HPropName.CLASS: {
                        break;
                    }
                    default: {
                        o.addParam(p.toElement());
                    }
                }
            }
            if (!styleRules.isEmpty()) {
                o.add("rules", Tson.ofObjectBuilder(styleRules.stream().map(x -> x.toElement()).toArray(TsonElementBase[]::new)));
            }
            for (HNode child : children()) {
                if (child instanceof DefaultHNode) {
                    o.add(((DefaultHNode) child).toTson0());
                } else {
                    o.add(Tson.ofString(child.toString()));
                }
            }
            return o.build();
        } else {
            TsonUpletBuilder o = Tson.ofUpletBuilder().name(nodeType);
            if (a.length > 0) {
                for (int i = 0; i < a.length; i++) {
                    String s = a[i];
                    if (i == a.length - 1) {
                        o.annotation(s, Arrays.stream(styleClasses).map(x -> Tson.ofString(x)).toArray(TsonElementBase[]::new));
                    } else {
                        o.annotation(s);
                    }
                }
            } else if (styleClasses.length > 0) {
                o.annotation(null, Arrays.stream(styleClasses).map(x -> Tson.ofString(x)).toArray(TsonElementBase[]::new));
            }
            if (source != null) {
                o.add(Tson.ofPair("source", Tson.ofString(String.valueOf(source))));
            }
            for (HProp p : properties.toList()) {
                switch (p.getName()) {
                    case HPropName.ANCESTORS:
                    case HPropName.CLASS: {
                        break;
                    }
                    default: {
                        o.add(p.toElement());
                    }
                }
            }
            return o.build();
        }
    }

    @Override
    public String toString() {
        return toTson0().toString();
    }

    @Override
    public String getName() {
        String n = NDocObjEx.of(getPropertyValue(HPropName.NAME).orNull()).asStringOrName().orNull();
        if (n != null) {
            return n;
        }
        //check if It's a template, and return template name
        NDocObjEx template = NDocObjEx.of(getPropertyValue(HPropName.TEMPLATE).orNull());
        if (template.isStringOrName()) {
            return template.asStringOrName().get();
        }
        return null;
    }

    @Override
    public void setChildAt(int i, HNode c) {
        NAssert.requireNonNull(c, "node");
        children.set(i, c);
    }
}
