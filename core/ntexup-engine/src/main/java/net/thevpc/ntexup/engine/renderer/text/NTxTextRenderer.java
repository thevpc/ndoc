package net.thevpc.ntexup.engine.renderer.text;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.eval.NTxValueByName;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;

import java.awt.*;

public class NTxTextRenderer extends NTxTextBaseRenderer {

    public NTxTextRenderer() {
        super(NTxNodeType.TEXT);
    }

    public NTxTextRendererBuilder createRichTextHelper(NTxNode p, NTxNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        String text = NTxValue.of(p.getPropertyValue(NTxPropName.VALUE).orNull()).asStringOrName().orElse("");
        NTxTextRendererFlavor f = ctx.engine().textRendererFlavor("").get();
        Paint fg = NTxValueByName.getForegroundColor(p, ctx,true);
        NTxTextRendererBuilderImpl builder = new NTxTextRendererBuilderImpl(ctx.engine(),fg);
        f.buildText(text, new NTxTextOptions(), p, ctx, builder);
        return builder;
    }


}
