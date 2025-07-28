package net.thevpc.ndoc.api.base.format;

import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;


import java.util.*;
import java.util.function.Predicate;

public class ToElementHelper {
    List<NElement> args = new ArrayList<>();
    List<NElement> children = new ArrayList<>();
    private String name;
    private NDocNode node;
    private Predicate<String> exclude;
    private Set<String> excludeSet = new HashSet<>();
    private Set<String> defaultExcludeSet = new HashSet<>(Arrays.asList(NDocPropName.CLASS));
    private NDocEngine engine;

    public static ToElementHelper of(NDocNode node, NDocEngine engine) {
        return new ToElementHelper(
                NDocUtils.uid(node.type())
                , node, engine);
    }

    public ToElementHelper(String name, NDocNode node, NDocEngine engine) {
        this.name = name;
        this.node = node;
        this.engine = engine;
    }

    public NElement build() {
        List<NElement> args2 = new ArrayList<>();
        List<NElement> ch = new ArrayList<>();
        args2.addAll(args);
        for (NDocProp p : node.props()) {
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
            NDocStyleRule[] rules = node.rules();
            if (rules.length > 0) {
                ch.add(
                        NElement.ofPair("styles",
                                NElement.ofObject(
                                        Arrays.stream(rules).map(x -> x.toElement()).toArray(NElement[]::new)
                                )
                        )
                );
            }
            ch.addAll(children);
            for (NDocNode child : node.children()) {
                ch.add(
                        engine.nodeTypeParser(child.type()).get().toElem(child)
                );
            }
            NObjectElementBuilder u = NElement.ofObjectBuilder(name).addParams(args2).addAll(ch);
            applyAnnotations(u);
            return u.build();
        } else {
            NUpletElementBuilder u = NElement.ofUplet(name, args2.toArray(new NElement[0])).builder();
            applyAnnotations(u);
            return u.build();
        }
    }

    private void applyAnnotations(NElementBuilder u) {
        NOptional<String[]> sa = NDocObjEx.of(node.getPropertyValue(NDocPropName.CLASS).orNull()).asStringArrayOrString();
        if (sa.isPresent()) {
            u.addAnnotation(null,
                    Arrays.stream(sa.get()).map(x -> NElement.ofString(x)).toArray(NElement[]::new)
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
            addChild(NElement.ofPair(name, NDocUtils.toElement(name)));
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
                addArg(NElement.ofString(value));
            } else {
                addArg(NElement.ofString(value,NElementType.TRIPLE_DOUBLE_QUOTED_STRING));
            }
        }
        excludeProps(name);
        return this;
    }

    public ToElementHelper excludeProps(String prop) {
        if (!NBlankable.isBlank(prop)) {
            excludeSet.add(NDocUtils.uid(prop));
        }
        return this;
    }

    public ToElementHelper addChildProps(String[] propNames) {
        if(propNames!=null) {
            for (String propName : propNames) {
                if(propName!=null) {
                    NElement v = node.getPropertyValue(propName).orNull();
                    if (v != null) {
                        addChild(NElement.ofPair(propName, v));
                    }
                }
            }
        }
        return this;
    }
}
