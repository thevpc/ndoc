package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HElement2D;
import net.thevpc.halfa.api.model.elem2d.HElement2DFactory;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem2d.primitives.HElement2DPolygon;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;

import java.awt.*;

public abstract class HPolygonBaseRenderer extends AbstractHNodeRenderer {
    public HPolygonBaseRenderer(String... types) {
        super(types);
    }

    public void render(HNode p, HPoint2D[] points, HNodeRendererContext ctx) {
        Bounds2 b = selfBounds(p, ctx);
        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint bc = resolveBackgroundColor(p, g, ctx);
            Paint fc = resolveLineColor(p, g, ctx, bc == null);
            g.draw2D(HElement2DFactory.polygon(points)
                    .setFill(bc != null)
                    .setContour(fc != null)
                    .setLineStroke(resolveStroke(p, g, ctx))
                    .setBackgroundPaint(bc)
                    .setLinePaint(fc));
            paintBorderLine(p, ctx, g, b);
        }
        paintDebugBox(p, ctx, g, b);
    }

}
