package net.thevpc.ndoc.elem.base.container.stack;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.spi.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

public class NDocStackContainerRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocStackContainerRenderer() {
        super(NDocNodeType.STACK);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 selfBounds = selfBounds(p, ctx);
        NDocGraphics g = ctx.graphics();
        NDocNodeRendererContext ctx2 = ctx.withBounds(p, selfBounds);
        if (!ctx2.isDry()) {
            NDocNodeRendererUtils.paintBackground(p, ctx2, g, selfBounds);
        }
        NDocNodeRendererContext finalCtx = ctx;
        List<NDocNode> texts = p.children()
                .stream().filter(x -> NDocValueByName.isVisible(x, finalCtx)).collect(Collectors.toList());
        for (NDocNode text : texts) {
            ctx2.render(text);
        }
        if (!ctx2.isDry()) {
            NDocNodeRendererUtils.paintBorderLine(p, ctx2, g, selfBounds);
        }
    }


}
