package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.model.HSizeRequirements;

public interface HNodeRenderer {

    HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx);

//    Bounds2 selfBounds(HNode p, HNodeRendererContext ctx);

    void render(HNode p, HNodeRendererContext ctx);

    String[] types();
}
