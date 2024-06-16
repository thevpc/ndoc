package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
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
    private Set<String> defaultExcludeSet = new HashSet<>(Arrays.asList(HPropName.STYLE_CLASS,HPropName.ANCESTORS));
    private HEngine engine;

    public static ToTsonHelper of(HNode node, HEngine engine) {
        return new ToTsonHelper(
                HUtils.uid(node.type())
                , node, engine);
    }

    public ToTsonHelper(String name, HNode node, HEngine engine) {
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
                                Tson.ofObj(
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
            TsonObjectBuilder u = Tson.ofObj(name, args2.toArray(new TsonElementBase[0]),
                    ch.toArray(new TsonElementBase[0])
            );
            applyAnnotations(u);
            return u.build();
        } else {
            TsonFunctionBuilder u = Tson.ofFunction(name, args2.toArray(new TsonElementBase[0]));
            applyAnnotations(u);
            return u.build();
        }
    }
    private void applyAnnotations(TsonElementBuilder u){
        for (String ancestor : node.getAncestors()) {
            u.annotation(ancestor);
        }
        NOptional<Object> e = node.getPropertyValue(HPropName.STYLE_CLASS);
        if(e.isPresent()){
            NOptional<String[]> sa = ObjEx.of(e.get()).asStringArrayOrString();
            if(sa.isPresent()){
                u.annotation(null,
                        Arrays.stream(sa.get()).map(x->Tson.ofString(x)).toArray(TsonElementBase[]::new)
                );
            }
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
        String value = ObjEx.ofProp(node, name).asString().orNull();
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
}
