package net.thevpc.ndoc.elem.base.line.quadcurve;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;

public class NDocQuadCurveParser extends NDocNodeParserBase {
    public NDocQuadCurveParser() {
        super(false, NDocNodeType.QUAD_CURVE);
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
                        case NDocPropName.FROM:
                        case NDocPropName.TO:
                        case NDocPropName.END_ARROW:
                        case NDocPropName.START_ARROW:
                        case NDocPropName.CTRL:
                        {
                            info.node.setProperty(uid, v);
                            return true;
                        }
                    }
                }
            }
        }
        return super.processArgument(info);
    }

    @Override
    public NElement toElem(NDocNode item) {
        return ToElementHelper.of(
                        item, engine()
                ).addChildProps(new String[]{NDocPropName.FROM, NDocPropName.TO, NDocPropName.START_ARROW
                        , NDocPropName.END_ARROW
                        , NDocPropName.CTRL
                })
                .build();
    }

}
