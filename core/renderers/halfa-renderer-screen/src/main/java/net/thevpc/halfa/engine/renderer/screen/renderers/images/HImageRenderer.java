package net.thevpc.halfa.engine.renderer.screen.renderers.images;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HImage;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.spi.HUtils;

import java.awt.*;

public class HImageRenderer extends AbstractHPartRenderer {

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HImage t = (HImage) p;
        Image image = t.image();



        Graphics2D g = ctx.getGraphics();

        Bounds2 b = selfBounds(t,  ctx);
        double x = b.getX();
        double y = b.getY();

        if (applyBackgroundColor(t, g, ctx)) {
            g.fillRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
        }

        applyForeground(t, g, ctx);
        if (image != null) {
            g.drawImage(image, (int) x, (int) (y - b.getMinY()), null);
        }
        return new Bounds2(x,y,b.getWidth(),b.getWidth());
    }


}
