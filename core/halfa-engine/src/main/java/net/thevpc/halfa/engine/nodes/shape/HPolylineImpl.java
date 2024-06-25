package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.style.HPropUtils;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.eval.ObjEx;
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
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "point": {
                            if (isAncestorScene3D(node)) {
                                NOptional<HPoint3D> p2d = v.asHPoint3D();
                                if (p2d.isPresent()) {
                                    HPropUtils.addPoint(node, p2d.get());
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                NOptional<HPoint2D> p2d = v.asHPoint2D();
                                if (p2d.isPresent()) {
                                    HPropUtils.addPoint(node, p2d.get());
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                        case "points": {
                            if (isAncestorScene3D(node)) {
                                node.setProperty(HProp.ofHPoint3DArray(HPropName.POINTS, v.asHPoint3DArray().get()));
                            } else {
                                node.setProperty(HProp.ofHPoint2DArray(HPropName.POINTS, v.asHPoint2DArray().get()));
                            }
                            return false;
                        }
                    }
                }
                break;
            }
            case UPLET: {
                if (isAncestorScene3D(node)) {
                    NOptional<HPoint3D> p2d = ObjEx.of(e.toPair().getValue()).asHPoint3D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<HPoint2D> p2d = ObjEx.of(e.toPair().getValue()).asHPoint2D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return super.processArg(id, node, e, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {
        HProp points = item.getProperty(HPropName.POINTS).orNull();
        if (points != null) {
            Object v = points.getValue();
            if (v == null) {
                points = null;
            } else if (v instanceof Object[] && ((Object[]) v).length == 0) {
                points = null;
            }
        }
        return ToTsonHelper.of(
                        item, engine()
                ).addChildren(
                        points == null ? null : Tson.ofPair("points", HUtils.toTson(points))
                )
                .build();
    }

}
