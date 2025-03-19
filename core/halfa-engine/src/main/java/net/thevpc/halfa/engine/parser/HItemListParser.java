package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.spi.base.parser.HStyleParser;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HItemListParser {
    public static NOptional<HItemList> readHItemList(TsonElement ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HItemList pg = new HItemList();

        switch (ff.type()) {
            case NAMED_UPLET:

            case OBJECT:
            case NAMED_PARAMETRIZED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_OBJECT:

            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY:
            {
                ObjEx ee = ObjEx.of(ff);
                for (TsonElement e : ee.args()) {
                    NOptional<HProp[]> u = HStyleParser.parseStyle(e, f, context);
                    if (u.isPresent()) {
                        for (HProp s : u.get()) {
                            pg.add(s);
                        }
                    } else {
                        return NOptional.ofNamedError("invalid " + e + " for page-group");
                    }
                }
                for (TsonElement e : ee.body()) {
                    NOptional<HItem> u = context.engine().newNode(e, context);
                    if (u.isPresent()) {
                        pg.add(u.get());
                    } else {
                        return NOptional.ofError(
                                () -> NMsg.ofC("Error parsing page group : %s", e)
                        );
                    }
                }
            }
        }
        return NOptional.of(pg);
    }
}
