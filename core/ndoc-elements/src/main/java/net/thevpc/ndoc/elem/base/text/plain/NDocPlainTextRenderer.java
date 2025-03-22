package net.thevpc.ndoc.elem.base.text.plain;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.elem.base.text.text.NDocTextRendererBuilderImpl;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.text.NDocRichTextToken;
import net.thevpc.ndoc.spi.renderer.text.NDocRichTextTokenType;
import net.thevpc.ndoc.spi.renderer.text.NDocTextBaseRenderer;
import net.thevpc.ndoc.spi.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.*;

public class NDocPlainTextRenderer extends NDocTextBaseRenderer {
    public NDocPlainTextRenderer() {
        super(HNodeType.PLAIN);
    }


    public NDocTextRendererBuilder createRichTextHelper(HNode p, NDocNodeRendererContext ctx) {
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilder helper = new NDocTextRendererBuilderImpl(fg);
        TsonElement d = p.getPropertyValue(HPropName.VALUE).orElse(Tson.of(""));
        String message = NStringUtils.trim(d.toStr().value());
        String[] allLines = message.trim().split("[\n]");
        for (int i = 0; i < allLines.length; i++) {
            allLines[i] = allLines[i].trim();
            NDocRichTextToken c = new NDocRichTextToken(NDocRichTextTokenType.PLAIN, allLines[i]);
            NDocGraphics g = ctx.graphics();
            g.setFont(c.textOptions.font);
            c.bounds = g.getStringBounds(c.text);
            helper.nextLine().addToken(c);
        }
        return helper;
    }

}
