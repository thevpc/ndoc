package net.thevpc.ndoc.elem.base.line.cubiccurve;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;

public class NDocCubicCurveParser extends NDocNodeParserBase {
    public NDocCubicCurveParser() {
        super(false, HNodeType.CUBIC_CURVE);
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
                    String uid = HUtils.uid(n.get());
                    switch (uid) {
                        case HPropName.FROM:
                        case HPropName.TO:
                        case HPropName.END_ARROW:
                        case HPropName.START_ARROW:
                        case HPropName.CTRL1:
                        case HPropName.CTRL2:
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
    public NElement toElem(HNode item) {
        return ToElementHelper.of(
                        item, engine()
                ).addChildProps(new String[]{HPropName.FROM, HPropName.TO, HPropName.START_ARROW
                        , HPropName.END_ARROW
                        , HPropName.CTRL1
                        , HPropName.CTRL2
                })
                .build();
    }

}
