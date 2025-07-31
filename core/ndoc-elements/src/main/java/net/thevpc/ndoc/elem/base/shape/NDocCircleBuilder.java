package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.util.NDocUtils;

public class NDocCircleBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.CIRCLE)
                .renderComponent(this::renderMain)
                ;
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        NDocGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            int ww = NDocUtils.intOf(b.getWidth());
            int hh = NDocUtils.intOf(b.getHeight());
            ww=Math.min(ww,hh);
            hh=ww;
            if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillOval((int) x, (int) y, ww, hh);
            }
            if (NDocNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                int finalWw = ww;
                int finalHh = hh;
                NDocNodeRendererUtils.withStroke(p, g, ctx,()->{
                    g.drawOval((int) x, (int) y, finalWw, finalHh);
                });
            }
        }
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }
}
