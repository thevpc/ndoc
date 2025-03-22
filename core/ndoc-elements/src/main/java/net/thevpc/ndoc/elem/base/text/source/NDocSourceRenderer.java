package net.thevpc.ndoc.elem.base.text.source;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.elem.base.text.text.NDocTextRendererBuilderImpl;
import net.thevpc.ndoc.elem.base.text.text.NutsHighlighterMapper;
import net.thevpc.ndoc.spi.base.parser.HTextUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.text.NDocTextBaseRenderer;
import net.thevpc.ndoc.spi.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.util.NStringUtils;

import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.text.NTextCode;
import net.thevpc.nuts.text.NTexts;

import java.awt.*;

public class NDocSourceRenderer extends NDocTextBaseRenderer {

    public NDocSourceRenderer() {
        super(HNodeType.SOURCE);
    }

    protected NDocTextRendererBuilder createRichTextHelper(HNode p, NDocNodeRendererContext ctx) {
        String lang = NStringUtils.trim(NDocObjEx.of(p.getPropertyValue(HPropName.LANG)).asStringOrName().orElse(""));
        String codeStr = (NDocObjEx.of(p.getPropertyValue(HPropName.VALUE)).asStringOrName().orElse(""));
        codeStr = HTextUtils.trimBloc(codeStr);
        NTexts ttt = NTexts.of();
        NTextCode ncode = ttt.ofCode(lang, codeStr);
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl result = new NDocTextRendererBuilderImpl(fg);
        NutsHighlighterMapper.highlightNutsText(lang, codeStr, ncode, p, ctx, result);
        return result;
    }


}
