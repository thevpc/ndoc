package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HLine;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HLineRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HLine t = (HLine) p;
        Point2D.Double bb = size(p, ctx);
        Rectangle2D b = new Rectangle2D.Double(0, 0, bb.getX(), bb.getY());
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();
        Graphics2D g = ctx.getGraphics();
        applyLineColor(t, g, ctx);
        g.drawRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
        return new Rectangle2D.Double(x,y,b.getWidth(),b.getWidth());
    }

}
