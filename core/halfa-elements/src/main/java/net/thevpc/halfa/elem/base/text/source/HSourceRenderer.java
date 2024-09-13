package net.thevpc.halfa.elem.base.text.source;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.elem.base.text.text.HTextRendererBuilderImpl;
import net.thevpc.halfa.elem.base.text.text.NutsHighlighterMapper;
import net.thevpc.halfa.spi.base.parser.HTextUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.text.HTextBaseRenderer;
import net.thevpc.halfa.spi.renderer.text.HTextRendererBuilder;
import net.thevpc.nuts.util.NStringUtils;

import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.text.NTextCode;
import net.thevpc.nuts.text.NTexts;

import java.awt.*;

public class HSourceRenderer extends HTextBaseRenderer {

    public HSourceRenderer() {
        super(HNodeType.SOURCE);
    }

    protected HTextRendererBuilder createRichTextHelper(HNode p, HNodeRendererContext ctx) {
        String lang = NStringUtils.trim(ObjEx.of(p.getPropertyValue(HPropName.LANG)).asStringOrName().orElse(""));
        String codeStr = (ObjEx.of(p.getPropertyValue(HPropName.VALUE)).asStringOrName().orElse(""));
        codeStr = HTextUtils.trimBloc(codeStr);
        NTexts ttt = NTexts.of(ctx.session());
        NTextCode ncode = ttt.ofCode(lang, codeStr);
        Paint fg = HValueByName.getForegroundColor(p, ctx,true);
        HTextRendererBuilderImpl result = new HTextRendererBuilderImpl(fg);
        NutsHighlighterMapper.highlightNutsText(lang, codeStr, ncode, p, ctx, result);
        return result;
    }


}
