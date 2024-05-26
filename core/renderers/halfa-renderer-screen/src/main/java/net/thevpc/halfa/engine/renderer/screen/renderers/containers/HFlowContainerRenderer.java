package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;

import java.awt.*;
import java.util.List;

public class HFlowContainerRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HFlowContainerRenderer() {
        super(HNodeType.FLOW);
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 expectedBounds = selfBounds(p, ctx);
        HContainer t = (HContainer) p;
        HGraphics g = ctx.graphics();
        Bounds2 newBounds = new Bounds2(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                expectedBounds.getWidth(),
                expectedBounds.getHeight()
        );
        HNodeRendererContext ctx2 = ctx.withBounds(t,newBounds);

        List<HNode> texts = t.children();
        Bounds2 a = expectedBounds;
        if (!ctx.isDry()) {
            paintBackground(t, ctx, g, a);
        }
        double yRef = 0;
        boolean debug = false;
        for (int i = 0; i < texts.size(); i++) {
            HNode text = texts.get(i);
            Bounds2 r1 = ctx2.render(text).toBounds2();
            a = expand(r1, a);
            if (i == 0) {
                yRef = r1.getY();
            }
            if (!ctx.isDry()) {
                if (debug) {
                    g.setColor(Color.RED);
                    g.fillOval((r1.getX() + r1.getWidth()), yRef, 5, 5);
                }
            }
            //check if we need a newline??
            ctx2 = ctx.withBounds(t,new Bounds2(
                    r1.getX() + r1.getWidth(), yRef,
                    expectedBounds.getWidth(),
                    expectedBounds.getHeight()
            ));
        }
        if (!ctx.isDry()) {
            paintBorderLine(t, ctx, g, a);
        }
        return new HSizeRequirements(a);
    }
}
