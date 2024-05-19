package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HPolyline;
import net.thevpc.halfa.api.model.SizeD;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HPolylineRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HPolyline t = (HPolyline) p;
        Rectangle2D.Double size = ctx.getBounds();
        SizeD base = new SizeD(size.width, size.height);
        Point2D.Double bb = size(p, ctx);
        SizeD newBase = mapDim(new SizeD(bb.getX(), bb.getY()), base);
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
        if (applyLineColor(t, g, ctx)) {
            g.drawPolyline(xx, yy, points.length);
        }
        return new Rectangle2D.Double(x,y,b.getWidth(),b.getWidth());
    }

}
