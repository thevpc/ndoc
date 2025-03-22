package net.thevpc.ndoc.elem.base.shape.polyline;

import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropUtils;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.base.format.ToTsonHelper;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonArray;
import net.thevpc.tson.TsonElement;

public class NDocPolylineParser extends NDocNodeParserBase {
    public NDocPolylineParser() {
        super(false, HNodeType.POLYLINE);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    NDocObjEx.SimplePair spp = sp.get();
                    NDocObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
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
                            return false;
                        }
                    }
                }
                break;
            }
            case UPLET:
            case NAMED_UPLET:
            {
                if (isAncestorScene3D(info.node)) {
                    NOptional<HPoint3D> p2d = NDocObjEx.of(info.currentArg.toPair().value()).asHPoint3D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(info.node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<HPoint2D> p2d = NDocObjEx.of(info.currentArg.toPair().value()).asHPoint2D();
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
        TsonElement points = item.getPropertyValue(HPropName.POINTS).orNull();
        if (points != null) {
            if (points instanceof TsonArray && ((TsonArray) points).isEmpty()) {
                points = null;
            }
        }
        return ToTsonHelper.of(
                        item, engine()
                ).addChildren(
                        points == null ? null : Tson.ofPair("points", points)
                )
                .build();
    }

}
