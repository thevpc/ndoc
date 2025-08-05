package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.util.NDocUtils;

public class NDocCircleBuilder implements NDocNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NTxNodeType.CIRCLE)
                .renderComponent(this::renderMain)
                ;
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxBounds2 b = ctx.selfBounds(p, null, null);
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
