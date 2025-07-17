package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.eval.NDocObjEx;

public class NDocPolygonRenderer extends NDocPolygonBaseRenderer {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocPolygonRenderer() {
        super(
                NDocNodeType.POLYGON
                , NDocNodeType.PENTAGON
                , NDocNodeType.HEXAGON
                , NDocNodeType.HEPTAGON
                , NDocNodeType.OCTAGON
                , NDocNodeType.NONAGON
                , NDocNodeType.DECAGON
        );
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        int count = -1;
        switch (p.type()) {
            case NDocNodeType.PENTAGON: {
                count = 5;
                break;
            }
            case NDocNodeType.HEXAGON: {
                count = 6;
                break;
            }
            case NDocNodeType.HEPTAGON: {
                count = 7;
                break;
            }
            case NDocNodeType.OCTAGON: {
                count = 8;
                break;
            }
            case NDocNodeType.NONAGON: {
                count = 9;
                break;
            }
            case NDocNodeType.DECAGON: {
                count = 10;
                break;
            }
        }
        if (count < 3) {
            count = NDocObjEx.ofProp(p, NDocPropName.COUNT).asInt().orElse(-1);
        }
        NDocPoint2D[] points = null;
        if (count < 3) {
            points = NDocObjEx.ofProp(p, NDocPropName.POINTS).asHPoint2DArray().orNull();
            if(points != null) {
                if (points.length < 3) {
                    points = null;
                }
            }
        }
        if (points == null) {
            if (count < 3) {
                count = 3;
            }
            points = new NDocPoint2D[count];
            double x0 = 50;
            double y0 = 50;
            double w0 = 50;
            double h0 = 50;
            for (int i = 0; i < points.length; i++) {
                double angle = i * 1.0 / points.length * 2 * Math.PI;
                points[i] = new NDocPoint2D(
                        x0 + w0 * Math.cos(angle)
                        , y0 + h0 * Math.sin(angle)
                );
            }
        }
        render(p, points, ctx);
    }

}
