package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPolyline;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.spi.HUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HPolylineRenderer extends AbstractHPartRenderer {

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HPolyline t = (HPolyline) p;
        Bounds2 size = ctx.getBounds();
        Bounds2 b = selfBounds(t, ctx);
        double x = b.getX();
        double y = b.getY();
        Graphics2D g = ctx.getGraphics();
        Double2[] points = t.points();
        int[] xx = new int[points.length];
        int[] yy = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            xx[i] = (int) (HUtils.doubleOf(points[i].getX()) / 100 * b.getWidth() + x);
            yy[i] = (int) (HUtils.doubleOf(points[i].getY()) / 100 * b.getHeight() + y);
        }
        if (applyLineColor(t, g, ctx,true)) {
            g.drawPolyline(xx, yy, points.length);
        }
        return b;
    }

}
