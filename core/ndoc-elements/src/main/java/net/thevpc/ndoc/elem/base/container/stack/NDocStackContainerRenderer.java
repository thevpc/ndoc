package net.thevpc.ndoc.elem.base.container.stack;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

public class NDocStackContainerRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public NDocStackContainerRenderer() {
        super(HNodeType.STACK);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 selfBounds = selfBounds(p, ctx);
        NDocGraphics g = ctx.graphics();
        NDocNodeRendererContext ctx2 = ctx.withBounds(p, selfBounds);
        if (!ctx2.isDry()) {
            HNodeRendererUtils.paintBackground(p, ctx2, g, selfBounds);
        }
        NDocNodeRendererContext finalCtx = ctx;
        List<HNode> texts = p.children()
                .stream().filter(x -> NDocValueByName.isVisible(x, finalCtx)).collect(Collectors.toList());
        for (HNode text : texts) {
            ctx2.render(text);
        }
        if (!ctx2.isDry()) {
            HNodeRendererUtils.paintBorderLine(p, ctx2, g, selfBounds);
        }
    }


}
