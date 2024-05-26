package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NBlankable;
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
    private HEngine engine;

    public static ToTsonHelper of(HNode node,HEngine engine) {
        return new ToTsonHelper(
                HUtils.uid(node.type())
                , node,engine);
    }

    public ToTsonHelper(String name, HNode node,HEngine engine) {
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
            ) {
                args2.add(p.toTson());
            }
        }
        if (node instanceof HContainer) {
            HStyleRule[] rules = ((HContainer) node).rules();
            if (rules.length > 0) {
                ch.add(
                        Tson.pair("styles",
                                Tson.obj(
                                        Arrays.stream(rules).map(x -> x.toTson()).toArray(TsonElement[]::new)
                                )
                        )
                );
            }
            ch.addAll(children);
            for (HNode child : ((HContainer) node).children()) {
                ch.add(
                        engine.nodeTypeFactory(child.type()).get().toTson(child)
                );
            }
            if (Objects.equals(node.type(), HNodeType.PAGE_GROUP)) {
                //TODO fix me, styles ignored. is that okkay?
                TsonObjectBuilder u = Tson.obj(ch.toArray(new TsonElementBase[0]));
                if (node.getParentTemplate() != null) {
                    u.annotation(node.getParentTemplate());
                }
                return u.build();
            } else {
                TsonObjectBuilder u = Tson.obj(name, args2.toArray(new TsonElementBase[0]),
                        ch.toArray(new TsonElementBase[0])
                );
                if (node.getParentTemplate() != null) {
                    u.annotation(node.getParentTemplate());
                }
                return u.build();
            }
        } else {
            TsonFunctionBuilder u = Tson.function(name, args2.toArray(new TsonElementBase[0]));
            if (node.getParentTemplate() != null) {
                u.annotation(node.getParentTemplate());
            }
            return u.build();
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
        if(value!=null){
            boolean multiLine=
                    value.indexOf('\n')>=0
                    || value.indexOf('\r')>=0;
            if(!multiLine){
                addArg(Tson.string(value, TsonStringLayout.DOUBLE_QUOTE));
            }else{
                addArg(Tson.string(value, TsonStringLayout.TRIPLE_DOUBLE_QUOTE));
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
