package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HRectangle;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.PageView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HRectangleRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HRectangle t = (HRectangle) p;

        Point2D.Double bb = size(p, ctx);
        Rectangle2D b = new Rectangle2D.Double(0, 0, bb.getX(), bb.getY());
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();
        Graphics2D g = ctx.getGraphics();
        Boolean threeD = get3D(p, ctx);
        Boolean raised = getRaised(p, ctx);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        Point2D.Double roundCorners = roundCornerArcs(p, ctx);

        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!round && !d3) {
            if (applyBackgroundColor(t, g, ctx)) {
                g.fillRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
            }
            if (applyLineColor(t, g, ctx)) {
                g.drawRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
            }
        } else if (round) {
            double cx = roundCorners.x / 100 * PageView.REF_SIZE.width;
            double cy = roundCorners.y / 100 * PageView.REF_SIZE.height;
            if (applyBackgroundColor(t, g, ctx)) {
                g.fillRoundRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight(), (int) cx, (int) cy);
            }
            if (applyLineColor(t, g, ctx)) {
                g.drawRoundRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight(), (int) cx, (int) cy);
            }
        } else if (threeD) {
            if (applyBackgroundColor(t, g, ctx)) {
                g.fill3DRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight(), raised != null && raised);
            }
            if (applyLineColor(t, g, ctx)) {
                g.draw3DRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight(), raised != null && raised);
            }
        }
        return new Rectangle2D.Double(x,y,b.getWidth(),b.getWidth());
    }

}
