package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.elem2d.NDocElement2DFactory;
import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.awt.*;
import java.util.Arrays;

public abstract class NDocPolygonBaseRenderer extends NDocNodeRendererBase {
    public NDocPolygonBaseRenderer(String... types) {
        super(types);
    }

    public void render(NDocNode node, NDocPoint2D[] points, NDocNodeRendererContext ctx) {
        NDocBounds2 b = selfBounds(node, ctx);
        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint bc = NDocValueByName.resolveBackgroundColor(node, ctx);
            Paint fc = NDocValueByName.getForegroundColor(node, ctx, bc == null);
            NDocPoint2D[] points2 = Arrays.stream(points)
                    .map(p -> new NDocPoint2D(
                            p.x / 100 * b.getWidth() + b.getMinX(),
                            p.y / 100 * b.getHeight() + b.getMinY()
                    ))
                    .toArray(NDocPoint2D[]::new);
            g.draw2D(NDocElement2DFactory.polygon(points2)
                    .setFill(bc != null)
                    .setContour(fc != null)
                    .setLineStroke(NDocNodeRendererUtils.resolveStroke(node, g, ctx))
                    .setBackgroundPaint(bc)
                    .setLinePaint(fc));
            NDocNodeRendererUtils.paintBorderLine(node, ctx, g, b);
        }
        NDocNodeRendererUtils.paintDebugBox(node, ctx, g, b);
    }

}
