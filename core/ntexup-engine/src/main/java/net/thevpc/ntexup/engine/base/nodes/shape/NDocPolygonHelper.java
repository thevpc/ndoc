package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.elem2d.NDocElement2DFactory;
import net.thevpc.ntexup.api.document.elem2d.NDocPoint2D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

import java.awt.*;
import java.util.Arrays;

public final class NDocPolygonHelper {


    public static void renderPointsCount(int count, NTxNode p, NDocNodeRendererContext ctx, NDocProperties defaultStyles) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocPoint2D[] points = null;
        if (count < 3) {
            count = 3;
        }
        points = new NDocPoint2D[count];
        double x0 = 50;
        double y0 = 50;
        double w0 = 50;
        double h0 = 50;
        for (int i = 0; i < points.length; i++) {
            double angle = i * 1.0 / points.length * 2 * Math.PI;
            points[i] = new NDocPoint2D(
                    x0 + w0 * Math.cos(angle)
                    , y0 + h0 * Math.sin(angle)
            );
        }
        renderPoints(p, points, ctx);
    }
    public static void renderPoints(NTxNode node, NDocPoint2D[] points, NDocNodeRendererContext ctx) {
        NDocBounds2 b = ctx.selfBounds(node);
        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint bc = ctx.resolveBackgroundColor(node);
            Paint fc = ctx.getForegroundColor(node, bc == null);
            NDocPoint2D[] points2 = Arrays.stream(points)
                    .map(p -> new NDocPoint2D(
                            p.x / 100 * b.getWidth() + b.getMinX(),
                            p.y / 100 * b.getHeight() + b.getMinY()
                    ))
                    .toArray(NDocPoint2D[]::new);
            g.draw2D(NDocElement2DFactory.polygon(points2)
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
