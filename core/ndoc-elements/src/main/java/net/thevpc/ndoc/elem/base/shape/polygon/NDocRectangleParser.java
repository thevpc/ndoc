package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class NDocRectangleParser extends NDocNodeParserBase {

    public NDocRectangleParser() {
        super(false, HNodeType.RECTANGLE);
    }


    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                TsonPair pp = info.currentArg.toPair();
                TsonElement k = pp.key();
                TsonElement v = pp.value();
                NDocObjEx ph = NDocObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {
                    String uid = net.thevpc.ndoc.api.util.HUtils.uid(n.get());
                    switch (uid) {
                        case HPropName.ROUND_CORNER:
                        case HPropName.THEED:
                        case HPropName.RAISED:
                        {
                            info.node.setProperty(uid, v);
                            return true;
                        }
                    }
                }
                break;
            }
            case NAME: {
                NDocObjEx h = NDocObjEx.of(info.currentArg);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String uid = HUtils.uid(u.get());
                    switch (uid) {
                        case HPropName.ROUND_CORNER:
                        case HPropName.THEED:
                        case HPropName.RAISED:
                        {
                            info.node.setProperty(uid, Tson.of(true));
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArgument(info);
    }
}
