package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.node.HEllipse;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HEllipseRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HEllipse t = (HEllipse) p;
        Rectangle2D.Double b = selfBounds(t, null, ctx);
        double x = b.getX();
        double y = b.getY();
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
