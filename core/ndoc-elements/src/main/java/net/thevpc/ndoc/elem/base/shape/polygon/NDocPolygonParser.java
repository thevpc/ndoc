package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.model.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.HPropUtils;
import net.thevpc.ndoc.api.style.NDocProp;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NArrayElement;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public class NDocPolygonParser extends NDocNodeParserBase {
    public NDocPolygonParser() {
        super(false, NDocNodeType.POLYGON);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case INT:
            case LONG: {
                info.node.setProperty(NDocPropName.COUNT, NDocUtils.addCompilerDeclarationPath( NDocObjEx.of(info.currentArg).asTsonInt().get(), info.context.source()));
                return true;
            }
            case PAIR: {
                NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    NDocObjEx.SimplePair spp = sp.get();
                    NDocObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case "count": {
                            info.node.setProperty(NDocPropName.COUNT, v.asTsonInt().get());
                            return true;
                        }
                        case "point": {
                            if (isAncestorScene3D(info.node)) {
                                NOptional<NDocPoint3D> p2d = v.asHPoint3D();
                                if (p2d.isPresent()) {
                                    HPropUtils.addPoint(info.node, p2d.get());
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                NOptional<NDocPoint2D> p2d = v.asHPoint2D();
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
                                info.node.setProperty(NDocProp.ofHPoint3DArray(NDocPropName.POINTS, v.asHPoint3DArray().get()));
                            } else {
                                info.node.setProperty(NDocProp.ofHPoint2DArray(NDocPropName.POINTS, v.asHPoint2DArray().get()));
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
                    NOptional<NDocPoint2D> p2d = NDocObjEx.of(info.currentArg.asPair().get().value()).asHPoint2D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(info.node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<NDocPoint3D> p2d = NDocObjEx.of(info.currentArg.asPair().get().value()).asHPoint3D();
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
    public NElement toElem(NDocNode item) {
        NElement count = item.getPropertyValue(NDocPropName.COUNT).orNull();
        Integer o = count == null ? null : NDocObjEx.of(count).asInt().orNull();
        if (o != null && o <= 1) {
            o = null;
        }

        NElement points = item.getPropertyValue(NDocPropName.POINTS).orNull();
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
                        count == null ? null : NElement.ofPair("count", count),
                        points == null ? null : NElement.ofPair("points", points)
                )
                .build();
    }

}
