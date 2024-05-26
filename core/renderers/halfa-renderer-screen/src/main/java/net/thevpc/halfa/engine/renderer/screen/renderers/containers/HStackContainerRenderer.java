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

import java.util.List;

public class HStackContainerRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HStackContainerRenderer() {
        super(HNodeType.STACK);
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Bounds2 selfBounds = selfBounds(p, ctx);
        HContainer t = (HContainer) p;
        HGraphics g = ctx.graphics();

        if (!ctx.isDry()) {
            paintBackground(t, ctx, g, selfBounds);
        }
        HNodeRendererContext ctx2 = ctx.withBounds(t,selfBounds);
        List<HNode> texts = t.children();
        for (HNode text : texts) {
            Bounds2 r1 = ctx2.render(text).toBounds2();
            selfBounds = expand(r1, selfBounds);
        }
        if (!ctx.isDry()) {
            paintBorderLine(t, ctx, g, selfBounds);
        }
        return new HSizeRequirements(selfBounds);
    }



}
