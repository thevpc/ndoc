package net.thevpc.ndoc.elem.base.line.line;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.base.format.ToTsonHelper;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class NDocLineParser extends NDocNodeParserBase {
    public NDocLineParser() {
        super(false, HNodeType.LINE);
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
                    String uid = HUtils.uid(n.get());
                    switch (uid) {
                        case HPropName.FROM:
                        case HPropName.TO:
                        case HPropName.END_ARROW:
                        case HPropName.START_ARROW: {
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
    public TsonElement toTson(HNode item) {
        return ToTsonHelper.of(
                        item, engine()
                ).addChildProps(new String[]{HPropName.FROM, HPropName.TO, HPropName.START_ARROW, HPropName.END_ARROW})
                .build();
    }

}
