package net.thevpc.halfa.elem.base.text.plain;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.elem.base.text.text.HTextRendererBuilderImpl;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.text.HRichTextToken;
import net.thevpc.halfa.spi.renderer.text.HRichTextTokenType;
import net.thevpc.halfa.spi.renderer.text.HTextBaseRenderer;
import net.thevpc.halfa.spi.renderer.text.HTextRendererBuilder;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.*;

public class HPlainTextRenderer extends HTextBaseRenderer {
    public HPlainTextRenderer() {
        super(HNodeType.PLAIN);
    }


    public HTextRendererBuilder createRichTextHelper(HNode p, HNodeRendererContext ctx) {
        Paint fg = HValueByName.getForegroundColor(p, ctx,true);
        HTextRendererBuilder helper = new HTextRendererBuilderImpl(fg);
        TsonElement d = p.getPropertyValue(HPropName.VALUE).orElse(Tson.of(""));
        String message = NStringUtils.trim(d.toStr().value());
        String[] allLines = message.trim().split("[\n]");
        for (int i = 0; i < allLines.length; i++) {
            allLines[i] = allLines[i].trim();
            HRichTextToken c = new HRichTextToken(HRichTextTokenType.PLAIN, allLines[i]);
            HGraphics g = ctx.graphics();
            g.setFont(c.textOptions.font);
            c.bounds = g.getStringBounds(c.text);
            helper.nextLine().addToken(c);
        }
        return helper;
    }

}
