package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.eval.ObjEx;

public class HPolygonRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public HPolygonRenderer() {
        super(
                HNodeType.POLYGON
                , HNodeType.PENTAGON
                , HNodeType.HEXAGON
                , HNodeType.HEPTAGON
                , HNodeType.NONAGON
                , HNodeType.DECAGON
        );
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
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
            count = ObjEx.ofProp(p, HPropName.COUNT).asInt().orElse(-1);
        }
        HPoint2D[] points = null;
        if (count < 3) {
            points = ObjEx.ofProp(p, HPropName.POINTS).asHPoint2DArray().get();
            if (points.length < 3) {
                points = null;
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
