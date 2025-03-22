package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.HElement2DFactory;
import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.awt.*;
import java.util.Arrays;

public abstract class NDocPolygonBaseRenderer extends NDocNodeRendererBase {
    public NDocPolygonBaseRenderer(String... types) {
        super(types);
    }

    public void render(HNode node, HPoint2D[] points, NDocNodeRendererContext ctx) {
        Bounds2 b = selfBounds(node, ctx);
        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint bc = NDocValueByName.resolveBackgroundColor(node, ctx);
            Paint fc = NDocValueByName.getForegroundColor(node, ctx, bc == null);
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
