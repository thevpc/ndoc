package net.thevpc.ndoc.engine.renderer.text;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererFlavor;

import java.awt.*;

public class NDocTextRenderer extends NDocTextBaseRenderer {

    public NDocTextRenderer() {
        super(NDocNodeType.TEXT);
    }

    public NDocTextRendererBuilder createRichTextHelper(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<NDocNode> all=new ArrayList<>();
        String text = NDocValue.of(p.getPropertyValue(NDocPropName.VALUE).orNull()).asStringOrName().orElse("");
        NDocTextRendererFlavor f = ctx.engine().textRendererFlavor("").get();
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl builder = new NDocTextRendererBuilderImpl(ctx.engine(),fg);
        f.buildText(text, new NDocTextOptions(), p, ctx, builder);
        return builder;
    }


}
