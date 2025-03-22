package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public class ParallelogramRenderer extends NDocPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public ParallelogramRenderer() {
        super(HNodeType.PARALLELOGRAM);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
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
