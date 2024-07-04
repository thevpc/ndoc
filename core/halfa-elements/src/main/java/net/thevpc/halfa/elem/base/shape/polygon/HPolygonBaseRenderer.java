package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HElement2DFactory;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.awt.*;
import java.util.Arrays;

public abstract class HPolygonBaseRenderer extends HNodeRendererBase {
    public HPolygonBaseRenderer(String... types) {
        super(types);
    }

    public void render(HNode node, HPoint2D[] points, HNodeRendererContext ctx) {
        Bounds2 b = selfBounds(node, ctx);
        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint bc = HValueByName.resolveBackgroundColor(node, ctx);
            Paint fc = HValueByName.getForegroundColor(node, ctx, bc == null);
            HPoint2D[] points2 = Arrays.stream(points)
                    .map(p -> new HPoint2D(
                            p.x / 100 * b.getWidth() + b.getMinX(),
                            p.y / 100 * b.getHeight() + b.getMinY()
                    ))
                    .toArray(HPoint2D[]::new);
            g.draw2D(HElement2DFactory.polygon(points2)
                    .setFill(bc != null)
                    .setContour(fc != null)
                    .setLineStroke(HNodeRendererUtils.resolveStroke(node, g, ctx))
                    .setBackgroundPaint(bc)
                    .setLinePaint(fc));
            HNodeRendererUtils.paintBorderLine(node, ctx, g, b);
        }
        HNodeRendererUtils.paintDebugBox(node, ctx, g, b);
    }

}
