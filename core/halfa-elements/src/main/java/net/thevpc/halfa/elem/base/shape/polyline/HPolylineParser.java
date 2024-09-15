package net.thevpc.halfa.elem.base.shape.polyline;

import net.thevpc.halfa.HDocumentFactory;
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
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonArray;
import net.thevpc.tson.TsonElement;

public class HPolylineParser extends HNodeParserBase {
    public HPolylineParser() {
        super(false, HNodeType.POLYLINE);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(currentArg).asSimplePair();
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
                    NOptional<HPoint3D> p2d = ObjEx.of(currentArg.toPair().value()).asHPoint3D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    NOptional<HPoint2D> p2d = ObjEx.of(currentArg.toPair().value()).asHPoint2D();
                    if (p2d.isPresent()) {
                        HPropUtils.addPoint(node, p2d.get());
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
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
