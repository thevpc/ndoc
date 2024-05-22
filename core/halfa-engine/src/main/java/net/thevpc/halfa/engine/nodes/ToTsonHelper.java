package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.tson.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToTsonHelper {
    List<TsonElement> args = new ArrayList<>();
    List<TsonElement> children = new ArrayList<>();
    private String name;
    private HNode node;

    public static ToTsonHelper of(HNode node) {
        return new ToTsonHelper(
                NNameFormat.LOWER_KEBAB_CASE.format(node.type().name())
                , node);
    }

    public ToTsonHelper(String name, HNode node) {
        this.name = name;
        this.node = node;
    }

    public TsonElement build() {
        List<TsonElement> args2 = new ArrayList<>();
        List<TsonElement> ch = new ArrayList<>();
        args2.addAll(args);
        for (HStyle style : node.styles()) {
            args2.add(style.toTson());
        }
        if (node instanceof HContainer) {
            HStyleRule[] rules = ((HContainer) node).rules();
            if (rules.length > 0) {
                ch.add(
                        Tson.pair("rules",
                                Tson.obj(
                                        Arrays.stream(rules).map(x -> x.toTson()).toArray(TsonElement[]::new)
                                )
                        )
                );
            }
            ch.addAll(children);
            for (HNode child : ((HContainer) node).children()) {
                ch.add(child.toTson());
            }
            if(node.type()== HNodeType.PAGE_GROUP) {
                //TODO fix me, styles ignored. is that okkay?
                TsonObjectBuilder u = Tson.obj(ch.toArray(new TsonElementBase[0]));
                if (node.getParentTemplate() != null) {
                    u.annotation(node.getParentTemplate());
                }
                return u.build();
            }else{
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
}
