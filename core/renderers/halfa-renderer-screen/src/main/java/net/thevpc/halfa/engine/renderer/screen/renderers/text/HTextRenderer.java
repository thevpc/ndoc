package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.engine.renderer.screen.renderers.text.util.HRichTextHelper;
import net.thevpc.halfa.engine.renderer.screen.renderers.text.util.HRichTextToken;
import net.thevpc.halfa.engine.renderer.screen.renderers.text.util.HRichTextTokenType;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.text.NTextCode;
import net.thevpc.nuts.text.NTexts;
import net.thevpc.nuts.util.NStringUtils;

public class HTextRenderer extends HTextBaseRenderer {

    public HTextRenderer() {
        super(HNodeType.TEXT);
    }

    public HRichTextHelper createRichTextHelper(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        List<HNode> all=new ArrayList<>();
        String lang = NStringUtils.trim(ObjEx.of(p.getPropertyValue(HPropName.LANG).orNull()).asString().orElse(""));
        String text = ObjEx.of(p.getPropertyValue(HPropName.VALUE).orNull()).asString().orElse("");
        switch (lang) {
            case "":
            case "default":
                return createRichTextHelperDefault(text, p, ctx);
            case "ntf":
                return createRichTextHelperNTF(text, p, ctx);
//            case "markdown":
//                return createRichTextHelperMarkdown(text, p, ctx);
            default:
                return createRichTextHelperDefault(text, p, ctx);
        }

    }


    public HRichTextHelper createRichTextHelperNTF(String text, HNode p, HNodeRendererContext ctx) {
        String lang = "ntf";
        String codeStr = text;
        codeStr = specialTrimCode(codeStr);
        NTexts ttt = NTexts.of(ctx.session());
        NText ncode = ttt.parse(codeStr);
        return createRichTextHelper(lang, codeStr, ncode, p, ctx);
    }

    public HRichTextHelper createRichTextHelperDefault(String text, HNode p, HNodeRendererContext ctx) {
        String mode = "plain";
        StringBuilder sb = new StringBuilder();
        char[] charArray = specialTrimCode(text).toCharArray();
        HDocumentFactory f = ctx.documentFactory();
        HRichTextHelper richTextHelper = new HRichTextHelper();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (mode) {
                case "plain": {
                    switch (c) {
                        case '$': {
                            if (i + 1 < charArray.length && charArray[i + 1] == '$') {
                                sb.append(c);
                                i++;
                            } else {
                                if (sb.length() > 0) {
                                    fillPlain(sb.toString(), richTextHelper, p, ctx);
                                    sb.setLength(0);
                                }
                                mode = "eq";
                            }
                            break;
                        }
                        default: {
                            sb.append(c);
                        }
                    }
                    break;
                }
                case "eq": {
                    switch (c) {
                        case '\\': {
                            sb.append(c);
                            if (i + 1 < charArray.length) {
                                sb.append(charArray[i + 1]);
                                i++;
                            }
                            break;
                        }
                        case '$': {
                            if (sb.length() > 0) {
                                fillEq(sb.toString(), richTextHelper, p, ctx);
                                sb.setLength(0);

                            }
                            mode = "plain";
                            break;
                        }
                        default: {
                            sb.append(c);
                        }
                    }
                    break;
                }
            }
        }

        if (sb.length() > 0) {
            switch (mode) {
                case "plain": {
                    fillPlain(sb.toString(), richTextHelper, p, ctx);
                    sb.setLength(0);
                    break;
                }
                case "eq": {
                    fillEq(sb.toString(), richTextHelper, p, ctx);
                    sb.setLength(0);
                    break;
                }
            }
            sb.setLength(0);
        }
        return richTextHelper;
    }

}
