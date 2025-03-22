package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.spi.renderer.text.NDocTextRendererBuilder;

public interface NDocTextRendererFlavor {
    String type();
    void buildText(String text, NDocTextOptions options, HNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) ;
}
