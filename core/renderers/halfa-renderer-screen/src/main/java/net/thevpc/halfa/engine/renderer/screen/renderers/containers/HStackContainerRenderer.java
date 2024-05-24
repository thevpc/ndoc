package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;

import java.awt.*;
import java.util.List;

public class HStackContainerRenderer extends AbstractHPartRenderer {

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        Bounds2 expectedBounds = selfBounds(p, ctx);
        HContainer t = (HContainer) p;
        Graphics2D g = ctx.getGraphics();
        Bounds2 a = expectedBounds;

        paintBackground(t, ctx, g, a);
        HPartRendererContext ctx2 = new HPartRendererContextDelegate(
                t,
                ctx,
                expectedBounds
        );
        List<HNode> texts = t.children();
        for (HNode text : texts) {
            Bounds2 r1 = ctx2.paintPagePart(text);
            a = expand(r1, a);
        }
        paintBorderLine(t, ctx, g, a);
        return a;
    }



}
