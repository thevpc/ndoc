package net.thevpc.ndoc.spi.base.format;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HStyleRule;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.*;

import java.util.*;
import java.util.function.Predicate;

public class ToTsonHelper {
    List<TsonElement> args = new ArrayList<>();
    List<TsonElement> children = new ArrayList<>();
    private String name;
    private HNode node;
    private Predicate<String> exclude;
    private Set<String> excludeSet = new HashSet<>();
    private Set<String> defaultExcludeSet = new HashSet<>(Arrays.asList(HPropName.CLASS, HPropName.ANCESTORS));
    private NDocEngine engine;

    public static ToTsonHelper of(HNode node, NDocEngine engine) {
        return new ToTsonHelper(
                net.thevpc.ndoc.api.util.HUtils.uid(node.type())
                , node, engine);
    }

    public ToTsonHelper(String name, HNode node, NDocEngine engine) {
        this.name = name;
        this.node = node;
        this.engine = engine;
    }

    public TsonElement build() {
        List<TsonElement> args2 = new ArrayList<>();
        List<TsonElement> ch = new ArrayList<>();
        args2.addAll(args);
        for (HProp p : node.props()) {
            if (
                    (exclude == null || !exclude.test(p.getName()))
                            && !excludeSet.contains(p.getName())
                            //exclude class and
                            && !defaultExcludeSet.contains(p.getName())
            ) {
                args2.add(p.toTson());
            }
        }
        if (node.children().size() > 0 || node.rules().length > 0) {
            HStyleRule[] rules = node.rules();
            if (rules.length > 0) {
                ch.add(
                        Tson.ofPair("styles",
                                Tson.ofObjectBuilder(
                                        Arrays.stream(rules).map(x -> x.toTson()).toArray(TsonElement[]::new)
                                )
                        )
                );
            }
            ch.addAll(children);
            for (HNode child : node.children()) {
                ch.add(
                        engine.nodeTypeFactory(child.type()).get().toTson(child)
                );
            }
            TsonObjectBuilder u = Tson.ofObjectBuilder(name, args2.toArray(new TsonElementBase[0]),
                    ch.toArray(new TsonElementBase[0])
            );
            applyAnnotations(u);
            return u.build();
        } else {
            TsonUpletBuilder u = Tson.ofUplet(name, args2.toArray(new TsonElementBase[0])).builder();
            applyAnnotations(u);
            return u.build();
        }
    }

    private void applyAnnotations(TsonElementBuilder u) {
        for (String ancestor : node.getAncestors()) {
            u.annotation(ancestor);
        }
        NOptional<String[]> sa = NDocObjEx.of(node.getPropertyValue(HPropName.CLASS).orNull()).asStringArrayOrString();
        if (sa.isPresent()) {
            u.annotation(null,
                    Arrays.stream(sa.get()).map(x -> Tson.ofString(x)).toArray(TsonElementBase[]::new)
            );
        }
    }

    public ToTsonHelper addArgs(TsonElement... elements) {
        if (elements != null) {
            for (TsonElement e : elements) {
                addArg(e);
            }
        }
        return this;
    }

    public ToTsonHelper addArg(TsonElement e) {
        if (e != null) {
            args.add(e);
        }
        return this;
    }

    public ToTsonHelper addNonNullPairChild(String name,Object value) {
        if(value!=null){
            addChild(Tson.ofPair(name, net.thevpc.ndoc.api.util.HUtils.toTson(name)));
        }
        return this;
    }

    public ToTsonHelper addChildren(TsonElement... elements) {
        if (elements != null) {
            for (TsonElement e : elements) {
                addChild(e);
            }
        }
        return this;
    }

    public ToTsonHelper addChild(TsonElement e) {
        if (e != null) {
            children.add(e);
        }
        return this;
    }

    public ToTsonHelper inlineStringProp(String name) {
        String value = NDocObjEx.ofProp(node, name).asStringOrName().orNull();
        if (value != null) {
            boolean multiLine =
                    value.indexOf('\n') >= 0
                            || value.indexOf('\r') >= 0;
            if (!multiLine) {
                addArg(Tson.ofString(value, TsonStringLayout.DOUBLE_QUOTE));
            } else {
                addArg(Tson.ofString(value, TsonStringLayout.TRIPLE_DOUBLE_QUOTE));
            }
        }
        excludeProps(name);
        return this;
    }

    public ToTsonHelper excludeProps(String prop) {
        if (!NBlankable.isBlank(prop)) {
            excludeSet.add(HUtils.uid(prop));
        }
        return this;
    }

    public ToTsonHelper addChildProps(String[] propNames) {
        if(propNames!=null) {
            for (String propName : propNames) {
                if(propName!=null) {
                    TsonElement v = node.getPropertyValue(propName).orNull();
                    if (v != null) {
                        addChild(Tson.ofPair(propName, v));
                    }
                }
            }
        }
        return this;
    }
}
