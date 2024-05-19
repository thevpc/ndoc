package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HImage;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HImageRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HImage t = (HImage) p;
        Image image = t.image();


        Point2D.Double expectedBounds = size(p, ctx); //just ignored?


        Graphics2D g = ctx.getGraphics();
        //applyFont(t, g, ctx);

        Rectangle2D.Double b = new Rectangle2D.Double(0, 0, expectedBounds.getX(), expectedBounds.getY());
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();

        if (applyBackgroundColor(t, g, ctx)) {
            g.fillRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
        }

        applyForeground(t, g, ctx);
        if (image != null) {
            g.drawImage(image, (int) x, (int) (y - b.getMinY()), null);
        }
        return new Rectangle2D.Double(x,y,b.getWidth(),b.getWidth());
    }


}
