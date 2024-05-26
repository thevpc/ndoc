package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.engine.parser.nodes.AbstractHITemNamedObjectParser;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.util.ArrayList;
import java.util.List;

public class StylesHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
    public StylesHITemNamedObjectParser() {
        super("styles");
    }
    @Override
    public boolean accept(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        HNode node = context.node();
        if(node==null){
            return true;
        }
        if(node instanceof HContainer){
            return true;
        }
        return  false;
    }


    @Override
    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        List<HItem> styles = new ArrayList<>();
        HDocumentFactory f = context.documentFactory();
        NSession session = context.session();
        switch (tsonElement.type()) {
            case OBJECT: {
                for (TsonElement yy : tsonElement.toObject().all()) {
                    NOptional<HStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                    if (!u.isPresent()) {
                        HStyleParser.parseStyleRule(yy, f, context).get();
                        return NOptional.ofEmpty(s -> NMsg.ofC("[%s] invalid style rule  %s :: %s", context.source(), yy, u.getMessage().apply(session)));
                    }
                    for (HStyleRule r : u.get()) {
                        styles.add(r);
                    }
                }
                break;
            }
            case ARRAY: {
                for (TsonElement yy : tsonElement.toArray().all()) {
                    NOptional<HStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                    if (!u.isPresent()) {
                        NOptional.ofEmpty(s -> NMsg.ofC("[%s] invalid style rule  %s :: %s", context.source(), yy, u.getMessage().apply(session)));
                    }
                    for (HStyleRule r : u.get()) {
                        styles.add(r);
                    }
                }
                break;
            }
            default: {
                return NOptional.ofEmpty(s -> NMsg.ofC("[%s] invalid style rule  %s ", context.source(), tsonElement));
            }
        }
        return NOptional.of(new HItemList().addAll(styles));
    }
}
