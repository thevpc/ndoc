package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.renderer.screen.renderers.text.util.*;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.util.NStringUtils;

import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.text.NTextCode;
import net.thevpc.nuts.text.NTexts;

public class HSourceRenderer extends HTextBaseRenderer {

    public HSourceRenderer() {
        super(HNodeType.SOURCE);
    }

    protected HRichTextHelper createRichTextHelper(HNode p, HNodeRendererContext ctx) {
        String lang=NStringUtils.trim(ObjEx.of(p.getPropertyValue(HPropName.LANG)).asStringOrName().orElse(""));
        String codeStr = (ObjEx.of(p.getPropertyValue(HPropName.VALUE)).asStringOrName().orElse(""));
        codeStr=specialTrimCode(codeStr);
        NTexts ttt = NTexts.of(ctx.session());
        NTextCode ncode = ttt.ofCode(lang, codeStr);
        return createRichTextHelper(lang, codeStr, ncode, p, ctx);
    }


}
