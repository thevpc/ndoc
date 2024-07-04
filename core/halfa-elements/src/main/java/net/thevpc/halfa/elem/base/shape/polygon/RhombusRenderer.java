package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class RhombusRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public RhombusRenderer() {
        super(HNodeType.RHOMBUS);
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        HPoint2D[] points = new HPoint2D[]{
                new HPoint2D(0, 50),
                new HPoint2D(50, 0),
                new HPoint2D(100, 50),
                new HPoint2D(50, 100),
        };
        render(p, points, ctx);
    }

}
