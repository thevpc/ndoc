package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public class TriangleRenderer extends NDocPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public TriangleRenderer() {
        super(HNodeType.TRIANGLE);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        HPoint2D[] points = new HPoint2D[]{
                new HPoint2D(0, 100),
                new HPoint2D(100, 100),
                new HPoint2D(50, 0),
        };
        render(p, points, ctx);
    }

}
