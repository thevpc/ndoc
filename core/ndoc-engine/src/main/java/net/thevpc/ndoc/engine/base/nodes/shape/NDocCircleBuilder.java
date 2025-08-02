package net.thevpc.ndoc.elem.base.shape;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
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
        NDocBounds2 b = ctx.selfBounds(p, null, null);
        double x = b.getX();
        double y = b.getY();
        NDocGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            int ww = NDocUtils.intOf(b.getWidth());
            int hh = NDocUtils.intOf(b.getHeight());
            ww=Math.min(ww,hh);
            hh=ww;
            if (someBG = ctx.applyBackgroundColor(p)) {
                g.fillOval((int) x, (int) y, ww, hh);
            }
            if (ctx.applyForeground(p, !someBG)) {
                int finalWw = ww;
                int finalHh = hh;
                ctx.withStroke(p,()->{
                    g.drawOval((int) x, (int) y, finalWw, finalHh);
                });
            }
        }
        ctx.paintDebugBox(p, b);
    }
}
