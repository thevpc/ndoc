package net.thevpc.ndoc.elem.base.shape.polyline;

import net.thevpc.ndoc.api.parser.ParseArgumentInfo;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.HPropUtils;
import  net.thevpc.ndoc.api.document.style.NDocProp;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.util.ToElementHelper;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.elem.NArrayElement;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

public class NDocPolylineParser extends NDocNodeParserBase {
    public NDocPolylineParser() {
        super(false, NDocNodeType.POLYLINE);
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
                    NOptional<NDocPoint3D> p2d = NDocObjEx.of(info.currentArg.asPair().get().value()).asHPoint3D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(info.node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<NDocPoint2D> p2d = NDocObjEx.of(info.currentArg.asPair().get().value()).asHPoint2D();
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
        NElement points = item.getPropertyValue(NDocPropName.POINTS).orNull();
        if (points != null) {
            if (points instanceof NArrayElement && ((NArrayElement) points).isEmpty()) {
                points = null;
            }
        }
        return ToElementHelper.of(
                        item, engine()
                ).addChildren(
                        points == null ? null : NElement.ofPair("points", points)
                )
                .build();
    }

}
