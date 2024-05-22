package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.api.node.HPageGroup;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonObject;
import net.thevpc.tson.TsonPair;

public class HPageGroupParser {
    public static NOptional<HPageGroup> readPageGroup(TsonObject ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HPageGroup pg = f.pageGroup();
        for (TsonElement e : ff.getAll()) {
            NOptional<HStyleRule> r = HStyleRuleParser.parseStyleRule(e, f, context);
            if (r.isPresent()) {
                pg.addRule(r.get());
            } else if (r.isError()) {
                return NOptional.ofError(
                        s -> NMsg.ofC("Error parsing page group : %s", r.getMessage().apply(s))
                );
            } else {
                NOptional<HNode> u = context.engine().newDocumentChild(e, pg, context);
                if(u.isPresent()){
                    pg.add(u.get());
                }else{
                    u = context.engine().newPageChild(e, pg, context);
                    if(u.isPresent()){
                        HPage page = f.page();
                        pg.add(page);
                        page.add(u.get());
                    }else{
                        return NOptional.ofError(
                                s -> NMsg.ofC("Error parsing page group : %s", r.getMessage().apply(s))
                        );
                    }
                }
            }
        }
        return NOptional.of(pg);
    }
}
