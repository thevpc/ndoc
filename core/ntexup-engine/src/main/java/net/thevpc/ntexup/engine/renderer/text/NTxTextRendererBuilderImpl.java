package net.thevpc.ntexup.engine.renderer.text;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.elem2d.NTxShadow;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;
import net.thevpc.ntexup.api.renderer.text.*;
import net.thevpc.ntexup.engine.util.NTxNodeRendererUtils;
import net.thevpc.ntexup.api.eval.NTxValueByName;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.text.NTextStyle;
import net.thevpc.nuts.text.NTextStyles;
import net.thevpc.nuts.text.NTexts;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class NTxTextRendererBuilderImpl implements NTxTextRendererBuilder {

    public String lang;
    public String code;
    public List<NTxRichTextRow> rows = new ArrayList<>();
    public Rectangle2D.Double bounds;
    private Paint defaultColor;
    private NTxEngine engine;

    public NTxTextRendererBuilderImpl(NTxEngine engine, Paint defaultColor) {
        this.defaultColor = defaultColor;
        this.engine = engine;
    }

    public void appendNText(String lang, String rawText, NText text, NTxNode node, NTxNodeRendererContext ctx) {
        NutsHighlighterMapper.highlightNutsText(lang, rawText, text, node, ctx, this);
    }

    @Override
    public void appendText(String rawText, NTxTextOptions options, NTxNode node, NTxNodeRendererContext ctx) {
        if (rawText == null || rawText.isEmpty()) {
            return;
        }
        if (options == null || !options.isStyled()) {
            appendPlain(rawText, ctx);
            return;
        }
        NTexts nTexts = NTexts.of();
        List<NTextStyle> styles = new ArrayList<>();
        if (options.bold != null && options.bold) {
            styles.add(NTextStyle.bold());
        }
        if (options.italic != null && options.italic) {
            styles.add(NTextStyle.italic());
        }
        if (options.underlined != null && options.underlined) {
            styles.add(NTextStyle.underlined());
        }
        if (options.strikeThrough != null && options.strikeThrough) {
            styles.add(NTextStyle.striked());
        }
        if (options.foregroundColor instanceof Color) {
            styles.add(NTextStyle.foregroundColor(((Color) options.foregroundColor).getRGB()));
        }
        if (options.foregroundColorIndex != null) {
            styles.add(NTextStyle.primary(options.foregroundColorIndex));
        }
        if (options.backgroundColor instanceof Color) {
            styles.add(NTextStyle.backgroundTrueColor(((Color) options.backgroundColor).getRGB()));
        }
        if (options.backgroundColorIndex != null) {
            styles.add(NTextStyle.primary(options.backgroundColorIndex));
        }
        NText nText = nTexts.ofStyled(rawText, NTextStyles.of(styles.toArray(new NTextStyle[0])));
        appendNText("", rawText, nText, node, ctx);
    }

    @Override
    public void appendCustom(String lang, String rawText, NTxTextOptions options, NTxNode node, NTxNodeRendererContext ctx) {
        if (rawText == null || rawText.isEmpty()) {
            return;
        }
        NTxTextRendererFlavor hTextRendererFlavor = engine.textRendererFlavor(lang).orElse(null);
        if(hTextRendererFlavor==null){
            hTextRendererFlavor=engine.textRendererFlavor("").get();
        }
        hTextRendererFlavor.buildText(rawText, options, node, ctx, this);
    }

    public void appendPlain(String text, NTxNodeRendererContext ctx) {
        while (text.startsWith("\n")) {
            this.nextLine();
            text = text.substring(1);
        }
        int end = 0;
        while (text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
            end++;
        }

        List<String> a = NStringUtils.split(text, "\n", false, false);

        NTxGraphics g = ctx.graphics();
        for (int j = 0; j < a.size(); j++) {
            if (j > 0) {
                this.nextLine();
            }
            NTxRichTextToken c = new NTxRichTextToken(NTxRichTextTokenType.PLAIN, a.get(j));
            g.setFont(c.textOptions.font);
            c.bounds = g.getStringBounds(c.text);
            this.currRow().addToken(
                    c
            );
        }
        for (int i = 0; i < end; i++) {
            this.nextLine();
        }
    }


    public NTxRichTextRow nextLine() {
        rows.add(new NTxRichTextRow());
        return rows.get(rows.size() - 1);
    }

    public NTxRichTextRow currRow() {
        if (rows.isEmpty()) {
            NTxRichTextRow e = new NTxRichTextRow();
            rows.add(e);
            return e;
        }
        return rows.get(rows.size() - 1);
    }

    public NTxBounds2 computeBound(NTxNodeRendererContext ctx) {
        NTxGraphics g = ctx.graphics();
        bounds = new Rectangle2D.Double(0, 0, 0, 0);
        double maxxY = 0;
        for (int i = 0; i < rows.size(); i++) {
            NTxRichTextRow row = rows.get(i);
            double minX = 0;
            double minY = 0;
            double maxX = 0;
            double maxY = 0;
            for (int j = 0; j < row.tokens.size(); j++) {
                NTxRichTextToken c = row.tokens.get(j);
                c.xOffset = maxX;
                g.setFont(c.textOptions.font);
//                if (c.attributedString == null) {
//                    c.bounds = g.getStringBounds(c.text);
//                } else {
//                    c.bounds = g.getStringBounds(c.text);
//                    //TextLayout textLayout=new TextLayout(          c.attributedString.getIterator(),g.getFontRenderContext());
//                    //c.textBounds=textLayout.getBounds();
//                    //c.textBounds = g.getStringBounds(c.attributedString.getIterator());
//                }
                maxX += c.bounds.getWidth();
                maxY = Math.max(maxY, c.bounds.getHeight());
            }
            row.textBounds = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
            if (i == 0) {
                row.yOffset = -row.textBounds.getMinY();
            } else {
                row.yOffset = rows.get(i - 1).yOffset + rows.get(i - 1).textBounds.getHeight();//+ textBounds[i].getMinY();
            }
            Rectangle2D.Double.union(bounds, row.textBounds, bounds);
            maxxY = row.yOffset + row.textBounds.getHeight();
        }
        return new NTxBounds2(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), maxxY);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setCode(String rawText) {
        this.code = rawText;
    }

    @Override
    public void addToken(NTxRichTextToken col) {
        rows.get(rows.size() - 1).tokens.add(col);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public void render(NTxNode p, NTxNodeRendererContext ctx, NTxBounds2 bgBounds, NTxBounds2 selfBounds) {
        boolean debug = ctx.isDebug(p);
        double x = selfBounds.getX();
        double y = selfBounds.getY();
        NTxGraphics g = ctx.graphics();
        NTxTextOptions textOptions = new NTxTextOptions()
                .setFont(NTxValueByName.getFont(p, ctx))
                .setForegroundColor(NTxValueByName.getForegroundColor(p, ctx, true));


        NTxNodeRendererUtils.paintBackground(p, ctx, g, bgBounds);
        NOptional<NTxShadow> shadowOptional = NTxValueByName.readStyleAsShadow(p, NTxPropName.SHADOW, ctx);
        int ascent = g.getFontMetrics(textOptions.getFont()).getAscent();
        if (shadowOptional.isPresent()) {
            NTxShadow shadow = shadowOptional.get();
            textOptions.setShadowColor(shadow.getColor());
            if (textOptions.getShadowColor() == null) {
                if (textOptions.getForegroundColor() instanceof Color) {
                    textOptions.setShadowColor(((Color) textOptions.getForegroundColor()).darker());
                } else {
                    textOptions.setShadowColor(textOptions.getForegroundColor());
                }
            }
            textOptions.setShadowTranslation(shadow.getTranslation());
        }
        Font oldFont= g.getFont();
        for (NTxRichTextRow row : this.rows) {
            for (NTxRichTextToken col : row.tokens) {
                switch (col.type) {
                    case PLAIN:
                    case STYLED: {
                        g.drawString(
                                col.text
                                , x + col.xOffset
                                , (y + row.yOffset) + ascent,
                                textOptions.copy().copyNonNullFrom(col.textOptions)
                        );
                        break;
                    }
                    case IMAGE_PAINTER: {
                        Rectangle2D b1 = col.bounds;
                        NTxDouble2 b2 = col.imagePainter.size();
                        col.imagePainter.paint(g, (x + col.xOffset), y + row.yOffset);
                        if (debug) {
                            g.drawRect(
                                    x + col.xOffset,
                                    y + row.yOffset,
                                    col.bounds.getWidth(),
                                    col.bounds.getHeight()
                            );
                        }
                        break;
                    }
                }
            }
        }
        NTxNodeRendererUtils.paintBorderLine(p, ctx, g, selfBounds);
    }
}
