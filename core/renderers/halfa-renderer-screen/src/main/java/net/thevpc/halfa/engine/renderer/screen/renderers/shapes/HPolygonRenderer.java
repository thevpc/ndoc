package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPolygon;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HPolygonRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HPolygon t = (HPolygon) p;
        Rectangle2D.Double b = selfBounds(t, ctx);
        double x = b.getX();
        double y = b.getY();
        Graphics2D g = ctx.getGraphics();
        Point2D.Double[] points = t.points();
        int[] xx = new int[points.length];
        int[] yy = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            xx[i] = (int) (xx[i] / 100 * b.getWidth() + x);
            yy[i] = (int) (yy[i] / 100 * b.getHeight() + y);
        }
        if (applyBackgroundColor(t, g, ctx)) {
            g.fillPolygon(xx, yy, points.length);
        }
        if (applyLineColor(t, g, ctx)) {
            g.drawPolygon(xx, yy, points.length);
        }
        return b;
    }

}
