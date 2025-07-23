package net.thevpc.ndoc.elem.base.line.arc;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.base.format.ToElementHelper;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;

public class NDocArcParser extends NDocNodeParserBase {
    public NDocArcParser() {
        super(false, NDocNodeType.ARC);
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
//                        case HPropName.END_ARROW:
//                        case HPropName.START_ARROW:
//                        case HPropName.START_ANGLE:
//                        case HPropName.END_ANGLE:
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
                ).addChildProps(new String[]{NDocPropName.FROM, NDocPropName.TO
//                        , HPropName.START_ARROW, HPropName.END_ARROW
//                        , HPropName.START_ANGLE, HPropName.END_ANGLE
                })
                .build();
    }
}
