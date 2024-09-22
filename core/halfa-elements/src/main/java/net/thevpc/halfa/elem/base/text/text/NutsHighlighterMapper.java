package net.thevpc.halfa.elem.base.text.text;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.util.Colors;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.text.*;
import net.thevpc.nuts.text.*;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class NutsHighlighterMapper {
    private static Color[] DEFAULT_CODE_PALETTE = new Color[]{
            new Color(0x26355D),
            new Color(0xAF47D2),
            new Color(0xDE3163),
            new Color(0x2ECC71),
            new Color(0xFF8F00),
            new Color(0xFF7F50),
            new Color(0xFFBF00),
            new Color(0xFFDB00),
            new Color(0x9FE2BF),
            new Color(0x40E0D0),
            new Color(0xCCCCFF),
            new Color(0x6495ED)
    };

    public static void highlightNutsText(String lang, String rawText, NText parsedText, HNode p, HNodeRendererContext ctx, HTextRendererBuilder result) {
        Map<String, TextPartStyle> cache = new HashMap<>();
        result.setLang(lang);
        result.setCode(rawText);
        HGraphics g = ctx.graphics();
        HNodeRendererUtils.applyFont(p, g, ctx);
        //String[] allLines = code.trim().split("[\n]");
        NTexts ttt = NTexts.of(ctx.session());
        NTextTransformConfig nTextTransformConfig = new NTextTransformConfig();
        nTextTransformConfig.setFlatten(true);
        nTextTransformConfig.setNormalize(true);
        nTextTransformConfig.setProcessTitleNumbers(true);
        List<NText> flatten = ttt.flatten(parsedText, nTextTransformConfig).toList();
        for (NText nText : flatten) {
            switch (nText.getType()) {
                case PLAIN: {
                    NTextPlain np = (NTextPlain) nText;
                    if (np.getText().equals("\n")) {
                        result.nextLine();
                    } else {
                        result.currRow();
                        HRichTextToken col = new HRichTextToken(HRichTextTokenType.PLAIN, np.getText());
                        col.tok = nText;
                        g.setFont(col.textOptions.font);
                        col.bounds = g.getStringBounds(col.text);
                        result.addToken(col);
                    }
                    break;
                }

                case STYLED: {
                    NTextStyled s = (NTextStyled) nText;
                    if (result.isEmpty()) {
                        result.nextLine();
                    }
                    for (NTextPlain t : toNTextPlains(s.getChild())) {
                        if (t.getText().equals("\n")) {
                            result.nextLine();
                            continue;
                        }
                        HRichTextToken col = new HRichTextToken(HRichTextTokenType.STYLED, t.getText());
                        col.tok = nText;
//                        col.attributedString = new AttributedString(col.text);
//                        col.attributedShadowString = new AttributedString(col.text);
                        g.setFont(col.textOptions.font);
                        col.bounds = g.getStringBounds(col.text);
                        result.addToken(col);
                        // Set attributes
                        NTextStyles styles = s.getStyles();
//                        int fontStyle = 0;
                        int strLen = t.getText().length();
//                        String fontFamily = null;
//                        double fontSize = 0;
                        for (NTextStyle nTextStyle : styles.toArray()) {
                            switch (nTextStyle.getType()) {
                                case BOLD: {
                                    col.textOptions.setBold(true);
                                    break;
                                }
                                case ITALIC: {
                                    col.textOptions.setItalic(true);
                                    break;
                                }
                                case UNDERLINED: {
                                    col.textOptions.underlined =true;
                                    break;
                                }
                                case STRIKED: {
                                    col.textOptions.strikeThrough=true;
                                    break;
                                }

                                case FORE_TRUE_COLOR: {
                                    col.textOptions.foregroundColor =new Color(nTextStyle.getVariant());
                                    break;
                                }
                                case BACK_TRUE_COLOR: {
                                    col.textOptions.backgroundColor =new Color(nTextStyle.getVariant());
                                    break;
                                }
                                case FORE_COLOR: {
                                    col.textOptions.foregroundColor =getCodeColorPalette(nTextStyle.getVariant());
                                    break;
                                }
                                case BACK_COLOR: {
                                    col.textOptions.backgroundColor =getCodeColorPalette(nTextStyle.getVariant());
                                    break;
                                }
                                case PLAIN: {
                                    break;
                                }
                                case BLINK: {
                                    //should we add timer??
                                    break;
                                }
                                case BOOLEAN:
                                case COMMENTS:
                                case CONFIG:
                                case DANGER:
                                case DATE:
                                case ERROR:
                                case FAIL:
                                case INFO:
                                case INPUT:
                                case KEYWORD:
                                case NUMBER:
                                case OPERATOR:
                                case OPTION:
                                case PALE:
                                case PATH:
                                case PRIMARY:
                                case REVERSED:
                                case SECONDARY:
                                case SEPARATOR:
                                case STRING:
                                case SUCCESS:
                                case TITLE:
                                case VAR:
                                case VERSION:
                                case WARN: {
                                    TextPartStyle ss = resolveCodeStyle(nTextStyle, p, ctx, cache);
                                    if (ss.foreground != null) {
                                        col.textOptions.foregroundColor =ss.foreground;
                                    }
                                    if (ss.background != null) {
                                        col.textOptions.backgroundColor =ss.background;
                                    }
                                    if (ss.bold) {
                                        col.textOptions.setBold(true);
                                    }
                                    if (ss.italic) {
                                        col.textOptions.setItalic(true);
                                    }
                                    if (ss.fontSize >= 1) {
                                        col.textOptions.setFontSize(ss.fontSize<=0?null:(float)ss.fontSize);
                                    }
                                    col.textOptions.setFontFamily(ss.fontFamily);
                                    break;
                                }

                            }
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("unexpected text type " + nText.getType());
            }
        }
        result.computeBound(ctx);
    }

    private static TextPartStyle resolveCodeStyle(NTextStyle nTextStyle, HNode node, HNodeRendererContext ctx, Map<String, TextPartStyle> cache) {
        String styleTypeId = nTextStyle.getType().id();
        String prefix = "source-" + styleTypeId + "-";
        TextPartStyle ss = cache.get(nTextStyle.id());
        if (ss != null) {
            return ss;
        }
        ss = new TextPartStyle();
        {
            ObjEx e = ObjEx.of(ctx.computePropertyValue(node, prefix + "color").orNull());
            Color[] colors = e.asColorArrayOrColor().orNull();
            ss.foreground = Colors.resolveDefaultColorByIndex(nTextStyle.getVariant(),colors);
        }
        {
            ObjEx e = ObjEx.of(ctx.computePropertyValue(node, prefix + "background").orNull());
            Color[] colors = e.asColorArray().orNull();
            if (colors == null || colors.length == 0) {
                // od nothing
            } else {
                int i = nTextStyle.getVariant() % colors.length;
                ss.background = colors[i];
            }
        }
        {
            ObjEx e = ObjEx.of(
                    ctx.computePropertyValue(node, prefix + "font-family")
                            .orElseUse(() -> ctx.computePropertyValue(node, "font-family"))
                            .orNull()
            );
            String value = NStringUtils.trimToNull(e.asStringOrName().orNull());
            if (value == null) {
                // od nothing
            } else {
                ss.fontFamily = value;
            }
        }
        {
            ObjEx e = ObjEx.of(
                    ctx.computePropertyValue(node, prefix + "font-family-size")
                            .orElseUse(() -> ctx.computePropertyValue(node, "font-family-size"))
                            .orNull()
            );
            Double value = e.asDouble().orNull();
            if (value == null) {
                // od nothing
            } else {
                ss.fontSize = value;
            }
        }
        {
            ss.bold = ObjEx.of(ctx.computePropertyValue(node, prefix + "font-bold")
                    .orElseUse(() -> ctx.computePropertyValue(node, "font-bold"))
                    .orNull()).asBoolean().orElse(false);

            ss.italic = ObjEx.of(ctx.computePropertyValue(node, prefix + "font-italic")
                    .orElseUse(() -> ctx.computePropertyValue(node, "font-italic"))
                    .orNull()).asBoolean().orElse(false);
        }
        cache.put(nTextStyle.id(), ss);
        return ss;
    }


    protected static List<NTextPlain> toNTextPlains(NText a) {
        if (a instanceof NTextPlain) {
            return Arrays.asList((NTextPlain) a);
        }
        if (a instanceof NTextList) {
            ArrayList<NTextPlain> objects = new ArrayList<>();
            NTextList list = (NTextList) a;
            for (NText nText : list) {
                objects.addAll(toNTextPlains(nText));
            }
            return objects;
        }
        return new ArrayList<>();
    }


    private static Color getCodeColorPalette(int i) {
        i = i % DEFAULT_CODE_PALETTE.length;
        return DEFAULT_CODE_PALETTE[i];
    }

}
