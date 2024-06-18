package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.engine.renderer.screen.common.HNodeRendererUtils;
import net.thevpc.halfa.engine.renderer.screen.renderers.text.util.*;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.nodes.HPropValueByNameParser;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.text.*;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class HTextBaseRenderer extends AbstractHNodeRenderer {
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

    HProperties defaultStyles = new HProperties();

    public HTextBaseRenderer(String... types) {
        super(types);
    }

    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        Bounds2 s = selfBounds(p, ctx);
        Bounds2 bb = ctx.getBounds();
        return new HSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public Bounds2 bgBounds(HNode p, HNodeRendererContext ctx) {
        return HPropValueByNameParser.selfBounds(p, null, null, ctx);
    }

    public Bounds2 selfBounds(HNode p, HNodeRendererContext ctx) {
        HRichTextHelper helper = createRichTextHelper(p, ctx);
        Bounds2 bounds2 = helper.computeBound(ctx);
        return HPropValueByNameParser.selfBounds(p, new Double2(bounds2.getWidth(),bounds2.getHeight()), null, ctx);
    }

    protected abstract HRichTextHelper createRichTextHelper(HNode p, HNodeRendererContext ctx);

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        HGraphics g = ctx.graphics();
        HNodeRendererUtils.applyFont(p, g, ctx);


        HRichTextHelper helper = createRichTextHelper(p, ctx);

        Bounds2 bgBounds0 = bgBounds(p, ctx);
        Bounds2 bgBounds = bgBounds0;
        Bounds2 selfBounds = selfBounds(p, ctx);
        bgBounds = bgBounds.expand(selfBounds);

        HNodeRendererContext finalCtx = ctx;
        if (HPropValueByNameParser.getDebugLevel(p, ctx) >= 10) {
            g.debugString(
                    "Plain:\n"
                            + "expected=" + bgBounds0 + "\n"
                            + "fullSize=" + selfBounds + "\n"
                            + "newExpectedBounds=" + bgBounds + "\n"
                            + "curr: "
                            + Arrays.asList(
                                    HPropName.SIZE,
                                    HPropName.ORIGIN,
                                    HPropName.POSITION
                            )
                            .stream().map(x
                                    -> p.getProperty(x).orNull()
                            ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n"
                            + "eff: "
                            + Arrays.asList(
                                    HPropName.SIZE,
                                    HPropName.ORIGIN,
                                    HPropName.POSITION
                            )
                            .stream().map(x
                                            -> {
                                        Object n = finalCtx.computePropertyValue(p, x).orNull();
                                        if (n == null) {
                                            return n;
                                        }
                                        return new HProp(x, n);
                                    }
                            ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n",
                    30, 100
            );
        }

        helper.computeBound(ctx);
        helper.render(p, ctx, bgBounds, this, selfBounds);
    }

    protected String specialTrimCode(String code) {
        List<String> rows = new ArrayList<>();
        for (String string : code.split("[\n\r]")) {
            if (rows.isEmpty()) {
                if (string.trim().isEmpty()) {
                    //just ignore
                } else {
                    rows.add(string);
                }
            } else {
                rows.add(string);
            }
        }
        int startingSpaces = -1;
        for (int i = rows.size() - 1; i >= 0; i--) {
            String r = rows.get(i);
            if (r.trim().isEmpty()) {
                rows.remove(i);
            } else {
                int s = computeStartingSpaces(r);
                if (startingSpaces < 0 || startingSpaces > s) {
                    startingSpaces = s;
                }
                break;
            }
        }
        if (startingSpaces > 0) {
            for (int i = 0; i < rows.size(); i++) {
                String r = rows.get(i);
                rows.set(i, r.substring(startingSpaces));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows.size(); i++) {
            String r = rows.get(i);
            sb.append(NStringUtils.trimRight(r));
            if (i + 1 < rows.size()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private int computeStartingSpaces(String code) {
        int x = 0;
        for (int i = 0; i < code.length(); i++) {
            char s = code.charAt(i);
            if (s == ' ') {
                x++;
            } else {
                break;
            }
        }
        return x;
    }


    protected List<NTextPlain> toNTextPlains(NText a) {
        if(a instanceof NTextPlain){
            return Arrays.asList((NTextPlain) a);
        }
        if(a instanceof NTextList){
            ArrayList<NTextPlain> objects = new ArrayList<>();
            NTextList list=(NTextList) a;
            for (NText nText : list) {
                objects.addAll(toNTextPlains(nText));
            }
            return objects;
        }
        return new ArrayList<>();
    }
    protected HRichTextHelper createRichTextHelper(String lang, String rawText, NText parsedText, HNode p, HNodeRendererContext ctx) {
        HRichTextHelper result = new HRichTextHelper();
        Map<String, TextPartStyle> cache = new HashMap<>();
        result.lang = lang;
        result.code = rawText;
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
                        result.rows.get(result.rows.size() - 1).tokens.add(col);
                    }
                    break;
                }

                case STYLED: {
                    NTextStyled s = (NTextStyled) nText;
                    if (result.rows.isEmpty()) {
                        HRichTextRow r = new HRichTextRow();
                        result.rows.add(r);
                    }
                    for (NTextPlain t : toNTextPlains(s.getChild())) {
                        if(t.getText().equals("\n")){
                            result.nextLine();
                            continue;
                        }
                        HRichTextToken col = new HRichTextToken(HRichTextTokenType.STYLED, t.getText());
                        col.tok = nText;
                        col.attributedString = new AttributedString(col.text);
                        col.attributedShadowString = new AttributedString(col.text);
                        result.rows.get(result.rows.size() - 1).tokens.add(col);
                        // Set attributes
                        NTextStyles styles = s.getStyles();
                        int fontStyle = 0;
                        int strLen = t.getText().length();
                        String fontFamily = null;
                        double fontSize = 0;
                        for (NTextStyle nTextStyle : styles.toArray()) {
                            switch (nTextStyle.getType()) {
                                case BOLD: {
                                    fontStyle |= Font.BOLD;
                                    break;
                                }
                                case ITALIC: {
                                    fontStyle |= Font.ITALIC;
                                    break;
                                }
                                case UNDERLINED: {
                                    col.attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, strLen);
                                    col.attributedShadowString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, strLen);
                                    break;
                                }

                                case FORE_TRUE_COLOR: {
                                    col.attributedString.addAttribute(TextAttribute.FOREGROUND, new Color(nTextStyle.getVariant()), 0, strLen);
                                    break;
                                }
                                case BACK_TRUE_COLOR: {
                                    col.attributedString.addAttribute(TextAttribute.BACKGROUND, new Color(nTextStyle.getVariant()), 0, strLen);
                                    break;
                                }
                                case FORE_COLOR: {
                                    col.attributedString.addAttribute(TextAttribute.FOREGROUND, getCodeColorPalette(nTextStyle.getVariant()), 0, strLen);
                                    break;
                                }
                                case BACK_COLOR: {
                                    col.attributedString.addAttribute(TextAttribute.FOREGROUND, getCodeColorPalette(nTextStyle.getVariant()), 0, strLen);
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
                                case STRIKED:
                                case STRING:
                                case SUCCESS:
                                case TITLE:
                                case VAR:
                                case VERSION:
                                case WARN: {
                                    TextPartStyle ss = resolveCodeStyle(nTextStyle, p, ctx, cache);
                                    if (ss.foreground != null) {
                                        col.attributedString.addAttribute(TextAttribute.FOREGROUND, ss.foreground, 0, strLen);
                                    }
                                    if (ss.background != null) {
                                        col.attributedString.addAttribute(TextAttribute.BACKGROUND, ss.background, 0, strLen);
                                    }
                                    if (ss.bold) {
                                        fontStyle |= Font.BOLD;
                                    }
                                    if (ss.italic) {
                                        fontStyle |= Font.ITALIC;
                                    }
                                    if (ss.fontSize >= 1) {
                                        fontSize = ss.fontSize;
                                    }
                                    fontFamily = ss.fontFamily;
                                    break;
                                }

                            }
                        }
                        Font baseFont = g.getFont();
                        double baseFontSize = baseFont.getSize();
                        if (fontSize < 1) {
                            fontSize = baseFontSize;
                        }
                        if (fontFamily != null) {
                            baseFont = new Font(fontFamily, Font.PLAIN, (int) fontSize);
                        } else if (fontSize != baseFontSize) {
                            baseFont = baseFont.deriveFont((float) baseFontSize);
                        }
                        col.font = baseFont.deriveFont(fontStyle);
                        col.attributedString.addAttribute(TextAttribute.FONT, col.font);
                        col.attributedShadowString.addAttribute(TextAttribute.FONT, col.font);
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("unexpected text type " + nText.getType());
            }
        }
        result.computeBound(ctx);
        return result;
    }

    private TextPartStyle resolveCodeStyle(NTextStyle nTextStyle, HNode node, HNodeRendererContext ctx, Map<String, TextPartStyle> cache) {
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
            if (colors == null || colors.length == 0) {
                colors = DEFAULT_CODE_PALETTE;
            }
            int i = nTextStyle.getVariant() % colors.length;
            ss.foreground = colors[i];
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

    private Color getCodeColorPalette(int i) {
        i = i % DEFAULT_CODE_PALETTE.length;
        return DEFAULT_CODE_PALETTE[i];
    }

    protected void fillPlain(String text, HRichTextHelper richTextHelper, HNode p, HNodeRendererContext ctx) {
        String[] a = text.split("\n");
        for (int j = 0; j < a.length; j++) {
            if (j > 0) {
                richTextHelper.nextLine();
            }
            richTextHelper.currRow().addToken(
                    new HRichTextToken(HRichTextTokenType.PLAIN, a[j])
            );
        }
    }

    protected void fillEq(String text, HRichTextHelper richTextHelper, HNode p, HNodeRendererContext ctx) {
        if (!text.isEmpty()) {
            HRichTextToken r = new HRichTextToken(
                    HRichTextTokenType.IMAGE_PAINTER,
                    text.toString()
            );
            double fontSize = HPropValueByNameParser.getFontSize(p, ctx);
            r.imagePainter = richTextHelper.createLatex(text, fontSize);
            richTextHelper.currRow().addToken(r);
        }
    }


}
