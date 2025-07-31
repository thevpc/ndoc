package net.thevpc.ndoc.engine.ext;

import net.thevpc.ndoc.engine.renderer.ConvertedNDocNodeRenderer;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

class NDocNodeRendererAsConverter extends ConvertedNDocNodeRenderer {
    private final NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext;

    public NDocNodeRendererAsConverter(NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext) {
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public NDocNode convert(NDocNode p, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.renderConvertAction != null) {
            return myNDocNodeCustomBuilderContext.renderConvertAction.convert(p, ctx, myNDocNodeCustomBuilderContext);
        }
        return p;
    }
}
