package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.util.List;

public class HStackContainerRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HStackContainerRenderer() {
        super(HNodeType.STACK);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 selfBounds = selfBounds(p, ctx);
        HGraphics g = ctx.graphics();

        if (!ctx.isDry()) {
            paintBackground(p, ctx, g, selfBounds);
        }
        List<HNode> texts = p.children();
        for (HNode text : texts) {
            ctx.render(text);
        }
        if (!ctx.isDry()) {
            paintBorderLine(p, ctx, g, selfBounds);
        }
    }


}
