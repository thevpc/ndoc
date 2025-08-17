package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;

public abstract class ConvertedNTxNodeRenderer extends NTxNodeRendererBase {
    public ConvertedNTxNodeRenderer(String... types) {
        super(types);
    }

    @Override
    public NTxSizeRequirements sizeRequirements(NTxNodeRendererContext rendererContext) {
        NTxNode node = rendererContext.node();
        NTxNode c = convert(node, rendererContext).setParent(node);
        return rendererContext.sizeRequirementsOf(c);
    }

    public abstract NTxNode convert(NTxNode p, NTxNodeRendererContext rendererContext);

    public void renderMain(NTxNodeRendererContext ctx) {
        NTxNode p2 = convert(ctx.node(), ctx).setParent(ctx.node());
        ctx.withNode(p2).render();
    }

}
