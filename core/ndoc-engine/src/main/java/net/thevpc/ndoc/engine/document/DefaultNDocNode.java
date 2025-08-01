package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.*;
import net.thevpc.ndoc.api.eval.NDocFunction;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.NDocAlign;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.*;


import java.util.*;
import java.util.stream.Collectors;

public class DefaultNDocNode implements NDocNode {
    private String uuid;
    private String nodeType;
    private NDocResource source;
    protected NDocItem parent;
    private NDocProperties properties;
    private Map<String, NElement> vars = new LinkedHashMap<>();
    private List<NDocNode> children = new ArrayList<>();
    private List<NDocStyleRule> styleRules = new ArrayList<>();
    private List<NDocNodeDef> definitions = new ArrayList<>();
    private List<NDocFunction> functions = new ArrayList<>();
    private List<NDocNode> hierarchy = new ArrayList<>();
    private NDocNodeDef templateDefinition;

    public static DefaultNDocNode ofText(String message) {
        DefaultNDocNode t = new DefaultNDocNode(NDocNodeType.TEXT);
        t.setProperty(NDocPropName.VALUE, NElement.ofString(message));
        return t;
    }

    public static DefaultNDocNode ofAssign(String name, NElement value, NDocResource source) {
        DefaultNDocNode n = new DefaultNDocNode(NDocNodeType.CTRL_ASSIGN, source);
        n.setProperty(NDocPropName.NAME, NDocUtils.addCompilerDeclarationPath(NElement.ofString(name),source));
        n.setProperty(NDocPropName.VALUE, NDocUtils.addCompilerDeclarationPath(value,source));
        return n;
    }

    public static DefaultNDocNode ofExpr(NElement value, NDocResource source) {
        DefaultNDocNode n = new DefaultNDocNode(NDocNodeType.CTRL_EXPR, source);
        n.setProperty(NDocPropName.VALUE, NDocUtils.addCompilerDeclarationPath(value,source));
        return n;
    }

    public DefaultNDocNode(String nodeType) {
        this(nodeType, null);
    }

    public DefaultNDocNode(String nodeType, NDocResource source) {
        this.nodeType = nodeType;
        this.uuid = UUID.randomUUID().toString();
        this.source = source;
        properties = new NDocProperties(this);
    }

    @Override
    public String uuid() {
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
                .map(NDocUtils::uid)
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
        NElement u = vars.get(property);
        if(u!=null){
            NDocResource source = NDocUtils.sourceOf(this);
            if(source!=null){
                u = NDocUtils.addCompilerDeclarationPath(u, source);
            }
        }
        return NOptional.ofNamed(u, property);
    }

    @Override
    public Map<String, NElement> getVars() {
        return new LinkedHashMap<>(vars);
    }

    public NOptional<NDocProp> getProperty(String... propertyNames) {
        return properties.get(propertyNames);
    }

    public NDocNode setProperty(NDocProp prop) {
        if(false) {
            String s = NDocUtils.findCompilerDeclarationPath(prop.getValue()).orNull();
            if (s == null) {
                throw new NIllegalArgumentException(NMsg.ofC("var value %s=%s is missing CompilerDeclarationPath", prop.getName(), prop.getValue()));
            }
        }
        properties.set(prop);
        return this;
    }

    public NDocNode unsetProperty(String s) {
        properties.unset(s);
        return this;
    }

    @Override
    public NDocNode setProperty(String name, NElement value) {
        if(value!=null) {
            setProperty(NDocProp.of(name, value));
        }else{
            unsetProperty(name);
        }
        return this;
    }

    @Override
    public NDocNode setProperty(String name, NToElement value) {
        if(value!=null){
            setProperty(NDocProp.of(name, value));
        }else {
            unsetProperty(name);
        }
        return this;
    }

    @Override
    public NDocNode setVar(String name, NElement value) {
        if (value == null) {
            vars.remove(name);
        } else {
            if(false) {
                String s = NDocUtils.findCompilerDeclarationPath(value).orNull();
                if (s == null) {
                    throw new NIllegalArgumentException(NMsg.ofC("var value %s=%s is missing CompilerDeclarationPath", name, value));
                }
            }
            vars.put(name, value);
        }
        return this;
    }



