package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

import java.util.ArrayList;
import java.util.List;

public class HStyleRuleParser {
    public static NOptional<HStyleRule> parseStyleRule(TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                TsonPair p = e.toPair();
                TsonElement k = p.getKey();
                TsonElementParseHelper ee = new TsonElementParseHelper(k);
                NOptional<String> n = ee.asStringOrName();
                if (n.isPresent()) {
                    if ("rule".equals(n.get())) {
                        TsonElement v = p.getValue();
                        List<HItem> styles=new ArrayList<>();
                        switch (v.type()) {
                            case OBJECT: {
                                for (TsonElement yy : v.toObject().all()) {
                                    NOptional<HItem> u = HStyleParser.parseStyle(yy, f, context);
                                    if (!u.isPresent()) {
                                        return NOptional.ofEmpty(s-> NMsg.ofC("invalid invalid rule  %s :: %s",e, u.getMessage().apply(s)));
                                    }
                                    styles.add(u.get());
                                }
                                break;
                            }
                            case ARRAY: {
                                for (TsonElement yy : v.toObject().all()) {
                                    NOptional<HItem> u = HStyleParser.parseStyle(yy, f, context);
                                    if (!u.isPresent()) {
                                        return NOptional.ofEmpty(s-> NMsg.ofC("invalid invalid rule  %s :: %s",e, u.getMessage().apply(s)));
                                    }
                                    styles.add(u.get());
                                }
                                break;
                            }
                            case PAIR: {
                                NOptional<HItem> u = HStyleParser.parseStyle(v, f, context);
                                if (!u.isPresent()) {
                                    return NOptional.ofEmpty(s-> NMsg.ofC("invalid invalid rule  %s :: %s",e, u.getMessage().apply(s)));
                                }
                                styles.add(u.get());
                            }
                        }
                        return NOptional.of(
                                new DefaultHStyleRule(
                                        DefaultHNodeSelector.ofAny(),
                                        toHStyleList(styles).toArray(new HStyle[0])
                                )
                        );
                    }
                }
                break;
            }
        }
        return NOptional.ofNamedEmpty("invalid HStyleRule");
    }
    private static List<HStyle> toHStyleList(List<HItem> i){
        List<HStyle> z=new ArrayList<>();
        for (HItem hItem : i) {
            z.addAll(toHStyleList(hItem));
        }
        return z;
    }

    private static List<HStyle> toHStyleList(HItem i){
        List<HStyle> z=new ArrayList<>();
        if(i instanceof HStyle){
            z.add((HStyle)i);
        }else if(i instanceof HItemList){
            z.addAll(toHStyleList(((HItemList) i).getItems()));
        }else{
            throw new IllegalArgumentException("not supported");
        }
        return z;
    }
}
