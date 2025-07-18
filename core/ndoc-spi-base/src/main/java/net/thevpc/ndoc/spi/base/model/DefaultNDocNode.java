package net.thevpc.ndoc.spi.base.model;

import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.node.*;
import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.*;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.*;


import java.util.*;
import java.util.stream.Collectors;

public class DefaultNDocNode implements NDocNode {
    private String uuid;
    private String nodeType;
    private NDocResource source;
    protected NDocNode parent;
    private NDocProperties properties = new NDocProperties();
    private Map<String, NElement> vars = new LinkedHashMap<>();
    private List<NDocNode> children = new ArrayList<>();
    private List<HStyleRule> styleRules = new ArrayList<>();
    private List<NDocNodeDef> definitions = new ArrayList<>();
    private List<NDocFunction> functions = new ArrayList<>();

    public DefaultNDocNode(String nodeType) {
        this.nodeType = nodeType;
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public NDocNode setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public String type() {
        return this.nodeType;
    }


    @Override
    public String[] getStyleClasses() {
        NOptional<NElement> style = getPropertyValue(NDocPropName.CLASS);
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

    public NDocResource source() {
        return source;
    }


    @Override
    public List<NDocProp> props() {
        return new ArrayList<>(properties.toSet());
    }

    public NDocNode setSource(NDocResource source) {
        this.source = source;
        return this;
    }

    @Override
    public boolean isDisabled() {
        NOptional<NElement> style = getPropertyValue(NDocPropName.HIDE);
        if (style.isEmpty()) {
            return false;
        }
        NElement v = style.get();
        if (v == null) {
            return false;
        }
        return v.asBooleanValue().get();
    }

    @Override
    public NDocNode setDisabled(boolean disabled) {
        setProperty(NDocProps.disabled(disabled));
        return this;
    }

    public String name() {
        NOptional<NElement> style = getPropertyValue(NDocPropName.NAME);
        if (style.isEmpty()) {
            return null;
        }
        NElement v = style.get();
        if (v == null) {
            return null;
        }
        return v.asStringValue().get();
    }

    @Override
    public NDocNode setName(String name) {
        setProperty(NDocProps.name(name));
        return this;
    }

    @Override
    public NOptional<NElement> getPropertyValue(String... propertyNames) {
        return getProperty(propertyNames).map(x -> x.getValue());
    }

    @Override
    public NOptional<NElement> getVar(String property) {
        return NOptional.ofNamed(vars.get(property), property);
    }

    public NOptional<NDocProp> getProperty(String... propertyNames) {
        return properties.get(propertyNames);
    }

    @Override
    public NDocNode setProperty(String name, NElement value) {
        properties.set(name, value);
        return this;
    }

    @Override
    public NDocNode setProperty(String name, NToElement value) {
        properties.set(name, value == null ? null : value.toElement());
        return this;
    }

    @Override
    public NDocNode setVar(String name, NElement value) {
        if (value == null) {
            vars.remove(name);
        } else {
            vars.put(name, value);
        }
        return this;
    }


    public NDocNode setProperty(NDocProp s) {
        properties.set(s);
        return this;
    }

    public NDocNode unsetProperty(String s) {
        properties.unset(s);
        return this;
    }

    @Override
    public NDocNode parent() {
        return parent;
    }


    private Set<String> _oldClassNames() {
        NOptional<NDocProp> y = properties.get(NDocPropName.CLASS);
        if (y.isPresent()) {
            return new LinkedHashSet<>(Arrays.asList(NDocObjEx.of(y.get().getValue()).asStringArray().orElse(new String[0])).stream()
                    .filter(x -> x != null && x.trim().length() > 0)
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }
        return new HashSet<>();
    }

    @Override
    public NDocNode addStyleClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            Set<String> s = _oldClassNames();
            s.add(className);
            setProperty(NDocProps.styleClasses(s.toArray(new String[0])));
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
        setProperty(NDocProps.styleClasses(s.toArray(new String[0])));
        return this;
    }

    @Override
    public NDocNode addStyleClasses(String... classNames) {
        if (classNames != null) {
            Set<String> s = _oldClassNames();
            for (String c : classNames) {
                c = validateClassName(c);
                if (c != null) {
                    s.add(c);
                }
            }
            setProperty(NDocProps.styleClasses(s.toArray(new String[0])));
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
    public NDocNode removeClass(String className) {
        className = validateClassName(className);
        if (className != null) {
            Set<String> s = _oldClassNames();
            s.remove(className);
            setProperty(NDocProps.styleClasses(s.toArray(new String[0])));
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
    public NDocNode setParent(NDocNode parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public NDocNode mergeNode(HItem other) {
        if (other != null) {
            if (other instanceof HItemList) {
                for (HItem item : ((HItemList) other).getItems()) {
                    mergeNode(item);
                }
            } else if (other instanceof NDocProp) {
                setProperty((NDocProp) other);
            } else if (other instanceof NDocNodeDef) {
                addNodeDefinition((NDocNodeDef) other);
            } else if (other instanceof NDocNode) {
                NDocNode hn = (NDocNode) other;
                if (this.source == null) {
                    this.source = hn.source();
                }
                this.properties.set(hn.props());
                addRules(hn.rules());
                for (NDocNode child : hn.children()) {
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
            } else if (a instanceof NDocProp) {
                setProperty((NDocProp) a);
                return true;
            } else if (a instanceof HStyleRule) {
                addRule((HStyleRule) a);
                return true;
            } else if (a instanceof NDocNode) {
                add((NDocNode) a);
                return true;
            } else if (a instanceof NDocNodeDef) {
                addNodeDefinition((NDocNodeDef) a);
                return true;
            }
        }
        return false;
    }

    @Override
    public NDocNode setPosition(NDocAlign align) {
        setProperty(NDocPropName.POSITION, align);
        return this;
    }

    @Override
    public NDocNode setPosition(Number x, Number y) {
        setPosition(new NDocDouble2(x, y));
        return this;
    }

    @Override
    public NDocNode setPosition(NDocDouble2 d) {
        setProperty(NDocPropName.POSITION, d);
        return this;
    }

    @Override
    public NDocNode setOrigin(NDocAlign align) {
        setProperty(NDocPropName.ORIGIN, align);
        return this;
    }

    @Override
    public NDocNode setOrigin(Number x, Number y) {
        setOrigin(new NDocDouble2(x, y));
        return this;
    }

    @Override
    public NDocNode setOrigin(NDocDouble2 d) {
        setProperty(NDocPropName.ORIGIN, d);
        return this;
    }

    @Override
    public NDocNode at(NDocAlign align) {
        setPosition(align);
        setOrigin(align);
        return this;
    }

    @Override
    public NDocNode at(Number x, Number y) {
        setPosition(x, y);
        setOrigin(x, y);
        return this;
    }

    @Override
    public NDocNode at(NDocDouble2 d) {
        setPosition(d);
        setOrigin(d);
        return this;
    }

    @Override
    public NDocNode setSize(Number size) {
        return setSize(new NDocDouble2(size, size));
    }

    @Override
    public NDocNode setSize(NDocDouble2 size) {
        NElement old = getPropertyValue(NDocPropName.SIZE).orNull();
        NDocDouble2 oo = old == null ? null : NDocObjEx.of(old).asDouble2().orNull();
        if (oo == null) {
            oo = new NDocDouble2(null, null);
        }
        Number w = size == null ? null : size.getX();
        Number h = size == null ? null : size.getY();
        setProperty(NDocPropName.SIZE, new NDocDouble2(
                NUtils.firstNonNull(w, oo.getX()),
                NUtils.firstNonNull(h, oo.getY())
        ));
        return this;
    }

    @Override
    public NDocNode setSize(Number w, Number h) {
        setSize(new NDocDouble2(w, h));
        return this;
    }

    @Override
    public NDocNode setFontSize(Number w) {
        setProperty(NDocPropName.FONT_SIZE, NElement.ofNumber(w));
        return this;
    }

    @Override
    public NDocNode setFontFamily(String w) {
        setProperty(NDocPropName.FONT_FAMILY, NElement.ofString(w));
        return this;
    }

    @Override
    public NDocNode setFontBold(Boolean w) {
        setProperty(NDocPropName.FONT_BOLD, NElement.ofBoolean(w));
        return this;
    }

    @Override
    public NDocNode setFontItalic(Boolean w) {
        setProperty(NDocPropName.FONT_ITALIC, NElement.ofBoolean(w));
        return this;
    }

    @Override
    public NDocNode setFontUnderlined(Boolean w) {
        setProperty(NDocPropName.FONT_UNDERLINED, NElement.ofBoolean(w));
        return this;
    }

    @Override
    public NDocNode setFontStrike(Boolean w) {
        setProperty(NDocPropName.FONT_STRIKE, NElement.ofBoolean(w));
        return this;
    }

    @Override
    public NDocNode setForegroundColor(String w) {
        setProperty(NDocPropName.FOREGROUND_COLOR, NElement.ofString(w));
        return this;
    }

    @Override
    public NDocNode setBackgroundColor(String w) {
        setProperty(NDocPropName.BACKGROUND_COLOR, NElement.ofString(w));
        return this;
    }

//    @Override
//    public NDocNode setLineColor(String w) {
//        setProperty(NDocPropName.LINE_COLOR, w);
//        return this;
//    }

    @Override
    public NDocNode setGridColor(String w) {
        setProperty(NDocPropName.GRID_COLOR, NElement.ofString(w));
        return this;
    }

    @Override
    public List<NDocProp> getProperties() {
        return new ArrayList<>(properties.toSet());
    }

    @Override
    public NDocNode setProperties(NDocProp... props) {
        if (props != null) {
            for (NDocProp prop : props) {
                setProperty(prop);
            }
        }
        return this;
    }

    @Override
    public NDocNode setChildren(NDocNode... children) {
        this.children.clear();
        addAll(children);
        return this;
    }

    @Override
    public NDocNode addAll(NDocNode... a) {
        if (a != null) {
            for (NDocNode n : a) {
                add(n);
            }
        }
        return this;
    }

    @Override
    public NDocNode add(NDocNode a) {
        if (a != null) {
            children.add(a);
            a.setParent(this);
        }
        return this;
    }

    @Override
    public List<NDocNode> children() {
        return children;
    }

    @Override
    public NDocNode addRule(HStyleRule s) {
        if (s != null) {
            styleRules.add(s);
        }
        return this;
    }

    @Override
    public NDocNode setRules(HStyleRule[] rules) {
        styleRules.clear();
        addRules(rules);
        return this;
    }

    @Override
    public NDocNode addRules(HStyleRule... rules) {
        if (rules != null) {
            for (HStyleRule rule : rules) {
                addRule(rule);
            }
        }
        return this;
    }

    @Override
    public NDocNode removeRule(HStyleRule s) {
        styleRules.remove(s);
        return this;
    }

    @Override
    public NDocNode clearRules() {
        for (HStyleRule rule : rules()) {
            removeRule(rule);
        }
        return this;
    }

    public NDocNodeDef[] nodeDefinitions() {
        return definitions.toArray(new NDocNodeDef[0]);
    }

    @Override
    public NDocNode addNodeDefinition(NDocNodeDef s) {
        if (s != null) {
            definitions.add(s);
        }
        return this;
    }

    @Override
    public NDocNode removeNodeDefinition(NDocNodeDef s) {
        if (s != null) {
            definitions.remove(s);
        }
        return this;
    }

    @Override
    public HStyleRule[] rules() {
        return styleRules.toArray(new HStyleRule[0]);
    }

    public NDocNode copy() {
        DefaultNDocNode o = new DefaultNDocNode(nodeType);
        copyTo(o);
        return o;
    }

    public NDocNode copyTo(NDocNode other) {
        other.setUuid(UUID.randomUUID().toString());
        other.setSource(source());
        other.setProperties(properties.toArray());
        other.addAll(children().stream().map(NDocNode::copy).toArray(NDocNode[]::new));
        other.addRules(Arrays.stream(rules()).toArray(HStyleRule[]::new));
        other.addNodeDefinitions(nodeDefinitions());
        return this;
    }

    public NDocNode copyFrom(NDocNode other) {
        other.copyTo(this);
        return this;
    }

    @Override
    public NDocNode addNodeDefinitions(NDocNodeDef... definitions) {
        for (NDocNodeDef definition : definitions) {
            addNodeDefinition(definition);
        }
        return this;
    }

    //    @Override
//    public NDocNode addRule(HProp... s) {
//        if (s != null) {
//            Set<NDocProp> ss = Arrays.stream(s).filter(x -> x != null).collect(Collectors.toSet());
//            if (!ss.isEmpty()) {
//                addRule(DefaultHStyleRule.ofAny(s));
//            }
//        }
//        return this;
//    }
//
    private NElement toTson0() {
        if (NDocNodeType.ASSIGN.equals(type())) {
            return NElement.ofPair("$" + getName(), getPropertyValue(NDocPropName.VALUE).orNull());
        }
        String[] styleClasses = getStyleClasses();
        if (!styleRules.isEmpty() || !children.isEmpty()) {
            NObjectElementBuilder o = NElement.ofObjectBuilder(nodeType);
            if (styleClasses.length > 0) {
                o.addAnnotation(null, Arrays.stream(styleClasses).map(x -> NElement.ofString(x)).toArray(NElement[]::new));
            }
            if (source != null) {
                o.add(NElement.ofPair("source", NElement.ofString(source.shortName())));
            }
            for (NDocProp p : properties.toList()) {
                switch (p.getName()) {
                    case NDocPropName.CLASS: {
                        break;
                    }
                    default: {
                        o.addParam(p.toElement());
                    }
                }
            }
            if (!styleRules.isEmpty()) {
                o.add("rules", NElement.ofObject(styleRules.stream().map(x -> x.toElement()).toArray(NElement[]::new)));
            }
            for (NDocNode child : children()) {
                if (child instanceof DefaultNDocNode) {
                    o.add(((DefaultNDocNode) child).toTson0());
                } else {
                    o.add(NElement.ofString(child.toString()));
                }
            }
            return o.build();
        } else {
            NUpletElementBuilder o = NElement.ofUpletBuilder().name(nodeType);
            if (styleClasses.length > 0) {
                o.addAnnotation(null, Arrays.stream(styleClasses).map(x -> NElement.ofString(x)).toArray(NElement[]::new));
            }
            if (source != null) {
                o.add(NElement.ofPair("source", NElement.ofString(String.valueOf(source))));
            }
            for (NDocProp p : properties.toList()) {
                switch (p.getName()) {
                    case NDocPropName.CLASS: {
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
        String n = NDocObjEx.of(getPropertyValue(NDocPropName.NAME).orNull()).asStringOrName().orNull();
        if (n != null) {
            return n;
        }
        return null;
    }

    @Override
    public void setChildAt(int i, NDocNode c) {
        NAssert.requireNonNull(c, "node");
        children.set(i, c);
    }

    @Override
    public NDocFunction[] nodeFunctions() {
        return functions.toArray(new NDocFunction[0]);
    }

    @Override
    public NDocNode addNodeFunction(NDocFunction s) {
        if (s != null) {
            functions.add(s);
        }
        return this;
    }

    @Override
    public NDocNode addNodeFunctions(NDocFunction... definitions) {
        if (definitions != null) {
            for (NDocFunction s : definitions) {
                addNodeFunction(s);
            }
        }
        return this;
    }

    @Override
    public NDocNode removeNodeFunction(String s) {
        functions.removeIf(f -> NNameFormat.equalsIgnoreFormat(f.name(), s));
        return this;
    }
}
