package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.engine.parser.styles.HStyleParser;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public class HItemListParser {
    public static NOptional<HItemList> readHItemList(TsonElement ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HItemList pg = new HItemList();

        switch (ff.type()) {
            case FUNCTION:
            case OBJECT:
            case ARRAY: {
                ObjEx ee = new ObjEx(ff);
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
                for (TsonElement e : ee.children()) {
                    NOptional<HItem> u = context.engine().newNode(e, context);
                    if (u.isPresent()) {
                        pg.add(u.get());
                    } else {
                        return NOptional.ofError(
                                s -> NMsg.ofC("Error parsing page group : %s", e)
                        );
                    }
                }
            }
        }
        return NOptional.of(pg);
    }
}
