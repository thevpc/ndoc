package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.NDocSizeRequirements;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.*;


public interface NDocNodeRenderer {

    NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx);

    NDocBounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx);

    void render(NDocNode p, NDocNodeRendererContext ctx);

    String[] types();
}
