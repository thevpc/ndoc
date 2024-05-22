package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.item.HItemList;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.api.node.HPageGroup;
import net.thevpc.halfa.api.style.HStyle;
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
            case ARRAY:
            {
                TsonElementExt ee=new TsonElementExt(ff);
                for (TsonElement e : ee.args()) {
                    NOptional<HItem> u = HStyleParser.parseStyle(e, f, context);
                    if (u.isPresent()) {
                        pg.add(u.get());
                    }else{
                        return NOptional.ofNamedError("invalid " + e+" for page-group");
                    }
                }
                for (TsonElement e : ee.children()) {
                    NOptional<HItem> u = context.engine().newDocumentChild(e, null, context);
                    if(u.isPresent()){
                        pg.add(u.get());
                    }else{
                        u = context.engine().newPageChild(e, null, context);
                        if(u.isPresent()){
                            pg.add(u.get());
                        }else{
                            return NOptional.ofError(
                                    s -> NMsg.ofC("Error parsing page group : %s", e)
                            );
                        }
                    }
                }
            }
        }
        return NOptional.of(pg);
    }
}
