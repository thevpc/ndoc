package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;

public abstract class ConvertedHPartRenderer extends AbstractHPartRenderer {
    public abstract HNode convert(HNode p, HPartRendererContext ctx) ;

    private HNode convertAndInherit(HNode p, HPartRendererContext ctx) {
        HNode t = convert(p, ctx);
        //just to inherit rules and so on...
        t.setParent(p);
        return t;
    }

    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        return ctx.computeSizeRequirements(convertAndInherit(p, ctx));
    }

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        return ctx.paintPagePart(convertAndInherit(p, ctx));
    }

}
