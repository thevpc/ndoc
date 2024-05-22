package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HTextRenderer extends AbstractHPartRenderer {

    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        HText t = (HText) p;
        String message = t.getMessage();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();
        applyFont(t, g, ctx);
        Rectangle2D b = g.getFontMetrics().getStringBounds(message, g);
        double ww = b.getWidth();
        double hh = b.getHeight();
        HSizeRequirements r = new HSizeRequirements();
        r.minX = ww;
        r.maxX = ww;
        r.preferredX = ww;
        r.minY = hh;
        r.maxY = hh;
        r.preferredY = hh;
        return r;
    }

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HText t = (HText) p;
        String message = t.getMessage();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();
        applyFont(t, g, ctx);
        Point2D.Double offset = new Point2D.Double(ctx.getBounds().getX(), ctx.getBounds().getY());
        Rectangle2D textBounds = g.getFontMetrics().getStringBounds(message, g);
        Rectangle2D.Double selfBounds = selfBounds(t, new Point2D.Double(textBounds.getWidth(),textBounds.getHeight()), ctx);
        double x = selfBounds.getX();
        double y = selfBounds.getY();

//        Rectangle2D.Double b2 = new Rectangle2D.Double(offset.getX() + x, offset.getY() + y, b.getWidth(), b.getHeight());
//        Rectangle2D.Double b1 = new Rectangle2D.Double(offset.getX() + b.getMinX(), offset.getY() + b.getMinY(), b.getWidth(), b.getHeight());
        paintBackground(t, ctx, g, selfBounds);

        applyForeground(t, g, ctx);
        g.drawString(message, (int) x, (int) (y - textBounds.getMinY()));
        paintBorderLine(t, ctx, g, selfBounds);
        return selfBounds;
    }


}
