package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

public class NDocItemListParser {
    public static NOptional<NDocItemList> readHItemList(NElement ff, NDocDocumentFactory f, NDocNodeFactoryParseContext context) {
        NDocItemList pg = new NDocItemList();

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
                NDocValue ee = NDocValue.of(ff);
                for (NElement e : ee.args()) {
                    NOptional<NDocProp[]> u = HStyleParser.parseStyle(e, f, context);
                    if (u.isPresent()) {
                        for (NDocProp s : u.get()) {
                            pg.add(s);
                        }
                    } else {
                        return NOptional.ofNamedError("invalid " + e + " for page-group");
                    }
                }
                for (NElement e : ee.body()) {
                    NOptional<NDocItem> u = context.engine().newNode(e, context);
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
