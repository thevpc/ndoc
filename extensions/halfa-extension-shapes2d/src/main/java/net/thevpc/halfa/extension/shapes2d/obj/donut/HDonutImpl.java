package net.thevpc.halfa.engine.nodes.elem2d.shape.donut;

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
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
public class HDonutImpl extends HNodeParserBase {
    public HDonutImpl(){
        super(false, HNodeType.DONUT);
    }

    @Override
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "inner-radius": {
                            node.setProperty(HProp.ofDouble(HPropName.INNER_RADIUS, v.asDouble().get()));
                            return true;
                        }
                        case "start-angle": {
                            node.setProperty(HProp.ofDouble(HPropName.START_ANGLE, v.asDouble().get()));
                            return true;
                        }
                        case "extent-angle": {
                            node.setProperty(HProp.ofDouble(HPropName.EXTENT_ANGLE, v.asDouble().get()));
                            return true;
                        }

                    }
                }
                break;
            }
        }
        return super.processArg(id, node, e, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {

        HProp innerRadius = item.getProperty(HPropName.INNER_RADIUS).orNull();
        HProp startAngle = item.getProperty(HPropName.START_ANGLE).orNull();
        HProp extentAngle = item.getProperty(HPropName.EXTENT_ANGLE).orNull();


        return ToTsonHelper.of(
                        item,
                        engine()
                ).addChildren(
                        innerRadius == null ? null : Tson.ofPair("inner-radius", HUtils.toTson(innerRadius.getValue())),
                        innerRadius == null ? null : Tson.ofPair("start-angle", HUtils.toTson(startAngle.getValue())),
                        innerRadius == null ? null : Tson.ofPair("extent-angle", HUtils.toTson(extentAngle.getValue()))
                )
                .build();
    }
}
