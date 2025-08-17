package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.engine.renderer.NTxNodeRendererBase;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

class NTxNodeRendererAsDefault extends NTxNodeRendererBase {
    private final NTxNodeBuilderContextImpl ctx;

    public NTxNodeRendererAsDefault(NTxNodeBuilderContextImpl ctx) {
        super(ctx.id);
        this.ctx = ctx;
    }

    @Override
    public void renderMain(NTxNodeRendererContext ctx) {
        if (this.ctx.renderMainAction != null) {
            this.ctx.renderMainAction.renderMain(ctx, this.ctx);
        }
    }

    @Override
    public NTxSizeRequirements sizeRequirements(NTxNodeRendererContext ctx) {
        if (this.ctx.sizeRequirementsAction != null) {
            NTxSizeRequirements u = this.ctx.sizeRequirementsAction.sizeRequirements(ctx, this.ctx);
            if (u != null) {
                return u;
            }
        }
        return super.sizeRequirements(ctx);
    }

    @Override
    public NTxBounds2 selfBounds(NTxNodeRendererContext ctx) {
        if (this.ctx.selfBoundsAction != null) {
            NTxBounds2 u = this.ctx.selfBoundsAction.selfBounds(ctx, this.ctx);
            if (u != null) {
                return u;
            }
        }
        return super.selfBounds(ctx);
    }
}
