package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.node.HArc;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HArcRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HArc t = (HArc) p;
        Rectangle2D.Double b = selfBounds(t, null, ctx);
        double x = b.getX();
        double y = b.getY();
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
