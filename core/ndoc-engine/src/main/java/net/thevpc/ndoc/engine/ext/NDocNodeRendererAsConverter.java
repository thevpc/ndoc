package net.thevpc.ndoc.engine;

import net.thevpc.ndoc.api.base.renderer.ConvertedNDocNodeRenderer;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

class NDocNodeRendererAsConverter extends ConvertedNDocNodeRenderer {
    private final MyNDocNodeCustomBuilderContext myNDocNodeCustomBuilderContext;

    public NDocNodeRendererAsConverter(MyNDocNodeCustomBuilderContext myNDocNodeCustomBuilderContext) {
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
