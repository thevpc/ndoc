package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.engine.renderer.ConvertedNDocNodeRenderer;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

class NDocNodeRendererAsConverter extends ConvertedNDocNodeRenderer {
    private final NTxNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext;

    public NDocNodeRendererAsConverter(NTxNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext) {
        super(myNDocNodeCustomBuilderContext.id());
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public NTxNode convert(NTxNode p, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.renderConvertAction != null) {
            return myNDocNodeCustomBuilderContext.renderConvertAction.convert(p, ctx, myNDocNodeCustomBuilderContext);
        }
        return p;
    }
}
