package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;

public interface NDocTextRendererFlavor {
    String type();
    void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) ;
}
