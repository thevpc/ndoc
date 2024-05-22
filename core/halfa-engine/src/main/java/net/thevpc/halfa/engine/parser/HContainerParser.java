package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HContainerParser {
    public static NOptional<HContainer> parseContainer(String type, TsonElement ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HContainer p = null;
        switch (type) {
            case "stack": {
                p = f.stack();
                break;
            }
            case "flow": {
                p = f.flow();
                break;
            }
            case "grid": {
                p = f.grid();
                break;
            }
            case "vgrid":
            case "v-grid":
            case "grid-v": {
                p = f.vgrid();
                break;
            }
            case "hgrid":
            case "h-grid":
            case "grid-h": {
                p = f.hgrid();
                break;
            }
            case "ul":
            case "unordered-list": {
                p = f.orderedList();
                break;
            }
            case "ol":
            case "ordered-list":
            case "numbered-list": {
                p = f.unorderedList();
                break;
            }
            default: {
                return NOptional.ofError(
                        s -> NMsg.ofC("[%s] Error parsing page : %s", context.source(), type)
                );
            }
        }
        TsonElementExt ee = new TsonElementExt(ff);
        for (TsonElement e : ee.args()) {
            NOptional<HItem> s = HStyleParser.parseStyle(e, f, context);
            if (s.isPresent()) {
                p.append(s.get());
            } else {
                s = HStyleParser.parseStyle(e, f, context);
                return NOptional.ofError(
                        ss -> NMsg.ofC("[%s] Error parsing styles for %s : %s", context.source(), e, ff)
                );
            }
        }
        for (TsonElement e : ee.children()) {
            NOptional<HStyleRule> r = HStyleRuleParser.parseStyleRule(e, f, context);
            if (r.isPresent()) {
                p.append(r.get());
            } else if (r.isError()) {
                return NOptional.ofError(
                        s -> NMsg.ofC("[%s] Error parsing rule : %s", context.source(), r.getMessage().apply(s))
                );
            } else {
                NOptional<HItem> u = context.engine().newPageChild(e, p, context);
                if (u.isPresent()) {
                    p.append(u.get());
                } else {
                    return NOptional.ofError(
                            s -> NMsg.ofC("[%s] Error parsing page : %s", context.source(), u.getMessage().apply(s))
                    );
                }
            }
        }
        return NOptional.of(p);
    }
}
