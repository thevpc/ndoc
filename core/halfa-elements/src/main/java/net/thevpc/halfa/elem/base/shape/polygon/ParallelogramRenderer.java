package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class ParallelogramRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public ParallelogramRenderer() {
        super(HNodeType.PARALLELOGRAM);
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        double w = 20;
        HPoint2D[] points = new HPoint2D[]{
                new HPoint2D(0, 100),
                new HPoint2D(100 - w, 100),
                new HPoint2D(100, 0),
                new HPoint2D(w, 0),
        };
        render(p, points, ctx);
    }

}
