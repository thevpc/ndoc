package net.thevpc.ntexup.engine.base.nodes.line;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DFactory;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;
import net.thevpc.ntexup.api.document.elem3d.NTxPoint3D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.engine.NTxEngineTools;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class NTxPolylineBuilder implements NTxNodeBuilder {
    private NTxProperties defaultStyles = new NTxProperties();
    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.POLYLINE)
                .parseParam((info, buildContext) -> {
                    NElement k = info.peek();
                    NTxEngineTools tools = buildContext.engine().tools();
                    if (k != null) {
                        if(k.isNamedPair()) {
                            NPairElement kk=k.asPair().get();
                            String ks = kk.key().asStringValue().orNull();
                            if (NTxPropName.POINT.equals(ks)) {
                                buildContext.addParamName(NTxPropName.POINTS);
                                if (buildContext.isAncestorScene3D(info.node())) {
                                    NOptional<NTxPoint3D> p2d = NTxValue.of(kk.value()).asHPoint3D();
                                    if (p2d.isPresent()) {
                                        tools.addPoint(info.node(), p2d.get());
                                        info.read();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    NOptional<NTxPoint2D> p2d = NTxValue.of(kk.value()).asHPoint2D();
                                    if (p2d.isPresent()) {
                                        tools.addPoint(info.node(), p2d.get());
                                        info.read();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            } else if (NTxPropName.POINTS.equals(ks)) {
                                buildContext.addParamName(NTxPropName.POINTS);
                                if (buildContext.isAncestorScene3D(info.node())) {
                                    NOptional<NTxPoint3D[]> p2d = NTxValue.of(kk.value()).asHPoint3DArray();
                                    if (p2d.isPresent()) {
                                        tools.addPoints(info.node(), p2d.get());
                                        info.read();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    NOptional<NTxPoint2D[]> p2d = NTxValue.of(kk.value()).asHPoint2DArray();
                                    if (p2d.isPresent()) {
                                        info.read();
                                        tools.addPoints(info.node(), p2d.get());
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        }else if(k.isUplet()){
                            if (buildContext.isAncestorScene3D(info.node())) {
                                NOptional<NTxPoint3D> p2d = NTxValue.of(k).asHPoint3D();
                                if (p2d.isPresent()) {
                                    buildContext.addParamName(NTxPropName.POINTS);
                                    tools.addPoint(info.node(), p2d.get());
                                    info.read();
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                NOptional<NTxPoint2D> p2d = NTxValue.of(k).asHPoint2D();
                                if (p2d.isPresent()) {
                                    buildContext.addParamName(NTxPropName.POINTS);
                                    tools.addPoint(info.node(), p2d.get());
                                    info.read();
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                    }
                    return false;
                })
                .renderComponent(this::render);
    }

    private void render(NTxNode p, NTxNodeRendererContext rendererContext, NTxNodeCustomBuilderContext buildContext) {

        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NTxBounds2 b = rendererContext.selfBounds(p);
        NTxGraphics g = rendererContext.graphics();
        NTxPoint2D[] points = NTxValue.ofProp(p, NTxPropName.POINTS).asHPoint2DArray().get();
        if (!rendererContext.isDry()) {
            Paint fc = rendererContext.getForegroundColor(p, true);
            g.draw2D(NTxElement2DFactory.polyline(points)
                    .setLineStroke(rendererContext.resolveStroke(p))
                    .setLinePaint(fc));
        }
        rendererContext.paintDebugBox(p, b);
    }

}
