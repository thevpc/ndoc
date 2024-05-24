package net.thevpc.halfa.engine.renderer.screen.renderers.fillers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HFiller;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.model.XYConstraints;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;

import java.awt.*;

public class HFillerRenderer extends AbstractHPartRenderer {
    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        HFiller t = (HFiller) p;
        XYConstraints c = t.getConstraints();
        HSizeRequirements r = new HSizeRequirements();
        Bounds2 b = ctx.getBounds();
        Bounds2 rb = ctx.getGlobalBounds();
        r.minX = c.getX().getMin().value(b, rb);
        r.maxX = c.getX().getMax().value(b, rb);
        r.preferredX = c.getX().getPreferred().value(b, rb);

        r.minY = c.getY().getMin().value(b, rb);
        r.maxY = c.getY().getMax().value(b, rb);
        r.preferredY = c.getY().getPreferred().value(b, rb);

        return r;
    }

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HFiller t = (HFiller) p;
        Graphics2D g = ctx.getGraphics();
        Bounds2 bounds = ctx.getBounds();
        Bounds2 bb = new Bounds2(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight());
        paintBackground(t, ctx, g, bounds);
        paintBorderLine(t, ctx, g, bb);
        return bb;
    }


}
