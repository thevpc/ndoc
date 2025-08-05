package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.document.NDocSizeRequirements;

public abstract class ConvertedNDocNodeRenderer extends NDocNodeRendererBase {
    public ConvertedNDocNodeRenderer(String... types) {
        super(types);
    }

    @Override
    public NDocSizeRequirements sizeRequirements(NTxNode p, NDocNodeRendererContext ctx) {
        NTxNode c = convert(p, ctx).setParent(p);
        return ctx.sizeRequirementsOf(c);
    }

    public abstract NTxNode convert(NTxNode p, NDocNodeRendererContext ctx);

    public void renderMain(NTxNode p, NDocNodeRendererContext ctx) {
        ctx.render(convert(p, ctx).setParent(p));
    }

}
