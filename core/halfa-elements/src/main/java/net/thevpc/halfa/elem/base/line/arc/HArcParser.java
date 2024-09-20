package net.thevpc.halfa.elem.base.line.arc;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HArcParser extends HNodeParserBase {
    public HArcParser() {
        super(false, HNodeType.ARC);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case PAIR: {
                TsonPair pp = currentArg.toPair();
                TsonElement k = pp.key();
                TsonElement v = pp.value();
                ObjEx ph = ObjEx.of(k);
                NOptional<String> n = ph.asStringOrName();
                if (n.isPresent()) {
                    String uid = HUtils.uid(n.get());
                    switch (uid) {
                        case HPropName.FROM:
                        case HPropName.TO:
//                        case HPropName.END_ARROW:
//                        case HPropName.START_ARROW:
//                        case HPropName.START_ANGLE:
//                        case HPropName.END_ANGLE:
                        {
                            node.setProperty(uid, v);
                            return true;
                        }
                    }
                }
            }
        }
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper.of(
                        item, engine()
                ).addChildProps(new String[]{HPropName.FROM, HPropName.TO
//                        , HPropName.START_ARROW, HPropName.END_ARROW
//                        , HPropName.START_ANGLE, HPropName.END_ANGLE
                })
                .build();
    }
}
