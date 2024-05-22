package net.thevpc.halfa.engine.renderer.screen.renderers.fillers;

import net.thevpc.halfa.api.node.HFiller;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.model.XYConstraints;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HFillerRenderer extends AbstractHPartRenderer {
    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        HFiller t = (HFiller) p;
        XYConstraints c = t.getConstraints();
        HSizeRequirements r = new HSizeRequirements();
        Rectangle2D.Double b = ctx.getBounds();
        Rectangle2D.Double rb = ctx.getGlobalBounds();
        r.minX = c.getX().getMin().value(b, rb);
        r.maxX = c.getX().getMax().value(b, rb);
        r.preferredX = c.getX().getPreferred().value(b, rb);

        r.minY = c.getY().getMin().value(b, rb);
        r.maxY = c.getY().getMax().value(b, rb);
        r.preferredY = c.getY().getPreferred().value(b, rb);

        return r;
    }

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HFiller t = (HFiller) p;
        Graphics2D g = ctx.getGraphics();
        Rectangle2D.Double bounds = ctx.getBounds();
        Rectangle2D.Double bb = new Rectangle2D.Double(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight());
        paintBackground(t, ctx, g, bounds);
        paintBorderLine(t, ctx, g, bb);
        return bb;
    }


}
