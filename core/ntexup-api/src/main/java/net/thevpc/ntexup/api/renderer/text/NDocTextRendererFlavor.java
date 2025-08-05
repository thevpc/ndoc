package net.thevpc.ntexup.api.renderer.text;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.List;

public interface NDocTextRendererFlavor {
    String type();
    void buildText(String text, NDocTextOptions options, NTxNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) ;
    List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx);
}
