package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public interface NDocNodeRenderer {

    NDocSizeRequirements sizeRequirements(HNode p, NDocNodeRendererContext ctx);

//    Bounds2 selfBounds(HNode p, HNodeRendererContext ctx);

    void render(HNode p, NDocNodeRendererContext ctx);

    String[] types();
}
