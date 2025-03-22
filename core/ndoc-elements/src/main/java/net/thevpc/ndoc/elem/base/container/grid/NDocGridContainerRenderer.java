package net.thevpc.ndoc.elem.base.container.grid;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public class NDocGridContainerRenderer extends NDocNodeRendererBase {

    HProperties defaultStyles = new HProperties();

    public NDocGridContainerRenderer() {
        super(HNodeType.GRID);
    }


    @Override
    public Bounds2 selfBounds(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 expectedBounds = super.selfBounds(p, ctx);
//        HGraphics g = ctx.graphics();
//        g.setColor(Color.RED);
//        g.drawRect(expectedBounds);
        HGridRendererHelper h = new HGridRendererHelper(p.children());
        return h.computeBound(p, ctx, expectedBounds);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 expectedBounds = selfBounds(p, ctx);
        HGridRendererHelper h = new HGridRendererHelper(p.children());
        h.render(p, ctx, expectedBounds);
    }


}
