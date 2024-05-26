package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.model.HPoint;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;

public class HLineRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HLineRenderer() {
        super(HNodeType.LINE);
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        Double2 from = HPoint.ofParent(ObjEx.ofProp(p, HPropName.FROM).asDouble2().get()).value(b, ctx.getGlobalBounds());
        Double2 to = HPoint.ofParent(ObjEx.ofProp(p, HPropName.TO).asDouble2().get()).value(b, ctx.getGlobalBounds());
        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            applyLineColor(p, g, ctx, true);
            g.drawLine(HUtils.doubleOf(from.getX()), HUtils.doubleOf(from.getY()),
                    HUtils.doubleOf(to.getX()), HUtils.doubleOf(to.getY())
            );
        }
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        return new HSizeRequirements(new Bounds2(minx, miny, maxX, maxY));
    }

}
