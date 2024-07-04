package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HRectangleParser extends HNodeParserBase {

    public HRectangleParser() {
        super(false, HNodeType.RECTANGLE);
    }


    @Override
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                TsonPair pp = e.toPair();
                TsonElement k = pp.getKey();
                TsonElement v = pp.getValue();
                ObjEx ph = ObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {
                    String uid = HUtils.uid(n.get());
                    switch (uid) {
                        case HPropName.ROUND_CORNER:
                        case HPropName.THEED:
                        case HPropName.RAISED:
                        {
                            node.setProperty(new HProp(uid, v));
                            return true;
                        }
                    }
                }
                break;
            }
            case NAME: {
                ObjEx h = ObjEx.of(e);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HUtils.uid(u.get());
                    switch (uid) {
                        case HPropName.ROUND_CORNER:
                        case HPropName.THEED:
                        case HPropName.RAISED:
                        {
                            node.setProperty(uid, Tson.of(true));
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArg(id, node, e, f, context);
    }
}
