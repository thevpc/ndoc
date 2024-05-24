package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HArc;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.spi.HUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HArcRenderer extends AbstractHPartRenderer {

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HArc t = (HArc) p;
        Bounds2 b = selfBounds(t, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double startAngle = t.startAngle();
        double endAngle = t.endAngle();
        Graphics2D g = ctx.getGraphics();
        applyLineColor(t, g, ctx,true);
        g.drawArc((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()),
                (int)startAngle,
                (int)endAngle
                );
        return b;
    }

}
