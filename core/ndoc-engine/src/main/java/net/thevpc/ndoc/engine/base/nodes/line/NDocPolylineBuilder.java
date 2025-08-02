package net.thevpc.ndoc.elem.base.line;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2DFactory;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.engine.NDocEngineTools;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class NDocPolylineBuilder implements NDocNodeCustomBuilder {
    private NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.POLYLINE)
                .parseParam((info, buildContext) -> {
                    NElement k = info.peek();
                    NDocEngineTools tools = buildContext.engine().tools();
                    if (k != null) {
                        if(k.isNamedPair()) {
                            NPairElement kk=k.asPair().get();
                            String ks = kk.key().asStringValue().orNull();
                            if (NDocPropName.POINT.equals(ks)) {
                                buildContext.addParamName(NDocPropName.POINTS);
                                if (buildContext.isAncestorScene3D(info.node())) {
                                    NOptional<NDocPoint3D> p2d = NDocValue.of(kk.value()).asHPoint3D();
                                    if (p2d.isPresent()) {
                                        tools.addPoint(info.node(), p2d.get());
                                        info.read();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    NOptional<NDocPoint2D> p2d = NDocValue.of(kk.value()).asHPoint2D();
                                    if (p2d.isPresent()) {
                                        tools.addPoint(info.node(), p2d.get());
                                        info.read();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            } else if (NDocPropName.POINTS.equals(ks)) {
                                buildContext.addParamName(NDocPropName.POINTS);
                                if (buildContext.isAncestorScene3D(info.node())) {
                                    NOptional<NDocPoint3D[]> p2d = NDocValue.of(kk.value()).asHPoint3DArray();
                                    if (p2d.isPresent()) {
                                        tools.addPoints(info.node(), p2d.get());
                                        info.read();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    NOptional<NDocPoint2D[]> p2d = NDocValue.of(kk.value()).asHPoint2DArray();
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
                                NOptional<NDocPoint3D> p2d = NDocValue.of(k).asHPoint3D();
                                if (p2d.isPresent()) {
                                    buildContext.addParamName(NDocPropName.POINTS);
                                    tools.addPoint(info.node(), p2d.get());
                                    info.read();
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                NOptional<NDocPoint2D> p2d = NDocValue.of(k).asHPoint2D();
                                if (p2d.isPresent()) {
                                    buildContext.addParamName(NDocPropName.POINTS);
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

    private void render(NDocNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext buildContext) {

        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = rendererContext.selfBounds(p);
        NDocGraphics g = rendererContext.graphics();
        NDocPoint2D[] points = NDocValue.ofProp(p, NDocPropName.POINTS).asHPoint2DArray().get();
        if (!rendererContext.isDry()) {
            Paint fc = rendererContext.getForegroundColor(p, true);
            g.draw2D(NDocElement2DFactory.polyline(points)
                    .setLineStroke(rendererContext.resolveStroke(p))
                    .setLinePaint(fc));
        }
        rendererContext.paintDebugBox(p, b);
    }

}
