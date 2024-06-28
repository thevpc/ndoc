package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.engin.spibase.parser.HStyleParser;
import net.thevpc.halfa.engine.parser.nodes.AbstractHITemNamedObjectParser;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
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
//        HNode node = context.node();
        return true;
//        if(node==null){
//            return true;
//        }
//        if(node instanceof HContainer){
//            return true;
//        }
//        return  false;
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
                        context.messages().addError(NMsg.ofC("[%s] invalid style rule  %s :: %s", HUtils.shortName(context.source()), yy, u.getMessage().apply(session)), context.source());
                        return NOptional.ofEmpty(s -> NMsg.ofC("[%s] invalid style rule  %s :: %s", HUtils.shortName(context.source()), yy, u.getMessage().apply(session)));
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
                        context.messages().addError(NMsg.ofC("[%s] invalid style rule  %s :: %s", HUtils.shortName(context.source()), yy, u.getMessage().apply(session)), context.source());
                        NOptional.ofEmpty(s -> NMsg.ofC("[%s] invalid style rule  %s :: %s", HUtils.shortName(context.source()), yy, u.getMessage().apply(session)));
                    }
                    for (HStyleRule r : u.get()) {
                        styles.add(r);
                    }
                }
                break;
            }
            default: {
                return NOptional.ofEmpty(s -> NMsg.ofC("[%s] invalid style rule  %s ", HUtils.shortName(context.source()), tsonElement));
            }
        }
        return NOptional.of(new HItemList().addAll(styles));
    }
}
