package net.thevpc.ndoc.api.renderer.text;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.List;

public interface NDocTextRendererFlavor {
    String type();
    void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) ;
    List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx);
}
