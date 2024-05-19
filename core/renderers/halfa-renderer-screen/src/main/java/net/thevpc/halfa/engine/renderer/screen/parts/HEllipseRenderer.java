package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HEllipse;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HEllipseRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HEllipse t = (HEllipse) p;
        Point2D.Double bb = size(p, ctx);
        Rectangle2D.Double b = new Rectangle2D.Double(0, 0, bb.getX(), bb.getY());
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();
        Graphics2D g = ctx.getGraphics();
        if (applyBackgroundColor(t, g, ctx)) {
            g.fillOval((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
        }
        if (applyLineColor(t, g, ctx)) {
            g.drawOval((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
        }
        return new Rectangle2D.Double(x,y,b.getWidth(),b.getWidth());
    }

}