    @Override
    public NDocItem parent() {
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
    public NDocItem setStyleClasses(String[] classNames) {
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
    public NDocNode setParent(NDocItem parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public NDocNode mergeNodes(NDocItem... other) {
        if(other!=null){
            for (NDocItem o : other) {
                mergeNode(o);
            }
        }
        return this;
    }

    @Override
    public NDocNode addAll(NDocItem... other) {
        if(other!=null){
            for (NDocItem o : other) {
                add(o);
            }
        }
        return this;
    }

    @Override
    public NDocNode mergeNode(NDocItem other) {
        if (other != null) {
            if (other instanceof NDocItemList) {
                for (NDocItem item : ((NDocItemList) other).getItems()) {
                    mergeNode(item);
                }
            } else if (other instanceof NDocProp) {
                setProperty((NDocProp) other);
            } else if (other instanceof NDocNodeDef) {
                addDefinition((NDocNodeDef) other);
            } else if (other instanceof NDocNode) {
                NDocNode hn = (NDocNode) other;
                if (this.source == null) {
                    this.source = hn.source();
                }
                this.properties.set(hn.props());
                addRules(hn.rules());
                for (NDocNode child : hn.children()) {
                    addChild(child);
                }
            }
        }
        return this;
    }
    @Override
    public NDocNode add(NDocItem other) {
        if (other != null) {
            if (other instanceof NDocItemList) {
                for (NDocItem item : ((NDocItemList) other).getItems()) {
                    add(item);
                }
            } else if (other instanceof NDocProp) {
                setProperty((NDocProp) other);
            } else if (other instanceof NDocNodeDef) {
                addDefinition((NDocNodeDef) other);
            } else if (other instanceof NDocNode) {
                addChild((NDocNode) other);
            } else if (other instanceof NDocStyleRule) {
                addRule((NDocStyleRule) other);
            }
        }
        return this;
    }

    @Override
    public boolean append(NDocItem a) {
        if (a != null) {
            if (a instanceof NDocItemList) {
                boolean b = false;
                for (NDocItem item : ((NDocItemList) a).getItems()) {
                    b |= append(item);
                }
                return b;
            } else if (a instanceof NDocProp) {
                setProperty((NDocProp) a);
                return true;
            } else if (a instanceof NDocStyleRule) {
                addRule((NDocStyleRule) a);
                return true;
            } else if (a instanceof NDocNode) {
                addChild((NDocNode) a);
                return true;
            } else if (a instanceof NDocNodeDef) {
                addDefinition((NDocNodeDef) a);
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
        addChildren(children);
        return this;
    }

    @Override
    public NDocNode addChildren(NDocNode... a) {
        if (a != null) {
            for (NDocNode n : a) {
                addChild(n);
            }
        }
        return this;
    }

    @Override
    public NDocNode addChild(NDocNode a) {
        if (a != null) {
            children.add(a);
            a.setParent(this);
            NDocUtils.checkNode(a,true);
        }
        return this;
    }

    @Override
    public List<NDocNode> children() {
        return children;
    }

    @Override
    public NDocNode addRule(NDocStyleRule s) {
        if (s != null) {
            if(!styleRules.contains(s)){
                styleRules.add(s);
            }
        }
        return this;
    }

    @Override
    public NDocNode setRules(NDocStyleRule[] rules) {
        styleRules.clear();
        addRules(rules);
        return this;
    }

    @Override
    public NDocNode addRules(NDocStyleRule... rules) {
        if (rules != null) {
            for (NDocStyleRule rule : rules) {
                addRule(rule);
            }
        }
        return this;
    }

    @Override
    public NDocNode removeRule(NDocStyleRule s) {
        styleRules.remove(s);
        return this;
    }

    @Override
    public NDocNode clearRules() {
        for (NDocStyleRule rule : rules()) {
            removeRule(rule);
        }
        return this;
    }

    @Override
    public NDocNode clearChildren() {
        children.clear();
        return this;
    }

    @Override
    public NDocNode clearDefinitions() {
        definitions.clear();
        return this;
    }

    public NDocNodeDef[] definitions() {
        return definitions.toArray(new NDocNodeDef[0]);
    }

    @Override
    public NDocNode addDefinition(NDocNodeDef s) {
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
    public NDocStyleRule[] rules() {
        return styleRules.toArray(new NDocStyleRule[0]);
    }

    public NDocNode copy() {
        DefaultNDocNode o = new DefaultNDocNode(nodeType, source());
        copyTo(o);
        return o;
    }

    public NDocNode copyTo(NDocNode other) {
        other.setUuid(UUID.randomUUID().toString());
        other.setSource(source());
        other.setProperties(properties.toArray());
        other.addChildren(children().stream().map(NDocNode::copy).toArray(NDocNode[]::new));
        other.addRules(Arrays.stream(rules()).toArray(NDocStyleRule[]::new));
        for (Map.Entry<String, NElement> e : vars.entrySet()) {
            other.setVar(e.getKey(), e.getValue());
        }
        for (NDocNodeDef v : definitions) {
            other.addDefinitions(v);
        }
        for (NDocFunction v : functions) {
            other.addNodeFunction(v);
        }
        for (NDocNode h : hierarchy) {
            other.addHierarchy(h);
        }
        other.setTemplateDefinition(templateDefinition);
        return this;
    }

    public NDocNode reset() {
        uuid = null;
        source = null;
        parent = null;
        if (properties != null) {
            properties.clear();
        }
        vars.clear();
        children.clear();
        styleRules.clear();
        definitions.clear();
        functions.clear();
        hierarchy.clear();
        templateDefinition=null;
        return this;
    }

    public NDocNode copyFrom(NDocNode other) {
        other.copyTo(this);
        return this;
    }

    @Override
    public NDocNode addDefinitions(NDocNodeDef... definitions) {
        for (NDocNodeDef definition : definitions) {
            addDefinition(definition);
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
    private NElement toElem0() {
        if (NDocNodeType.CTRL_ASSIGN.equals(type())) {
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
                    o.add(((DefaultNDocNode) child).toElem0());
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
        return toElem0().toString();
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

    public NDocNode addHierarchy(NDocNode n) {
        if(n!=null){
            hierarchy.add(n);
        }
        return this;
    }

    public NDocNode removeHierarchy(NDocNode n) {
        if(n!=null){
            hierarchy.remove(n);
        }
        return this;
    }

    public List<NDocNode> hierarchy() {
        return hierarchy;
    }

    public NDocNode setTemplateDefinition(NDocNodeDef n) {
        this.templateDefinition=n;
        return this;
    }

    public NDocNodeDef templateDefinition() {
        return templateDefinition;
    }
}
