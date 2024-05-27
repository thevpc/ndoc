package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;

public abstract class ConvertedHPartRenderer extends AbstractHNodeRenderer {
    public ConvertedHPartRenderer(String... types) {
        super(types);
    }

    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        HNode c = convert(p, ctx).setParent(p);
        return ctx.sizeRequirementsOf(c);
    }

    public abstract HNode convert(HNode p, HNodeRendererContext ctx);

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx.render(convert(p, ctx).setParent(p));
    }

}
