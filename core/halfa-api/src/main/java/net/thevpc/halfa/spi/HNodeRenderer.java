package net.thevpc.halfa.spi;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public interface HNodeRenderer {

    HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx);

//    Bounds2 selfBounds(HNode p, HNodeRendererContext ctx);

    void render(HNode p, HNodeRendererContext ctx);

    String[] types();
}
