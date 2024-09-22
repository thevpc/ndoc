package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropUtils;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonArray;
import net.thevpc.tson.TsonElement;

public class HPolygonParser extends HNodeParserBase {
    public HPolygonParser() {
        super(false, HNodeType.POLYGON);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case INT:
            case LONG: {
                info.node.setProperty(HPropName.COUNT, ObjEx.of(info.currentArg).asTsonInt().get());
                return true;
            }
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "count": {
                            info.node.setProperty(HPropName.COUNT, v.asTsonInt().get());
                            return true;
                        }
                        case "point": {
                            if (isAncestorScene3D(info.node)) {
                                NOptional<HPoint3D> p2d = v.asHPoint3D();
                                if (p2d.isPresent()) {
                                    HPropUtils.addPoint(info.node, p2d.get());
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                NOptional<HPoint2D> p2d = v.asHPoint2D();
                                if (p2d.isPresent()) {
                                    HPropUtils.addPoint(info.node, p2d.get());
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                        case "points": {
                            if (isAncestorScene3D(info.node)) {
                                info.node.setProperty(HProp.ofHPoint3DArray(HPropName.POINTS, v.asHPoint3DArray().get()));
                            } else {
                                info.node.setProperty(HProp.ofHPoint2DArray(HPropName.POINTS, v.asHPoint2DArray().get()));
                            }
                            return true;
                        }
                    }
                }
                break;
            }
            case UPLET: {
                if (isAncestorScene3D(info.node)) {
                    NOptional<HPoint2D> p2d = ObjEx.of(info.currentArg.toPair().value()).asHPoint2D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(info.node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<HPoint3D> p2d = ObjEx.of(info.currentArg.toPair().value()).asHPoint3D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(info.node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return super.processArgument(info);
    }

    @Override
    public TsonElement toTson(HNode item) {
        TsonElement count = item.getPropertyValue(HPropName.COUNT).orNull();
        Integer o = count == null ? null : ObjEx.of(count).asInt().orNull();
        if (o != null && o <= 1) {
            o = null;
        }

        TsonElement points = item.getPropertyValue(HPropName.POINTS).orNull();
        if (points != null) {
            if (((TsonArray) points).isEmpty()) {
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
                        count == null ? null : Tson.ofPair("count", count),
                        points == null ? null : Tson.ofPair("points", points)
                )
                .build();
    }

}
