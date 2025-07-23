package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class TrapezoidRenderer extends NDocPolygonBaseRenderer {
    NDocProperties defaultStyles = new NDocProperties();

    public TrapezoidRenderer() {
        super(NDocNodeType.TRAPEZOID);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        double x1 = 20;
        double x2 = 20;
        NDocPoint2D[] points = new NDocPoint2D[]{
                new NDocPoint2D(0, 100),
                new NDocPoint2D(100, 100),
                new NDocPoint2D(100 - x2, 0),
                new NDocPoint2D(x1, 0),
        };
        render(p, points, ctx);
    }

}
