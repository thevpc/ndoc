package net.thevpc.ndoc.elem.base.text.text;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.base.parser.HTextUtils;
import net.thevpc.ndoc.spi.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.spi.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.spi.NDocTextRendererFlavor;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.text.NTexts;

public class NDocTextRendererFlavorNTF implements NDocTextRendererFlavor {
    @Override
    public String type() {
        return "ntf";
    }

    @Override
    public void buildText(String text, NDocTextOptions options, HNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        String lang = "ntf";
        String codeStr = text;
        codeStr = HTextUtils.trimBloc(codeStr);
        NTexts ttt = NTexts.of();
        NText ncode = ttt.of(codeStr);
        NutsHighlighterMapper.highlightNutsText(lang, codeStr, ncode, p, ctx,builder);
    }
}
