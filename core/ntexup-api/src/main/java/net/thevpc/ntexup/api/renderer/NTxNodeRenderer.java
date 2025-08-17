package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;


public interface NTxNodeRenderer {

    NTxSizeRequirements sizeRequirements(NTxNodeRendererContext ctx);

    NTxBounds2 selfBounds(NTxNodeRendererContext ctx);

    void render(NTxNodeRendererContext rendererContext);

    String[] types();
}
