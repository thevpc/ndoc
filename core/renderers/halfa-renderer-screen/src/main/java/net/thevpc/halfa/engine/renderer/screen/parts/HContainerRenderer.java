package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HLayout;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HContainer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContextDelegate;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class HContainerRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        Rectangle2D.Double expectedBounds = bounds(p, ctx);
        HContainer t = (HContainer) p;
        HLayout layout = getLayout(t, ctx);
        if (layout == null) {
            layout = HLayout.ABSOLUTE;
        }

        switch (layout) {
            case ABSOLUTE: {
                return paintPagePartAbsolute(t, expectedBounds, ctx);
            }
            case FLOW: {
                return paintPagePartFlow(t, expectedBounds, ctx);
            }
        }
        throw new IllegalArgumentException("unexpected");
    }

    public Rectangle2D.Double paintPagePartAbsolute(HContainer t, Rectangle2D.Double expectedBounds, HPartRendererContext ctx) {
        Graphics2D g = ctx.getGraphics();
        Rectangle2D.Double a = expectedBounds;

        paintBackground(t, ctx, g, a);
        HPartRendererContext ctx2 = new HPartRendererContextDelegate(
                t,
                ctx,
                ctx.getOffset(),
                expectedBounds
        );
        List<HPagePart> texts = t.children();
        for (HPagePart text : texts) {
            Rectangle2D.Double r1 = ctx2.paintPagePart(text);
            a = expand(r1, a);
        }
        paintBorderLine(t, ctx, g, a);
        return a;
    }

    public Rectangle2D.Double paintPagePartFlow(HContainer t, Rectangle2D.Double expectedBounds, HPartRendererContext ctx) {
        Graphics2D g = ctx.getGraphics();
        Rectangle2D.Double newBounds = new Rectangle2D.Double(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                expectedBounds.getX(),
                expectedBounds.getY()
        );
        HPartRendererContext ctx2 = new HPartRendererContextDelegate(
                t,
                ctx,
                new Point2D.Double(0, 0),
                newBounds
        );

        List<HPagePart> texts = t.children();
        Rectangle2D.Double a = expectedBounds;
        paintBackground(t, ctx, g, a);

        for (HPagePart text : texts) {
            Rectangle2D.Double r1 = ctx2.paintPagePart(text);
            a = expand(r1, a);
            //check if we need a newline??
            ctx2 = new HPartRendererContextDelegate(
                    t,
                    ctx,
                    new Point2D.Double(r1.getX() + r1.width, r1.getY()),
                    newBounds
            );
        }
        paintBorderLine(t, ctx, g, a);
        return a;

    }



    private Rectangle2D.Double expand(Rectangle2D.Double a, Rectangle2D.Double b) {
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        Rectangle2D.Double r1 = a;
        if (r1.getMinX() < minX) {
            minX = r1.getMinX();
        }
        if (r1.getMaxX() < minX) {
            maxX = r1.getMaxX();
        }
        if (r1.getMinY() < minY) {
            minY = r1.getMinY();
        }
        if (r1.getMaxY() < minY) {
            maxY = r1.getMaxY();
        }
        r1 = b;
        if (r1.getMinX() < minX) {
            minX = r1.getMinX();
        }
        if (r1.getMaxX() < minX) {
            maxX = r1.getMaxX();
        }
        if (r1.getMinY() < minY) {
            minY = r1.getMinY();
        }
        if (r1.getMaxY() < minY) {
            maxY = r1.getMaxY();
        }
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }
}
