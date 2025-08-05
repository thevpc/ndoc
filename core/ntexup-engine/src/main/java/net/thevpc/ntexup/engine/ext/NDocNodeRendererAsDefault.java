package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.engine.renderer.NDocNodeRendererBase;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

class NDocNodeRendererAsDefault extends NDocNodeRendererBase {
    private final NTxNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext;

    public NDocNodeRendererAsDefault(NTxNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext) {
        super(myNDocNodeCustomBuilderContext.id);
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public void renderMain(NTxNode p, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.renderMainAction != null) {
            myNDocNodeCustomBuilderContext.renderMainAction.renderMain(p, ctx, myNDocNodeCustomBuilderContext);
        }
    }

    @Override
    public NDocSizeRequirements sizeRequirements(NTxNode p, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.sizeRequirementsAction != null) {
            NDocSizeRequirements u = myNDocNodeCustomBuilderContext.sizeRequirementsAction.sizeRequirements(p, ctx, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        return super.sizeRequirements(p, ctx);
    }

    @Override
    public NTxBounds2 selfBounds(NTxNode t, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.selfBoundsAction != null) {
            NTxBounds2 u = myNDocNodeCustomBuilderContext.selfBoundsAction.selfBounds(t, ctx, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        return super.selfBounds(t, ctx);
    }
}
