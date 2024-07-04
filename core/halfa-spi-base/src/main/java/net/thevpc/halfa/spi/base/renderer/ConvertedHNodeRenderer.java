package net.thevpc.halfa.spi.base.renderer;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;

public abstract class ConvertedHNodeRenderer extends HNodeRendererBase {
    public ConvertedHNodeRenderer(String... types) {
        super(types);
    }

    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        HNode c = convert(p, ctx).setParent(p);
        return ctx.sizeRequirementsOf(c);
    }

    public abstract HNode convert(HNode p, HNodeRendererContext ctx);

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx.render(convert(p, ctx).setParent(p));
    }

}
