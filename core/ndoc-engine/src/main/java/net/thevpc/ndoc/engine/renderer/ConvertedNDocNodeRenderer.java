package net.thevpc.ndoc.api.base.renderer;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.model.NDocSizeRequirements;

public abstract class ConvertedNDocNodeRenderer extends NDocNodeRendererBase {
    public ConvertedNDocNodeRenderer(String... types) {
        super(types);
    }

    @Override
    public NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx) {
        NDocNode c = convert(p, ctx).setParent(p);
        return ctx.sizeRequirementsOf(c);
    }

    public abstract NDocNode convert(NDocNode p, NDocNodeRendererContext ctx);

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx.render(convert(p, ctx).setParent(p));
    }

}
