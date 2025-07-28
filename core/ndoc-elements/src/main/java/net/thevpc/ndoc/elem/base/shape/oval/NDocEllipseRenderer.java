package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocEllipseRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocEllipseRenderer() {
        super(
                NDocNodeType.ELLIPSE,
                NDocNodeType.CIRCLE
        );
    }

    @Override
    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        NDocGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillOval((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
            }
            if (NDocNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                NDocNodeRendererUtils.withStroke(p, g, ctx,()->{
                    g.drawOval((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
                });
            }
        }
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}
