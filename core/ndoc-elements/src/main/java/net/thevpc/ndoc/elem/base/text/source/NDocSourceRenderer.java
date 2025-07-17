package net.thevpc.ndoc.elem.base.text.source;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocPropName;
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
        super(NDocNodeType.SOURCE);
    }

    protected NDocTextRendererBuilder createRichTextHelper(NDocNode p, NDocNodeRendererContext ctx) {
        String lang = NStringUtils.trim(NDocObjEx.of(p.getPropertyValue(NDocPropName.LANG)).asStringOrName().orElse(""));
        String codeStr = (NDocObjEx.of(p.getPropertyValue(NDocPropName.VALUE)).asStringOrName().orElse(""));
        codeStr = HTextUtils.trimBloc(codeStr);
        NTexts ttt = NTexts.of();
        NTextCode ncode = ttt.ofCode(lang, codeStr);
        Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
        NDocTextRendererBuilderImpl result = new NDocTextRendererBuilderImpl(fg);
        NutsHighlighterMapper.highlightNutsText(lang, codeStr, ncode, p, ctx, result);
        return result;
    }


}
