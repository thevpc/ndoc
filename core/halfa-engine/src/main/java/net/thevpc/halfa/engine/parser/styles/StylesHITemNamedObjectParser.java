package net.thevpc.halfa.engine.parser.styles;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.document.HMsg;
import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.parser.HStyleParser;
import net.thevpc.halfa.engine.parser.nodes.AbstractHITemNamedObjectParser;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
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
        switch (tsonElement.type()) {
            case OBJECT:
            case NAMED_PARAMETRIZED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_OBJECT:
            {
                for (TsonElement yy : tsonElement.toObject().body()) {
                    NOptional<HStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                    if (!u.isPresent()) {
                        HStyleParser.parseStyleRule(yy, f, context).get();
                        context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule  %s :: %s", net.thevpc.halfa.api.util.HUtils.shortName(context.source()), yy, u.getMessage().get()).asSevere(), context.source()));
                        return NOptional.ofEmpty(() -> NMsg.ofC("[%s] invalid style rule  %s :: %s", net.thevpc.halfa.api.util.HUtils.shortName(context.source()), yy, u.getMessage().get()));
                    }
                    for (HStyleRule r : u.get()) {
                        styles.add(r);
                    }
                }
                break;
            }
            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY:
            {
                for (TsonElement yy : tsonElement.toArray().body()) {
                    NOptional<HStyleRule[]> u = HStyleParser.parseStyleRule(yy, f, context);
                    if (!u.isPresent()) {
                        context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid style rule  %s :: %s", net.thevpc.halfa.api.util.HUtils.shortName(context.source()), yy, u.getMessage().get().asSevere(), context.source())));
                        NOptional.ofEmpty(() -> NMsg.ofC("[%s] invalid style rule  %s :: %s", net.thevpc.halfa.api.util.HUtils.shortName(context.source()), yy, u.getMessage().get()));
                    }
                    for (HStyleRule r : u.get()) {
                        styles.add(r);
                    }
                }
                break;
            }
            default: {
                return NOptional.ofEmpty(() -> NMsg.ofC("[%s] invalid style rule  %s ", HUtils.shortName(context.source()), tsonElement));
            }
        }
        return NOptional.of(new HItemList().addAll(styles));
    }
}
