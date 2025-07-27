package net.thevpc.ndoc.elem.base.container.stack;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

public class NDocStackContainerRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocStackContainerRenderer() {
        super(NDocNodeType.GROUP);
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
