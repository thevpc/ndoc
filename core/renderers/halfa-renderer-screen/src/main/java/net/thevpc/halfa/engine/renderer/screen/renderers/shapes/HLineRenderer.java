package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HLine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.model.HPoint;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.spi.HUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HLineRenderer extends AbstractHPartRenderer {

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HLine t = (HLine) p;
        Bounds2 b = selfBounds(t, ctx);
        Double2 from = HPoint.ofParent(t.from().getX(), t.from().getY()).value(b, ctx.getGlobalBounds());
        Double2 to = HPoint.ofParent(t.to().getX(), t.to().getY()).value(b, ctx.getGlobalBounds());
        Graphics2D g = ctx.getGraphics();
        applyLineColor(t, g, ctx,true);
        g.drawLine(HUtils.intOf(from.getX()), HUtils.intOf(from.getY()),
                HUtils.intOf(to.getX()), HUtils.intOf(to.getY())
        );
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        return new Bounds2(minx, miny, maxX, maxY);
    }

}
