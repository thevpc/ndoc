package net.thevpc.ndoc.spi.base.renderer;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;

public abstract class ConvertedNDocNodeRenderer extends NDocNodeRendererBase {
    public ConvertedNDocNodeRenderer(String... types) {
        super(types);
    }

    @Override
    public NDocSizeRequirements sizeRequirements(HNode p, NDocNodeRendererContext ctx) {
        HNode c = convert(p, ctx).setParent(p);
        return ctx.sizeRequirementsOf(c);
    }

    public abstract HNode convert(HNode p, NDocNodeRendererContext ctx);

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx.render(convert(p, ctx).setParent(p));
    }

}
