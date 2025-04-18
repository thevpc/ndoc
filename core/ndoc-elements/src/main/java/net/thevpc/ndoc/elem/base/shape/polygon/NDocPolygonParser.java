package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropUtils;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NArrayElement;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.util.NOptional;

public class NDocPolygonParser extends NDocNodeParserBase {
    public NDocPolygonParser() {
        super(false, HNodeType.POLYGON);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case INTEGER:
            case LONG: {
                info.node.setProperty(HPropName.COUNT, NDocObjEx.of(info.currentArg).asTsonInt().get());
                return true;
            }
            case PAIR: {
                NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    NDocObjEx.SimplePair spp = sp.get();
                    NDocObjEx v = spp.getValue();
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
            case UPLET:
            case NAMED_UPLET:
            {
                if (isAncestorScene3D(info.node)) {
                    NOptional<HPoint2D> p2d = NDocObjEx.of(info.currentArg.asPair().get().value()).asHPoint2D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(info.node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<HPoint3D> p2d = NDocObjEx.of(info.currentArg.asPair().get().value()).asHPoint3D();
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
    public NElement toElem(HNode item) {
        NElement count = item.getPropertyValue(HPropName.COUNT).orNull();
        Integer o = count == null ? null : NDocObjEx.of(count).asInt().orNull();
        if (o != null && o <= 1) {
            o = null;
        }

        NElement points = item.getPropertyValue(HPropName.POINTS).orNull();
        if (points != null) {
            if (((NArrayElement) points).isEmpty()) {
                points = null;
            }
        }
        if (points != null) {
            count = null;
        }
        return ToElementHelper.of(
                        item,
                        engine()
                ).addChildren(
                        count == null ? null : NElements.of().ofPair("count", count),
                        points == null ? null : NElements.of().ofPair("points", points)
                )
                .build();
    }

}
