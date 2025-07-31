package net.thevpc.ndoc.engine.ext;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.renderer.NDocTextToken;
import net.thevpc.ndoc.api.renderer.NDocTextTokenFlavored;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.Arrays;
import java.util.List;

class CustomNDocTextRendererFlavorFromBuilder2 implements NDocTextRendererFlavor {
    private final NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext;

    public CustomNDocTextRendererFlavorFromBuilder2(NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext) {
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public String type() {
        return myNDocNodeCustomBuilderContext.id;
    }

    @Override
    public void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        myNDocNodeCustomBuilderContext.renderTextAction.buildText(text, options, p, ctx, builder, myNDocNodeCustomBuilderContext);
    }

    @Override
    public List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx) {
        if (myNDocNodeCustomBuilderContext.renderEmbeddedTextAction != null) {
            List<NDocTextToken> u = myNDocNodeCustomBuilderContext.renderEmbeddedTextAction.parseImmediate(queue, ctx, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        String flavor = NDocUtils.uid(myNDocNodeCustomBuilderContext.id);

        String end;
        if (queue.peek(3 + flavor.length()).equals("[[" + flavor + ":")) {
            end = "]]";
        } else {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        while (queue.hasNext()) {
            String u = queue.peek(3);
            if (u.equals("\\" + end)) {
                sb.append(queue.read(3));
            } else if (queue.peek(2).equals(end)) {
                queue.read(2);
                return Arrays.asList(
                        new NDocTextTokenFlavored(type(), sb.toString().trim())
                );
            } else {
                char c = queue.read();
                sb.append(c);
            }
        }
        return Arrays.asList(
                new NDocTextTokenFlavored(type(), sb.toString().trim())
        );
    }
}
