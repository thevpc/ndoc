package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class HCylinderImpl extends AbstractHNodeTypeFactory {

    public HCylinderImpl(){
        super(false, HNodeType.CYLINDER);
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
                        case "ellipse-height": {
                            node.setProperty(HProp.ofDouble(HPropName.ELLIPSE_H, v.asDouble().get()));
                            return true;
                        }

                        case "top-color": {
                            node.setProperty(HProp.ofObject(HPropName.TOP_COLOR, v.asColor().get()));
                            return true;
                        }
                        case "segment-count": {
                            node.setProperty(HProp.ofInt(HPropName.SEGMENT_COUNT, v.asInt().get()));
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
    public TsonElement toTson (HNode item) {
        HProp ellipseHeight = item.getProperty(HPropName.ELLIPSE_H).orNull();
        HProp topColor = item.getProperty(HPropName.TOP_COLOR).orNull();
        HProp segmentCount = item.getProperty(HPropName.SEGMENT_COUNT).orNull();


        return ToTsonHelper.of(
                        item,
                        engine()
                ).addChildren(
                        ellipseHeight == null ? null : Tson.ofPair("ellipse-height", HUtils.toTson(ellipseHeight.getValue())),
                        topColor == null ? null : Tson.ofPair("top-color", HUtils.toTson(topColor.getValue())),
                        segmentCount == null ? null : Tson.ofPair("segment-count", HUtils.toTson(segmentCount.getValue()))

                )
                .build();
    }
}
