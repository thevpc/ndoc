package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public class RhombusRenderer extends NDocPolygonBaseRenderer {
    NDocProperties defaultStyles = new NDocProperties();

    public RhombusRenderer() {
        super(NDocNodeType.RHOMBUS);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocPoint2D[] points = new NDocPoint2D[]{
                new NDocPoint2D(0, 50),
                new NDocPoint2D(50, 0),
                new NDocPoint2D(100, 50),
                new NDocPoint2D(50, 100),
        };
        render(p, points, ctx);
    }

}
