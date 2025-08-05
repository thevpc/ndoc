package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.*;


public interface NTxNodeRenderer {

    NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext ctx);

    NTxBounds2 selfBounds(NTxNode p, NTxNodeRendererContext ctx);

    void render(NTxNode p, NTxNodeRendererContext ctx);

    String[] types();
}
