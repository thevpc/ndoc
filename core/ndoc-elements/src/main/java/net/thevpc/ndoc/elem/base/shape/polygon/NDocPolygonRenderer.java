package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.eval.NDocObjEx;

public class NDocPolygonRenderer extends NDocPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public NDocPolygonRenderer() {
        super(
                HNodeType.POLYGON
                , HNodeType.PENTAGON
                , HNodeType.HEXAGON
                , HNodeType.HEPTAGON
                , HNodeType.OCTAGON
                , HNodeType.NONAGON
                , HNodeType.DECAGON
        );
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        int count = -1;
        switch (p.type()) {
            case HNodeType.PENTAGON: {
                count = 5;
                break;
            }
            case HNodeType.HEXAGON: {
                count = 6;
                break;
            }
            case HNodeType.HEPTAGON: {
                count = 7;
                break;
            }
            case HNodeType.OCTAGON: {
                count = 8;
                break;
            }
            case HNodeType.NONAGON: {
                count = 9;
                break;
            }
            case HNodeType.DECAGON: {
                count = 10;
                break;
            }
        }
        if (count < 3) {
            count = NDocObjEx.ofProp(p, HPropName.COUNT).asInt().orElse(-1);
        }
        HPoint2D[] points = null;
        if (count < 3) {
            points = NDocObjEx.ofProp(p, HPropName.POINTS).asHPoint2DArray().orNull();
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
            points = new HPoint2D[count];
            double x0 = 50;
            double y0 = 50;
            double w0 = 50;
            double h0 = 50;
            for (int i = 0; i < points.length; i++) {
                double angle = i * 1.0 / points.length * 2 * Math.PI;
                points[i] = new HPoint2D(
                        x0 + w0 * Math.cos(angle)
                        , y0 + h0 * Math.sin(angle)
                );
            }
        }
        render(p, points, ctx);
    }

}
