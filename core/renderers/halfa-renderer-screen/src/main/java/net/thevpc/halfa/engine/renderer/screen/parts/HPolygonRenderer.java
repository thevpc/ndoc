package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HPolygon;
import net.thevpc.halfa.api.model.SizeD;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HPolygonRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HPolygon t = (HPolygon) p;
        Point2D.Double bb = size(p, ctx);
        SizeD newBase = new SizeD(bb.getX(), bb.getY());
        Rectangle2D b = new Rectangle2D.Double(0, 0, newBase.getWidth(), newBase.getWidth());
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();
        Graphics2D g = ctx.getGraphics();
        Point2D.Double[] points = t.points();
        int[] xx = new int[points.length];
        int[] yy = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            xx[i] = (int) (xx[i] / 100 * newBase.getWidth() + x);
            yy[i] = (int) (yy[i] / 100 * newBase.getHeight() + y);
        }
        if (applyBackgroundColor(t, g, ctx)) {
            g.fillPolygon(xx, yy, points.length);
        }
        if (applyLineColor(t, g, ctx)) {
            g.drawPolygon(xx, yy, points.length);
        }
        return new Rectangle2D.Double(x,y,b.getWidth(),b.getWidth());
    }

}
