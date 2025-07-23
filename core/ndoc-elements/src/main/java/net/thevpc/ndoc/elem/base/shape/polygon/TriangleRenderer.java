package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class TriangleRenderer extends NDocPolygonBaseRenderer {
    NDocProperties defaultStyles = new NDocProperties();

    public TriangleRenderer() {
        super(NDocNodeType.TRIANGLE);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocPoint2D[] points = new NDocPoint2D[]{
                new NDocPoint2D(0, 100),
                new NDocPoint2D(100, 100),
                new NDocPoint2D(50, 0),
        };
        render(p, points, ctx);
    }

}
