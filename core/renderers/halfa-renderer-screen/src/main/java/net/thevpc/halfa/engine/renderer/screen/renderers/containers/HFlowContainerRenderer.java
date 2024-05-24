package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;

import java.awt.*;
import java.util.List;

public class HFlowContainerRenderer extends AbstractHPartRenderer {

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        Bounds2 expectedBounds = selfBounds(p, ctx);
        HContainer t = (HContainer) p;
        Graphics2D g = ctx.getGraphics();
        Bounds2 newBounds = new Bounds2(
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
        Bounds2 a = expectedBounds;
        paintBackground(t, ctx, g, a);
        double yRef=0;
        boolean debug=false;
        for (int i = 0; i < texts.size(); i++) {
            HNode text = texts.get(i);
            Bounds2 r1 = ctx2.paintPagePart(text);
            a = expand(r1, a);
            if (i == 0) {
                yRef = r1.getY();
            }
            if(debug) {
                g.setColor(Color.RED);
                g.fillOval((int) (r1.getX() + r1.getWidth()), (int) yRef, 5, 5);
            }
            //check if we need a newline??
            ctx2 = new HPartRendererContextDelegate(
                    t,
                    ctx,
                    new Bounds2(
                            r1.getX() + r1.getWidth(), yRef,
                            expectedBounds.getWidth(),
                            expectedBounds.getHeight()
                    )
            );
        }
        paintBorderLine(t, ctx, g, a);
        return a;
    }
}
