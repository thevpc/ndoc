package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HPageParser {
    public static NOptional<HPage> parsePage(TsonElement ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HPage p = f.page();
        switch (ff.type()) {
            case FUNCTION:
            case OBJECT:
            case ARRAY: {
                TsonElementExt ee=new TsonElementExt(ff);
                if(!HNodeParser.fillAnnotations(ff,p)){
                    return NOptional.ofNamedError("page can have only a single parent template");
                }
                for (TsonElement e : ee.args()) {
                    NOptional<HItem> u = HStyleParser.parseStyle(e, f, context);
                    if (u.isPresent()) {
                        p.append(u.get());
                    }else {
                        return NOptional.ofNamedError("invalid style or property " + e+" for page");
                    }
                }
                for (TsonElement e : ee.children()) {
                    NOptional<HStyleRule> r = HStyleRuleParser.parseStyleRule(e, f, context);
                    if (r.isPresent()) {
                        p.append(r.get());
                    } else if (r.isError()) {
                        return NOptional.ofError(
                                s -> NMsg.ofC("Error parsing rule : %s", r.getMessage().apply(s))
                        );
                    } else {
                        NOptional<HItem> u = context.engine().newPageChild(e, p, context);
                        if (u.isPresent()) {
                            p.append(u.get());
                        } else {
                            return NOptional.ofError(
                                    s -> NMsg.ofC("Error parsing page : %s", u.getMessage().apply(s))
                            );
                        }
                    }
                }
            }
        }
        return NOptional.of(p);
    }
}
