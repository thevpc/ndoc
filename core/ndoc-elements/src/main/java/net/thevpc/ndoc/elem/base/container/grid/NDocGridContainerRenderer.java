package net.thevpc.ndoc.elem.base.container.grid;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocGridContainerRenderer extends NDocNodeRendererBase {

    NDocProperties defaultStyles = new NDocProperties();

    public NDocGridContainerRenderer() {
        super(NDocNodeType.GRID);
    }


    @Override
    public NDocBounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 expectedBounds = super.selfBounds(p, ctx);
//        HGraphics g = ctx.graphics();
//        g.setColor(Color.RED);
//        g.drawRect(expectedBounds);
        NDocGridRendererHelper h = new NDocGridRendererHelper(p.children());
        return h.computeBound(p, ctx, expectedBounds);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 expectedBounds = selfBounds(p, ctx);
        NDocGridRendererHelper h = new NDocGridRendererHelper(p.children());
        h.render(p, ctx, expectedBounds);
    }


}
