package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.style.HPropUtils;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class HPolylineImpl extends AbstractHNodeTypeFactory {
    public HPolylineImpl() {
        super(false, HNodeType.POLYLINE);
    }

    @Override
    protected boolean processArg(String id, HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = new ObjEx(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "point": {
                            NOptional<Double2> p2d = v.asDouble2();
                            if (p2d.isPresent()) {
                                HPropUtils.addPoint(p, p2d.get());
                                return true;
                            } else {
                                return false;
                            }
                        }
                        case "points": {
                            p.setProperty(HProp.ofDouble2Array(HPropName.POINTS, v.asDouble2Array().get()));
                            return false;
                        }
                    }
                }
                break;
            }
            case UPLET: {
                NOptional<Double2> p2d = new ObjEx(e.toPair().getValue()).asDouble2();
                if (p2d.isPresent()) {
                    HPropUtils.addPoint(p, p2d.get());
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public TsonElement toTson(HNode item) {
        HNode node = (HNode) item;
        HProp points = node.getProperty(HPropName.POINTS).orNull();
        if (points != null) {
            if (points.getValue() == null) {
                points = null;
            } else if (((Double2[]) points.getValue()).length == 0) {
                points = null;
            }
        }
        return ToTsonHelper.of(
                        node,engine()
                ).addChildren(
                        points == null ? null : Tson.ofPair("points", HUtils.toTson(points))
                )
                .build();
    }

}
