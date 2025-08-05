package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.engine.renderer.text.NDocTextRendererBase;

class NDocNodeRendererAsText extends NDocTextRendererBase {
    private final NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext;

    public NDocNodeRendererAsText(NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext) {
        super(myNDocNodeCustomBuilderContext.id, myNDocNodeCustomBuilderContext.id);
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public void renderMain(NTxNode p, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.renderMainAction != null) {
            myNDocNodeCustomBuilderContext.renderMainAction.renderMain(p, ctx, myNDocNodeCustomBuilderContext);
        }else {
            super.renderMain(p, ctx);
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
    public NDocBounds2 selfBounds(NTxNode t, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.selfBoundsAction != null) {
            NDocBounds2 u = myNDocNodeCustomBuilderContext.selfBoundsAction.selfBounds(t, ctx, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        return super.selfBounds(t, ctx);
    }
}
