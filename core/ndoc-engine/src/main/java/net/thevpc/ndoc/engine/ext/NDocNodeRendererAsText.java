package net.thevpc.ndoc.engine.ext;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.NDocSizeRequirements;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.engine.renderer.text.NDocTextRendererBase;

class NDocNodeRendererAsText extends NDocTextRendererBase {
    private final NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext;

    public NDocNodeRendererAsText(NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext) {
        super(myNDocNodeCustomBuilderContext.id, myNDocNodeCustomBuilderContext.id);
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.renderMainAction != null) {
            myNDocNodeCustomBuilderContext.renderMainAction.renderMain(p, ctx, myNDocNodeCustomBuilderContext);
        }else {
            super.renderMain(p, ctx);
        }
    }

    @Override
    public NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.sizeRequirementsAction != null) {
            NDocSizeRequirements u = myNDocNodeCustomBuilderContext.sizeRequirementsAction.sizeRequirements(p, ctx, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        return super.sizeRequirements(p, ctx);
    }

    @Override
    public NDocBounds2 selfBounds(NDocNode t, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.selfBoundsAction != null) {
            NDocBounds2 u = myNDocNodeCustomBuilderContext.selfBoundsAction.selfBounds(t, ctx, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        return super.selfBounds(t, ctx);
    }
}
