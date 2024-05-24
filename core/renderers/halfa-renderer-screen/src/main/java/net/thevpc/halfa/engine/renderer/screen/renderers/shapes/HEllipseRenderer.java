package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HEllipse;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.spi.HUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HEllipseRenderer extends AbstractHPartRenderer {

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HEllipse t = (HEllipse) p;
        Bounds2 b = selfBounds(t, null, ctx);
        double x = b.getX();
        double y = b.getY();
        Graphics2D g = ctx.getGraphics();
        boolean someBG=false;
        if (someBG=applyBackgroundColor(t, g, ctx)) {
            g.fillOval((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
        }
        if (applyLineColor(t, g, ctx, !someBG)) {
            g.drawOval((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
        }
        return new Bounds2(x,y,b.getWidth(),b.getWidth());
    }

}
