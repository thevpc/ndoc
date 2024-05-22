package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.node.HLine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.model.HPoint;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HLineRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HLine t = (HLine) p;
        Rectangle2D.Double b = selfBounds(t, ctx);
        Point2D.Double from = HPoint.ofParent(t.from().getX(), t.from().getY()).value(b, ctx.getGlobalBounds());
        Point2D.Double to = HPoint.ofParent(t.to().getX(), t.to().getY()).value(b, ctx.getGlobalBounds());
        Graphics2D g = ctx.getGraphics();
        applyLineColor(t, g, ctx);
        g.drawLine((int) from.getX(), (int) from.getY(),
                (int) to.getX(), (int) to.getY()
        );
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        return new Rectangle2D.Double(minx, miny, maxX, maxY);
    }

}
