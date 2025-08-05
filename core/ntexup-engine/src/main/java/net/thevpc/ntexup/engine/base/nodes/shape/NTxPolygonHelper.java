package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DFactory;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

import java.awt.*;
import java.util.Arrays;

public final class NTxPolygonHelper {


    public static void renderPointsCount(int count, NTxNode p, NTxNodeRendererContext ctx, NTxProperties defaultStyles) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxPoint2D[] points = null;
        if (count < 3) {
            count = 3;
        }
        points = new NTxPoint2D[count];
        double x0 = 50;
        double y0 = 50;
        double w0 = 50;
        double h0 = 50;
        for (int i = 0; i < points.length; i++) {
            double angle = i * 1.0 / points.length * 2 * Math.PI;
            points[i] = new NTxPoint2D(
                    x0 + w0 * Math.cos(angle)
                    , y0 + h0 * Math.sin(angle)
            );
        }
        renderPoints(p, points, ctx);
    }
    public static void renderPoints(NTxNode node, NTxPoint2D[] points, NTxNodeRendererContext ctx) {
        NTxBounds2 b = ctx.selfBounds(node);
        NTxGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint bc = ctx.resolveBackgroundColor(node);
            Paint fc = ctx.getForegroundColor(node, bc == null);
            NTxPoint2D[] points2 = Arrays.stream(points)
                    .map(p -> new NTxPoint2D(
                            p.x / 100 * b.getWidth() + b.getMinX(),
                            p.y / 100 * b.getHeight() + b.getMinY()
                    ))
                    .toArray(NTxPoint2D[]::new);
            g.draw2D(NTxElement2DFactory.polygon(points2)
                    .setFill(bc != null)
                    .setContour(fc != null)
                    .setLineStroke(ctx.resolveStroke(node))
                    .setBackgroundPaint(bc)
                    .setLinePaint(fc));
            ctx.paintBorderLine(node, b);
        }
        ctx.paintDebugBox(node, b);
    }

}
