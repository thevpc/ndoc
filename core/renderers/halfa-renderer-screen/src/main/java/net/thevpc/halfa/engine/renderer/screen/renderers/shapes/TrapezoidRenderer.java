package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class TrapezoidRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public TrapezoidRenderer() {
        super(HNodeType.TRAPEZOID);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        double x1 = 20;
        double x2 = 20;
        HPoint2D[] points = new HPoint2D[]{
                new HPoint2D(0, 100),
                new HPoint2D(100, 100),
                new HPoint2D(100 - x2, 0),
                new HPoint2D(x1, 0),
        };
        render(p, points, ctx);
    }

}
