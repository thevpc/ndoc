package net.thevpc.ntexup.engine.renderer.text;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.eval.NTxValueByName;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
import net.thevpc.ntexup.api.util.NtxFontInfo;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NTxTextRendererBase extends NTxTextBaseRenderer {
    private String flavor;

    public NTxTextRendererBase(String type, String flavor) {
        super(type);
        this.flavor = flavor;
    }

    public NTxTextRendererBuilder createRichTextHelper(NTxNode p, NTxNodeRendererContext ctx) {
        NTxTextRendererFlavor f = ctx.engine().textRendererFlavor(flavor).orNull();
        if (f == null) {
            ctx.engine().log().log(NMsg.ofC("TextRendererFlavor not found %s", flavor));
            f = ctx.engine().textRendererFlavor("text").get();
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        String text = NTxValue.of(p.getPropertyValue(NTxPropName.VALUE).orNull()).asStringOrName().orElse("");
        Paint fg = NTxValueByName.getForegroundColor(p, ctx, true);
        NtxFontInfo font = NTxValueByName.getFontInfo(p, ctx);
        if (font.baseFont == null && NBlankable.isBlank(font.family)) {
            font.baseFont = ctx.graphics().getFont();
        }
        NTxTextRendererBuilderImpl builder = new NTxTextRendererBuilderImpl(ctx.engine(), fg, font);
        f.buildText(text, new NTxTextOptions(), p, ctx, builder);
        return builder;
    }


}
