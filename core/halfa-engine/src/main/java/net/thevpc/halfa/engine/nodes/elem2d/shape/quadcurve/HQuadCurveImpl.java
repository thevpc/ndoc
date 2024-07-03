package net.thevpc.halfa.engine.nodes.elem2d.shape.quadcurve;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engin.spibase.format.ToTsonHelper;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

public class HQuadCurveImpl extends HNodeParserBase {
    public HQuadCurveImpl() {
        super(false, HNodeType.QUAD_CURVE);
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
                        case HPropName.FROM:
                        case HPropName.TO:
                        case HPropName.END_ARROW:
                        case HPropName.START_ARROW:
                        case HPropName.CTRL:
                        {
                            node.setProperty(new HProp(uid, v));
                            return true;
                        }
                    }
                }
            }
        }
        return super.processArg(id, node, e, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper.of(
                        item, engine()
                ).addChildProps(new String[]{HPropName.FROM, HPropName.TO, HPropName.START_ARROW
                        , HPropName.END_ARROW
                        , HPropName.CTRL
                })
                .build();
    }

}
