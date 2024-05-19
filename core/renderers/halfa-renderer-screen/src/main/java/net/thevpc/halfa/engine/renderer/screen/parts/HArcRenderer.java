package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HArc;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HArcRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HArc t = (HArc) p;
        Point2D.Double bb = size(p, ctx);
        Rectangle2D.Double b = new Rectangle2D.Double(0, 0, bb.getX(), bb.getY());
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();
        double startAngle = t.startAngle();
        double endAngle = t.endAngle();
        Graphics2D g = ctx.getGraphics();
        applyLineColor(t, g, ctx);
        g.drawArc((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight(),
                (int)startAngle,
                (int)endAngle
                );
        return b;
    }

}
