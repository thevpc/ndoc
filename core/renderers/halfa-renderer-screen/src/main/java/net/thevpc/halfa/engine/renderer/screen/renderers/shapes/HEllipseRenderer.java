package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engine.renderer.screen.common.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;

public class HEllipseRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HEllipseRenderer() {
        super(
                HNodeType.ELLIPSE,
                HNodeType.CIRCLE
        );
    }

    @Override
    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = HValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        HGraphics g = ctx.graphics();
        boolean someBG = false;
        if (!ctx.isDry()) {
            if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                g.fillOval((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }
            if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                HNodeRendererUtils.applyStroke(p, g, ctx);
                g.drawOval((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}
