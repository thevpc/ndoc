package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
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
                        List<HStyle> styles=new ArrayList<>();
                        switch (v.type()) {
                            case OBJECT: {
                                for (TsonElement yy : v.toObject().all()) {
                                    NOptional<HStyle> u = HStyleParser.parseStyle(yy, f, context);
                                    if (!u.isPresent()) {
                                        return NOptional.ofEmpty(s-> NMsg.ofC("invalid invalid rule  %s :: %s",e, u.getMessage().apply(s)));
                                    }
                                    styles.add(u.get());
                                }
                                break;
                            }
                            case ARRAY: {
                                for (TsonElement yy : v.toObject().all()) {
                                    NOptional<HStyle> u = HStyleParser.parseStyle(yy, f, context);
                                    if (!u.isPresent()) {
                                        return NOptional.ofEmpty(s-> NMsg.ofC("invalid invalid rule  %s :: %s",e, u.getMessage().apply(s)));
                                    }
                                    styles.add(u.get());
                                }
                                break;
                            }
                            case PAIR: {
                                NOptional<HStyle> u = HStyleParser.parseStyle(v, f, context);
                                if (!u.isPresent()) {
                                    return NOptional.ofEmpty(s-> NMsg.ofC("invalid invalid rule  %s :: %s",e, u.getMessage().apply(s)));
                                }
                                styles.add(u.get());
                            }
                        }
                        return NOptional.of(
                                new DefaultHStyleRule(
                                        DefaultHNodeSelector.ofAny(),
                                        styles.toArray(new HStyle[0])
                                )
                        );
                    }
                }
                break;
            }
        }
        return NOptional.ofNamedEmpty("invalid HStyleRule");
    }
}
