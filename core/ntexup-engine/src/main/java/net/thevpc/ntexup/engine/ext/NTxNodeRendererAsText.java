package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.engine.renderer.text.NTxTextRendererBase;

class NTxNodeRendererAsText extends NTxTextRendererBase {
    private final NTxNodeCustomBuilderContextImpl ctx;

    public NTxNodeRendererAsText(NTxNodeCustomBuilderContextImpl ctx) {
        super(ctx.id, ctx.id);
        this.ctx = ctx;
    }

    @Override
    public void renderMain(NTxNode p, NTxNodeRendererContext ctx) {
        if (this.ctx.renderMainAction != null) {
            this.ctx.renderMainAction.renderMain(p, ctx, this.ctx);
        }else {
            super.renderMain(p, ctx);
        }
    }

    @Override
    public NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext ctx) {
        if (this.ctx.sizeRequirementsAction != null) {
            NTxSizeRequirements u = this.ctx.sizeRequirementsAction.sizeRequirements(p, ctx, this.ctx);
            if (u != null) {
                return u;
            }
        }
        return super.sizeRequirements(p, ctx);
    }

    @Override
    public NTxBounds2 selfBounds(NTxNode t, NTxNodeRendererContext ctx) {
        if (this.ctx.selfBoundsAction != null) {
            NTxBounds2 u = this.ctx.selfBoundsAction.selfBounds(t, ctx, this.ctx);
            if (u != null) {
                return u;
            }
        }
        return super.selfBounds(t, ctx);
    }
}
