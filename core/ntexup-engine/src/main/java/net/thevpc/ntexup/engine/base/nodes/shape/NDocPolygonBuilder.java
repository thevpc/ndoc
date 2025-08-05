package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.engine.NDocEngineTools;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.parser.NDocArgumentReader;
import net.thevpc.ntexup.api.document.elem2d.NDocPoint2D;
import net.thevpc.ntexup.api.document.elem3d.NDocPoint3D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;

import java.util.Arrays;

public class NDocPolygonBuilder implements NDocNodeBuilder {
    private NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.POLYGON)
                .parseParam().named(NDocPropName.COUNT).then()
                .parseParam(new NDocNodeCustomBuilderContext.ProcessParamAction() {
                    @Override
                    public boolean processParam(NDocArgumentReader info, NDocNodeCustomBuilderContext buildContext) {
                        NElement k = info.peek();
                        NDocEngineTools tools = buildContext.engine().tools();
                        if (k != null) {
                            if(k.isInt()) {
                                info.read();
                                info.node().setProperty(NDocPropName.COUNT,k);
                                return true;
                            }else if(k.isNamedPair()) {
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
                    }
                })
                .renderComponent(this::render);
    }

    private void render(NTxNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext buildContext) {
        Integer count = NDocValue.ofProp(p, NDocPropName.COUNT).asInt().orNull();
        if (buildContext.isAncestorScene3D(p)) {
            //really, where is 3D!!
            NDocPoint2D[] points = NDocValue.ofProp(p, NDocPropName.POINTS).asHPoint2DArray().orNull();
            if(points!=null && points.length>=3) {
                if (count != null && count >= 3 && count > points.length) {
                    points = Arrays.copyOf(points, count);
                }
                NDocPolygonHelper.renderPoints(p, points, rendererContext);
            }else{
                NDocPolygonHelper.renderPointsCount(count==null?3:count.intValue(),p, rendererContext, defaultStyles);
            }
        }else{
            NDocPoint2D[] points = NDocValue.ofProp(p, NDocPropName.POINTS).asHPoint2DArray().orNull();
            if(points!=null && points.length>=3) {
                if (count != null && count >= 3 && count > points.length) {
                    points = Arrays.copyOf(points, count);
                }
                NDocPolygonHelper.renderPoints(p, points, rendererContext);
            }else{
                NDocPolygonHelper.renderPointsCount(count==null?3:count.intValue(),p, rendererContext, defaultStyles);
            }
        }
    }

}
