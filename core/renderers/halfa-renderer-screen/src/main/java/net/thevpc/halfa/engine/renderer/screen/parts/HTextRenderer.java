package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HText;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HTextRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HText t = (HText) p;
        String message = t.getMessage();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();
        applyFont(t, g, ctx);

        Point2D.Double expectedBounds = size(p, ctx); //just ignored?
        Rectangle2D b = g.getFontMetrics().getStringBounds(message, g);
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();

        paintBackground(t,ctx,g,new Rectangle2D.Double(b.getMinX(),b.getMinY(),b.getWidth(),b.getHeight()));

        applyForeground(t, g, ctx);
        g.drawString(message, (int) x, (int) (y - b.getMinY()));

        paintBorderLine(t,ctx,g,new Rectangle2D.Double(x,y,b.getWidth(),b.getHeight()));
        return new Rectangle2D.Double(x, y, b.getWidth(), b.getWidth());
    }


}
