package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;
import net.thevpc.ntexup.api.renderer.text.NTxTextToken;
import net.thevpc.ntexup.api.renderer.text.NTxTextTokenFlavored;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.Arrays;
import java.util.List;

class CustomNTxTextRendererFlavorFromBuilder implements NTxTextRendererFlavor {
    private final NTxNodeCustomBuilderContextImpl ctx;

    public CustomNTxTextRendererFlavorFromBuilder(NTxNodeCustomBuilderContextImpl ctx) {
        this.ctx = ctx;
    }

    @Override
    public String type() {
        return ctx.id;
    }

    @Override
    public void buildText(String text, NTxTextOptions options, NTxNode p, NTxNodeRendererContext ctx, NTxTextRendererBuilder builder) {
        this.ctx.renderTextAction.buildText(text, options, p, ctx, builder, this.ctx);
    }

    @Override
    public List<NTxTextToken> parseImmediate(NReservedSimpleCharQueue queue, NTxNodeRendererContext ctx) {
        if (this.ctx.renderEmbeddedTextAction != null) {
            List<NTxTextToken> u = this.ctx.renderEmbeddedTextAction.parseImmediate(queue, ctx, this.ctx);
            if (u != null) {
                return u;
            }
        }
        String flavor = NTxUtils.uid(this.ctx.id);

        String end;
        if (queue.peek(3 + flavor.length()).equals("[[" + flavor + ":")) {
            end = "]]";
            queue.read(3 + flavor.length());
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
                        new NTxTextTokenFlavored(type(), sb.toString().trim())
                );
            } else {
                char c = queue.read();
                sb.append(c);
            }
        }
        return Arrays.asList(
                new NTxTextTokenFlavored(type(), sb.toString().trim())
        );
    }

    @Override
    public String toString() {
        return "CustomFlavorFromBuilder(" + ctx.id() + ")";
    }
}
