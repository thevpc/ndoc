package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.node.*;


public interface NDocNodeRenderer {

    NDocSizeRequirements sizeRequirements(NTxNode p, NDocNodeRendererContext ctx);

    NDocBounds2 selfBounds(NTxNode p, NDocNodeRendererContext ctx);

    void render(NTxNode p, NDocNodeRendererContext ctx);

    String[] types();
}
