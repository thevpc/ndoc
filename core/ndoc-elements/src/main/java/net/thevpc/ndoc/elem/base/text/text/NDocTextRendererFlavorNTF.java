package net.thevpc.ndoc.elem.base.text.text;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.base.parser.HTextUtils;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.renderer.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.text.NTexts;

public class NDocTextRendererFlavorNTF implements NDocTextRendererFlavor {
    @Override
    public String type() {
        return "ntf";
    }

    @Override
    public void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        String lang = "ntf";
        String codeStr = text;
        codeStr = HTextUtils.trimBloc(codeStr);
        NTexts ttt = NTexts.of();
        NText ncode = ttt.of(codeStr);
        NutsHighlighterMapper.highlightNutsText(lang, codeStr, ncode, p, ctx,builder);
    }
}
