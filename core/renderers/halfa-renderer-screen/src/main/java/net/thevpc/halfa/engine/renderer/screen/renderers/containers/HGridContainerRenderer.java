package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class HGridContainerRenderer extends AbstractHNodeRenderer {

    HProperties defaultStyles = new HProperties();

    public HGridContainerRenderer() {
        super(HNodeType.GRID);
    }


    @Override
    public Bounds2 selfBounds(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 expectedBounds = super.selfBounds(p, ctx);
//        HGraphics g = ctx.graphics();
//        g.setColor(Color.RED);
//        g.drawRect(expectedBounds);
        HGridRendererHelper h = new HGridRendererHelper(p.children());
        return h.computeBound(p, ctx, expectedBounds);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 expectedBounds = selfBounds(p, ctx);
        HGridRendererHelper h = new HGridRendererHelper(p.children());
        h.render(p, ctx, expectedBounds);
    }


}
