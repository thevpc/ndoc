package net.thevpc.halfa.elem.base.text.text;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.base.parser.HTextUtils;
import net.thevpc.halfa.spi.renderer.text.HTextOptions;
import net.thevpc.halfa.spi.renderer.text.HTextRendererBuilder;
import net.thevpc.halfa.spi.HTextRendererFlavor;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.text.NTexts;

public class HTextRendererFlavorNTF implements HTextRendererFlavor {
    @Override
    public String type() {
        return "ntf";
    }

    @Override
    public void buildText(String text, HTextOptions options, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        String lang = "ntf";
        String codeStr = text;
        codeStr = HTextUtils.trimBloc(codeStr);
        NTexts ttt = NTexts.of(ctx.session());
        NText ncode = ttt.parse(codeStr);
        NutsHighlighterMapper.highlightNutsText(lang, codeStr, ncode, p, ctx,builder);
    }
}
