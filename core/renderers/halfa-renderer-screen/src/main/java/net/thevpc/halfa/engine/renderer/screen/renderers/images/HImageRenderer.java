package net.thevpc.halfa.engine.renderer.screen.renderers.images;

import net.thevpc.halfa.api.node.HImage;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HImageRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HImage t = (HImage) p;
        Image image = t.image();



        Graphics2D g = ctx.getGraphics();

        Rectangle2D.Double b = selfBounds(t,  ctx);
        double x = b.getX();
        double y = b.getY();

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
