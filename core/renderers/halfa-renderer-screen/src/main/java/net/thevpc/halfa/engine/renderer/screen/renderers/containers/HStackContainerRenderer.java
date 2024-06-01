package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

public class HStackContainerRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HStackContainerRenderer() {
        super(HNodeType.STACK);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 selfBounds = selfBounds(p, ctx);
        HGraphics g = ctx.graphics();
        HNodeRendererContext ctx2 = ctx.withBounds(p, selfBounds);
        if (!ctx2.isDry()) {
            paintBackground(p, ctx2, g, selfBounds);
        }
        HNodeRendererContext finalCtx = ctx;
        List<HNode> texts = p.children()
                .stream().filter(x-> resolveVisible(x, finalCtx.graphics(), finalCtx)).collect(Collectors.toList())
                ;
        for (HNode text : texts) {
            ctx2.render(text);
        }
        if (!ctx2.isDry()) {
            paintBorderLine(p, ctx2, g, selfBounds);
        }
    }


}
