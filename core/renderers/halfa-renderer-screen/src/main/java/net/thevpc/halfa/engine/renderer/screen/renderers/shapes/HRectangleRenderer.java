package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HRectangle;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.PageView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HRectangleRenderer extends AbstractHPartRenderer {


    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HRectangle t = (HRectangle) p;

        Rectangle2D.Double b = selfBounds(t, ctx);
        double x = b.getX();
        double y = b.getY();
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
        return new Rectangle2D.Double(x, y, b.getWidth(), b.getWidth());
    }

}
