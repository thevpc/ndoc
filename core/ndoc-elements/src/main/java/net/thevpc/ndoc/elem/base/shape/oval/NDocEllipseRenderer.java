package net.thevpc.ndoc.elem.base.shape.oval;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public class NDocEllipseRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public NDocEllipseRenderer() {
        super(
                HNodeType.ELLIPSE,
                HNodeType.CIRCLE
        );
    }

    @Override
    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        NDocGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillOval((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight()));
            }
            if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                HNodeRendererUtils.withStroke(p, g, ctx,()->{
                    g.drawOval((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
                });
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}
