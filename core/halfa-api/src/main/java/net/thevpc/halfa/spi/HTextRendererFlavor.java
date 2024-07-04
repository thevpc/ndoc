package net.thevpc.halfa.spi;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.text.HTextRendererBuilder;

public interface HTextRendererFlavor {
    String type();
    void buildText(String text, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) ;
}
