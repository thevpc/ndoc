package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.engine.renderer.ConvertedNTxNodeRenderer;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

class NTxNodeRendererAsConverter extends ConvertedNTxNodeRenderer {
    private final NTxNodeCustomBuilderContextImpl ctx;

    public NTxNodeRendererAsConverter(NTxNodeCustomBuilderContextImpl ctx) {
        super(ctx.id());
        this.ctx = ctx;
    }

    @Override
    public NTxNode convert(NTxNode p, NTxNodeRendererContext ctx) {
        if (this.ctx.renderConvertAction != null) {
            return this.ctx.renderConvertAction.convert(p, ctx, this.ctx);
        }
        return p;
    }
}
