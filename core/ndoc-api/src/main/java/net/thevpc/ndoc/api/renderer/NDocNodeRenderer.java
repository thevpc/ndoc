package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.model.*;


public interface NDocNodeRenderer {

    NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx);

//    Bounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx);

    void render(NDocNode p, NDocNodeRendererContext ctx);

    String[] types();
}
