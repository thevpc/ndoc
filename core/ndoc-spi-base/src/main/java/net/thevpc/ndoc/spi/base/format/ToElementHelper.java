package net.thevpc.ndoc.spi.base.format;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HStyleRule;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;


import java.util.*;
import java.util.function.Predicate;

public class ToElementHelper {
    List<NElement> args = new ArrayList<>();
    List<NElement> children = new ArrayList<>();
    private String name;
    private HNode node;
    private Predicate<String> exclude;
    private Set<String> excludeSet = new HashSet<>();
    private Set<String> defaultExcludeSet = new HashSet<>(Arrays.asList(HPropName.CLASS, HPropName.ANCESTORS));
    private NDocEngine engine;

    public static ToElementHelper of(HNode node, NDocEngine engine) {
        return new ToElementHelper(
                net.thevpc.ndoc.api.util.HUtils.uid(node.type())
                , node, engine);
    }

    public ToElementHelper(String name, HNode node, NDocEngine engine) {
        this.name = name;
        this.node = node;
        this.engine = engine;
    }

    public NElement build() {
        List<NElement> args2 = new ArrayList<>();
        List<NElement> ch = new ArrayList<>();
        args2.addAll(args);
        for (HProp p : node.props()) {
            if (
                    (exclude == null || !exclude.test(p.getName()))
                            && !excludeSet.contains(p.getName())
                            //exclude class and
                            && !defaultExcludeSet.contains(p.getName())
            ) {
                args2.add(p.toElement());
            }
        }
        if (node.children().size() > 0 || node.rules().length > 0) {
            HStyleRule[] rules = node.rules();
            if (rules.length > 0) {
                ch.add(
                        NElements.ofPair("styles",
                                NElements.ofObject(
                                        Arrays.stream(rules).map(x -> x.toElement()).toArray(NElement[]::new)
                                )
                        )
                );
            }
            ch.addAll(children);
            for (HNode child : node.children()) {
                ch.add(
                        engine.nodeTypeFactory(child.type()).get().toElem(child)
                );
            }
            NObjectElementBuilder u = NElements.ofObjectBuilder(name).addParams(args2).addAll(ch);
            applyAnnotations(u);
            return u.build();
        } else {
            NUpletElementBuilder u = NElements.ofUplet(name, args2.toArray(new NElement[0])).builder();
            applyAnnotations(u);
            return u.build();
        }
    }

    private void applyAnnotations(NElementBuilder u) {
        for (String ancestor : node.getAncestors()) {
            u.addAnnotation(ancestor);
        }
        NOptional<String[]> sa = NDocObjEx.of(node.getPropertyValue(HPropName.CLASS).orNull()).asStringArrayOrString();
        if (sa.isPresent()) {
            u.addAnnotation(null,
                    Arrays.stream(sa.get()).map(x -> NElements.ofString(x)).toArray(NElement[]::new)
            );
        }
    }

    public ToElementHelper addArgs(NElement... elements) {
        if (elements != null) {
            for (NElement e : elements) {
                addArg(e);
            }
        }
        return this;
    }

    public ToElementHelper addArg(NElement e) {
        if (e != null) {
            args.add(e);
        }
        return this;
    }

    public ToElementHelper addNonNullPairChild(String name, Object value) {
        if(value!=null){
            addChild(NElements.ofPair(name, net.thevpc.ndoc.api.util.HUtils.toElement(name)));
        }
        return this;
    }

    public ToElementHelper addChildren(NElement... elements) {
        if (elements != null) {
            for (NElement e : elements) {
                addChild(e);
            }
        }
        return this;
    }

    public ToElementHelper addChild(NElement e) {
        if (e != null) {
            children.add(e);
        }
        return this;
    }

    public ToElementHelper inlineStringProp(String name) {
        String value = NDocObjEx.ofProp(node, name).asStringOrName().orNull();
        if (value != null) {
            boolean multiLine =
                    value.indexOf('\n') >= 0
                            || value.indexOf('\r') >= 0;
            if (!multiLine) {
                addArg(NElements.ofString(value));
            } else {
                addArg(NElements.ofString(value,NElementType.TRIPLE_DOUBLE_QUOTED_STRING));
            }
        }
        excludeProps(name);
        return this;
    }

    public ToElementHelper excludeProps(String prop) {
        if (!NBlankable.isBlank(prop)) {
            excludeSet.add(HUtils.uid(prop));
        }
        return this;
    }

    public ToElementHelper addChildProps(String[] propNames) {
        if(propNames!=null) {
            for (String propName : propNames) {
                if(propName!=null) {
                    NElement v = node.getPropertyValue(propName).orNull();
                    if (v != null) {
                        addChild(NElements.ofPair(propName, v));
                    }
                }
            }
        }
        return this;
    }
}
