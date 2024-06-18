package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.renderer.screen.common.HNodeRendererUtils;
import net.thevpc.halfa.spi.nodes.HPropValueByNameParser;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;

public class HArcRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Bounds2 b = HPropValueByNameParser.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double startAngle = (double) p.getPropertyValue(HPropName.FROM).orElse(0.0);
        double endAngle = (double) p.getPropertyValue(HPropName.TO).orElse(0.0);
        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            HNodeRendererUtils.applyLineColor(p, g, ctx, true);
            HNodeRendererUtils.applyStroke(p, g, ctx);
            g.drawArc((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()),
                    (int) startAngle,
                    (int) endAngle
            );
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}
