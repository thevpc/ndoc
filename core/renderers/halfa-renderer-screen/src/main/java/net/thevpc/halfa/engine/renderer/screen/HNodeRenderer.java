package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public interface HNodeRenderer {
    HSizeRequirements render(HNode p, HNodeRendererContext ctx);

    String[] types();
}
