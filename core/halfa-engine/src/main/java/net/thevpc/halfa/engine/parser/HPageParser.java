package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonObject;

public class HPageParser {
    public static NOptional<HPage> parsePage(TsonObject ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HPage p = f.page();
        for (TsonElement e : ff.getAll()) {
            NOptional<HStyleRule> r = HStyleRuleParser.parseStyleRule(e, f, context);
            if (r.isPresent()) {
                p.addRule(r.get());
            } else if (r.isError()) {
                return NOptional.ofError(
                        s -> NMsg.ofC("Error parsing page group : %s", r.getMessage().apply(s))
                );
            } else {
                NOptional<HNode> u = context.engine().newPageChild(e, p, context);
                if (u.isPresent()) {
                    p.add(u.get());
                } else {
                    return NOptional.ofError(
                            s -> NMsg.ofC("Error parsing page : %s", u.getMessage().apply(s))
                    );
                }
            }
        }
        return NOptional.of(p);
    }
}
