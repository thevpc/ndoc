package net.thevpc.ntexup.engine.renderer.text;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.eval.NDocValueByName;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.renderer.text.NDocTextOptions;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererFlavor;

import java.awt.*;

public class NDocTextRenderer extends NDocTextBaseRenderer {

    public NDocTextRenderer() {
        super(NTxNodeType.TEXT);
    }

    public NDocTextRendererBuilder createRichTextHelper(NTxNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<NDocNode> all=new ArrayList<>();
        String text = NDocValue.of(p.getPropertyValue(NTxPropName.VALUE).orNull()).asStringOrName().orElse("");
        NDocTextRendererFlavor f = ctx.engine().textRendererFlavor("").get();
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl builder = new NDocTextRendererBuilderImpl(ctx.engine(),fg);
        f.buildText(text, new NDocTextOptions(), p, ctx, builder);
        return builder;
    }


}
