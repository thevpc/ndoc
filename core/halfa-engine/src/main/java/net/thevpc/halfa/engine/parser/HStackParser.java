package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.container.HStackContainer;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonObject;

public class HStackParser {
    public static NOptional<HContainer> parseContainer(String type, TsonObject ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HContainer p =null;
        switch (type){
            case "stack":{
                p=f.stack();
                break;
            }
            case "flow":{
                p=f.flow();
                break;
            }
            case "grid":{
                p=f.grid();
                break;
            }
            case "vgrid":
            case "v-grid":
            case "grid-v":
            {
                p=f.vgrid();
                break;
            }
            case "hgrid":
            case "h-grid":
            case "grid-h":
            {
                p=f.hgrid();
                break;
            }
            case "ul":
            case "unordered-list":{
                p = f.orderedList();
                break;
            }
            case "ol":
            case "ordered-list":
            case "numbered-list":{
                p = f.unorderedList();
                break;
            }
        }
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
