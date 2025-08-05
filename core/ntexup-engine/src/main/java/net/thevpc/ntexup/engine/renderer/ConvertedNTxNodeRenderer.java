package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;

public abstract class ConvertedNTxNodeRenderer extends NTxNodeRendererBase {
    public ConvertedNTxNodeRenderer(String... types) {
        super(types);
    }

    @Override
    public NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext ctx) {
        NTxNode c = convert(p, ctx).setParent(p);
        return ctx.sizeRequirementsOf(c);
    }

    public abstract NTxNode convert(NTxNode p, NTxNodeRendererContext ctx);

    public void renderMain(NTxNode p, NTxNodeRendererContext ctx) {
        ctx.render(convert(p, ctx).setParent(p));
    }

}
