package net.thevpc.ndoc.engine.renderer.text;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.text.*;
import net.thevpc.ndoc.api.util.Colors;
import net.thevpc.ndoc.engine.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
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

    public static void highlightNutsText(String lang, String rawText, NText parsedText, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder result) {
        Map<String, NDocTextPartStyle> cache = new HashMap<>();
        result.setLang(lang);
        result.setCode(rawText);
        NDocGraphics g = ctx.graphics();
        NDocNodeRendererUtils.applyFont(p, g, ctx);
        //String[] allLines = code.trim().split("[\n]");
        NTexts ttt = NTexts.of();
        NTextTransformConfig nTextTransformConfig = new NTextTransformConfig();
        nTextTransformConfig.setFlatten(true);
        nTextTransformConfig.setNormalize(true);
        nTextTransformConfig.setProcessTitleNumbers(true);
        List<NText> flatten = ttt.flatten(parsedText, nTextTransformConfig).toList();
        for (NText nText : flatten) {
            processNTextRecursively(nText, result, ctx, new NTextStyle[0],p, cache);
        }
        result.computeBound(ctx);
    }

    private static void applyOptions(NDocTextOptions to, NTextStyle nTextStyle, NDocNode p, NDocNodeRendererContext ctx, Map<String, NDocTextPartStyle> cache) {
        switch (nTextStyle.getType()) {
            case BOLD: {
                to.setBold(true);
                break;
            }
            case ITALIC: {
                to.setItalic(true);
                break;
            }
            case UNDERLINED: {
                to.underlined = true;
                break;
            }
            case STRIKED: {
                to.strikeThrough = true;
                break;
            }

            case FORE_TRUE_COLOR: {
                to.foregroundColor = new Color(nTextStyle.getVariant());
                break;
            }
            case BACK_TRUE_COLOR: {
                to.backgroundColor = new Color(nTextStyle.getVariant());
                break;
            }
            case FORE_COLOR: {
                to.foregroundColor = getCodeColorPalette(nTextStyle.getVariant());
                break;
            }
            case BACK_COLOR: {
                to.backgroundColor = getCodeColorPalette(nTextStyle.getVariant());
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
                NDocTextPartStyle ss = resolveCodeStyle(nTextStyle, p, ctx, cache);
                if (ss.foreground != null) {
                    to.foregroundColor = ss.foreground;
                }
                if (ss.background != null) {
                    to.backgroundColor = ss.background;
                }
                if (ss.bold) {
                    to.setBold(true);
                }
                if (ss.italic) {
                    to.setItalic(true);
                }
                if (ss.fontSize >= 1) {
                    to.setFontSize(ss.fontSize <= 0 ? null : (float) ss.fontSize);
                }
                to.setFontFamily(ss.fontFamily);
                break;
            }

        }
    }

    private static void processNTextRecursively(NText nText, NDocTextRendererBuilder result, NDocNodeRendererContext ctx, NTextStyle[] styles, NDocNode p, Map<String, NDocTextPartStyle> cache) {
        NDocGraphics g = ctx.graphics();
        if(styles==null){
            styles=new NTextStyle[0];
        }
        switch (nText.getType()) {
            case PLAIN: {
                if (result.isEmpty()) {
                    result.nextLine();
                }
                NTextPlain np = (NTextPlain) nText;
                if (np.getValue().equals("\n")) {
                    result.nextLine();
                } else {
                    if (styles.length==0) {
                        result.currRow();
                        NDocRichTextToken col = new NDocRichTextToken(NDocRichTextTokenType.PLAIN, np.getValue());
                        col.tok = nText;
                        //g.setFont(col.textOptions.font);
                        col.bounds = g.getStringBounds(col.text);
                        result.addToken(col);
                    } else {
                        result.currRow();
                        NDocRichTextToken col = new NDocRichTextToken(NDocRichTextTokenType.STYLED, np.getValue());
                        col.tok = nText;
                        //g.setFont(col.textOptions.font);
                        col.bounds = g.getStringBounds(col.text);
                        for (NTextStyle nTextStyle : styles) {
                            applyOptions(col.textOptions, nTextStyle, p, ctx, cache);
                        }
                        result.addToken(col);
                    }
                }
                break;
            }
            case STYLED:{
                NTextStyled ss=(NTextStyled)nText;
                List<NTextStyle> newStyles=new ArrayList<>(Arrays.asList(styles));
                newStyles.addAll(ss.getStyles().toList());
                processNTextRecursively(ss.getChild(), result, ctx,newStyles.toArray(new NTextStyle[0]), p,cache);
                break;
            }
            case LIST:{
                NTextList list = (NTextList) nText;
                for (NText nt : list.getChildren()) {
                    processNTextRecursively(nt, result, ctx,styles, p,cache);
                }
                break;
            }
            default:{
                throw new IllegalArgumentException("Unsupported text type: " + nText.getType());
            }
        }
    }

    private static NDocTextPartStyle resolveCodeStyle(NTextStyle nTextStyle, NDocNode node, NDocNodeRendererContext ctx, Map<String, NDocTextPartStyle> cache) {
        String styleTypeId = nTextStyle.getType().id();
        String prefix = "source-" + styleTypeId + "-";
        NDocTextPartStyle ss = cache.get(nTextStyle.id());
        if (ss != null) {
            return ss;
        }
        ss = new NDocTextPartStyle();
        {
            NDocValue e = NDocValue.of(ctx.computePropertyValue(node, prefix + "color").orNull());
            Color[] colors = e.asColorArrayOrColor().orNull();
            ss.foreground = Colors.resolveDefaultColorByIndex(nTextStyle.getVariant(), colors);
        }
        {
            NDocValue e = NDocValue.of(ctx.computePropertyValue(node, prefix + "background").orNull());
            Color[] colors = e.asColorArray().orNull();
            if (colors == null || colors.length == 0) {
                // od nothing
            } else {
                int i = nTextStyle.getVariant() % colors.length;
                ss.background = colors[i];
            }
        }
        {
            NDocValue e = NDocValue.of(
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
            NDocValue e = NDocValue.of(
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
            ss.bold = NDocValue.of(ctx.computePropertyValue(node, prefix + "font-bold")
                    .orElseUse(() -> ctx.computePropertyValue(node, "font-bold"))
                    .orNull()).asBoolean().orElse(false);

            ss.italic = NDocValue.of(ctx.computePropertyValue(node, prefix + "font-italic")
                    .orElseUse(() -> ctx.computePropertyValue(node, "font-italic"))
                    .orNull()).asBoolean().orElse(false);
        }
        cache.put(nTextStyle.id(), ss);
        return ss;
    }


    protected static List<NTextPlain> toNTextPlains(NText a) {
        if (a instanceof NTextStyled) {
            return toNTextPlains(((NTextStyled) a).getChild());
        }
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
