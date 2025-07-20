package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

public class NDocRectangleParser extends NDocNodeParserBase {

    public NDocRectangleParser() {
        super(false, NDocNodeType.RECTANGLE);
    }


    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                NPairElement pp = info.currentArg.asPair().get();
                NElement k = pp.key();
                NElement v = pp.value();
                NDocObjEx ph = NDocObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {
                    String uid = NDocUtils.uid(n.get());
                    switch (uid) {
                        case NDocPropName.ROUND_CORNER:
                        case NDocPropName.THEED:
                        case NDocPropName.RAISED:
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
                    String uid = NDocUtils.uid(u.get());
                    switch (uid) {
                        case NDocPropName.ROUND_CORNER:
                        case NDocPropName.THEED:
                        case NDocPropName.RAISED:
                        {
                            info.node.setProperty(uid, NElement.ofBoolean(true));
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
