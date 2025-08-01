package net.thevpc.ndoc.elem.base.shape;

import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.engine.NDocEngineTools;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentReader;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NOptional;

import java.util.Arrays;

public class NDocPolygonBuilder implements NDocNodeCustomBuilder {
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
                                        NOptional<NDocPoint3D> p2d = NDocObjEx.of(kk.value()).asHPoint3D();
                                        if (p2d.isPresent()) {
                                            tools.addPoint(info.node(), p2d.get());
                                            info.read();
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        NOptional<NDocPoint2D> p2d = NDocObjEx.of(kk.value()).asHPoint2D();
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
                                        NOptional<NDocPoint3D[]> p2d = NDocObjEx.of(kk.value()).asHPoint3DArray();
                                        if (p2d.isPresent()) {
                                            tools.addPoints(info.node(), p2d.get());
                                            info.read();
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        NOptional<NDocPoint2D[]> p2d = NDocObjEx.of(kk.value()).asHPoint2DArray();
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
                                    NOptional<NDocPoint3D> p2d = NDocObjEx.of(k).asHPoint3D();
                                    if (p2d.isPresent()) {
                                        buildContext.addParamName(NDocPropName.POINTS);
                                        tools.addPoint(info.node(), p2d.get());
                                        info.read();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    NOptional<NDocPoint2D> p2d = NDocObjEx.of(k).asHPoint2D();
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

    private void render(NDocNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext buildContext) {
        Integer count = NDocObjEx.ofProp(p, NDocPropName.COUNT).asInt().orNull();
        if (buildContext.isAncestorScene3D(p)) {
            //really, where is 3D!!
            NDocPoint2D[] points = NDocObjEx.ofProp(p, NDocPropName.POINTS).asHPoint2DArray().orNull();
            if(points!=null && points.length>=3) {
                if (count != null && count >= 3 && count > points.length) {
                    points = Arrays.copyOf(points, count);
                }
                NDocPolygonHelper.renderPoints(p, points, rendererContext);
            }else{
                NDocPolygonHelper.renderPointsCount(count==null?3:count.intValue(),p, rendererContext, defaultStyles);
            }
        }else{
            NDocPoint2D[] points = NDocObjEx.ofProp(p, NDocPropName.POINTS).asHPoint2DArray().orNull();
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
