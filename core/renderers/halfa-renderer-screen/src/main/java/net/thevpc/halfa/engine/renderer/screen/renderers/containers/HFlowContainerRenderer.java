package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class HFlowContainerRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        Rectangle2D.Double expectedBounds = bounds(p, ctx);
        HContainer t = (HContainer) p;
        Graphics2D g = ctx.getGraphics();
        Rectangle2D.Double newBounds = new Rectangle2D.Double(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                expectedBounds.getWidth(),
                expectedBounds.getHeight()
        );
        HPartRendererContext ctx2 = new HPartRendererContextDelegate(
                t,
                ctx,
                newBounds
        );

        List<HNode> texts = t.children();
        Rectangle2D.Double a = expectedBounds;
        paintBackground(t, ctx, g, a);

        for (HNode text : texts) {
            Rectangle2D.Double r1 = ctx2.paintPagePart(text);
            a = expand(r1, a);
            //check if we need a newline??
            ctx2 = new HPartRendererContextDelegate(
                    t,
                    ctx,
                    new Rectangle2D.Double(
                            r1.getX() + r1.width, r1.getY(),
                            expectedBounds.getWidth(),
                            expectedBounds.getHeight()
                    )
            );
        }
        paintBorderLine(t, ctx, g, a);
        return a;
    }
}
