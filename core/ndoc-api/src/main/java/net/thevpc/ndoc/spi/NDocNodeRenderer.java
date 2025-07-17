package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public interface NDocNodeRenderer {

    NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx);

//    Bounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx);

    void render(NDocNode p, NDocNodeRendererContext ctx);

    String[] types();
}
