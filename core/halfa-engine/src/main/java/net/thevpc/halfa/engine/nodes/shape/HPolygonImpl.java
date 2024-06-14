package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
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

public class HPolygonImpl extends AbstractHNodeTypeFactory {
    public HPolygonImpl() {
        super(false, HNodeType.POLYGON);
    }

    @Override
    protected boolean processArg(String id, HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case INT:
            case LONG: {
                p.setProperty(HProp.ofInt(HPropName.COUNT, new ObjEx(e).asInt().get()));
                return true;
            }
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = new ObjEx(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "count": {
                            p.setProperty(HProp.ofInt(HPropName.COUNT, v.asInt().get()));
                            return true;
                        }
                        case "point": {
                            if (isAncestorScene3D(p)) {
                                NOptional<HPoint3D> p2d = v.asHPoint3D();
                                if (p2d.isPresent()) {
                                    HPropUtils.addPoint(p, p2d.get());
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                NOptional<HPoint2D> p2d = v.asHPoint2D();
                                if (p2d.isPresent()) {
                                    HPropUtils.addPoint(p, p2d.get());
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                        case "points": {
                            if (isAncestorScene3D(p)) {
                                p.setProperty(HProp.ofHPoint3DArray(HPropName.POINTS, v.asHPoint3DArray().get()));
                            } else {
                                p.setProperty(HProp.ofHPoint2DArray(HPropName.POINTS, v.asHPoint2DArray().get()));
                            }
                            return false;
                        }
                    }
                }
                break;
            }
            case UPLET: {
                if (isAncestorScene3D(p)) {
                    NOptional<HPoint2D> p2d = new ObjEx(e.toPair().getValue()).asHPoint2D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(p, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<HPoint3D> p2d = new ObjEx(e.toPair().getValue()).asHPoint3D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(p, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public TsonElement toTson(HNode item) {
        HProp count = item.getProperty(HPropName.COUNT).orNull();
        if (count != null && count.getValue() == null) {
            count = null;
        }
        Integer o = count == null ? null : (Integer) count.getValue();
        if (o != null && o <= 1) {
            o = null;
        }

        HProp points = item.getProperty(HPropName.POINTS).orNull();
        if (points != null) {
            if (points.getValue() == null) {
                points = null;
            } else if (((Object[]) points.getValue()).length == 0) {
                points = null;
            }
        }
        if (points != null) {
            count = null;
        }
        return ToTsonHelper.of(
                        item,
                        engine()
                ).addChildren(
                        count == null ? null : Tson.ofPair("count", HUtils.toTson(count.getValue())),
                        points == null ? null : Tson.ofPair("points", HUtils.toTson(points.getValue()))
                )
                .build();
    }

}
