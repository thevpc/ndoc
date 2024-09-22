package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HRectangleParser extends HNodeParserBase {

    public HRectangleParser() {
        super(false, HNodeType.RECTANGLE);
    }


    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                TsonPair pp = info.currentArg.toPair();
                TsonElement k = pp.key();
                TsonElement v = pp.value();
                ObjEx ph = ObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {
                    String uid = net.thevpc.halfa.api.util.HUtils.uid(n.get());
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
                ObjEx h = ObjEx.of(info.currentArg);
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
