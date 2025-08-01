package net.thevpc.ndoc.engine.renderer.text;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.Shadow;
import net.thevpc.ndoc.api.document.node.NDocNode;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererFlavor;
import net.thevpc.ndoc.api.renderer.text.*;
import net.thevpc.ndoc.engine.tools.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.text.NTextStyle;
import net.thevpc.nuts.text.NTextStyles;
import net.thevpc.nuts.text.NTexts;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class NDocTextRendererBuilderImpl implements NDocTextRendererBuilder {

    public String lang;
    public String code;
    public List<NDocRichTextRow> rows = new ArrayList<>();
    public Rectangle2D.Double bounds;
    private Paint defaultColor;
    private NDocEngine engine;

    public NDocTextRendererBuilderImpl(NDocEngine engine, Paint defaultColor) {
        this.defaultColor = defaultColor;
        this.engine = engine;
    }

    public void appendNText(String lang, String rawText, NText text, NDocNode node, NDocNodeRendererContext ctx) {
        NutsHighlighterMapper.highlightNutsText(lang, rawText, text, node, ctx, this);
    }

    @Override
    public void appendText(String rawText, NDocTextOptions options, NDocNode node, NDocNodeRendererContext ctx) {
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
    public void appendCustom(String lang, String rawText, NDocTextOptions options, NDocNode node, NDocNodeRendererContext ctx) {
        if (rawText == null || rawText.isEmpty()) {
            return;
        }
        NDocTextRendererFlavor hTextRendererFlavor = engine.textRendererFlavor(lang).orElse(null);
        if(hTextRendererFlavor==null){
            hTextRendererFlavor=engine.textRendererFlavor("").get();
        }
        hTextRendererFlavor.buildText(rawText, options, node, ctx, this);
    }

    public void appendEq(String text, NDocNode node, NDocNodeRendererContext ctx) {
        if (!text.isEmpty()) {
            NDocRichTextToken r = new NDocRichTextToken(
                    NDocRichTextTokenType.IMAGE_PAINTER,
                    text
            );
            double fontSize = NDocValueByName.getFontSize(node, ctx);
            r.imagePainter = this.createLatex(text, fontSize);
            NDocDouble2 size = r.imagePainter.size();
            r.bounds = new Rectangle2D.Double(0, 0, size.getX(), size.getX());
            this.currRow().addToken(r);
        }
    }

    public void appendPlain(String text, NDocNodeRendererContext ctx) {
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

        NDocGraphics g = ctx.graphics();
        for (int j = 0; j < a.size(); j++) {
            if (j > 0) {
                this.nextLine();
            }
            NDocRichTextToken c = new NDocRichTextToken(NDocRichTextTokenType.PLAIN, a.get(j));
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


    public NDocRichTextRow nextLine() {
        rows.add(new NDocRichTextRow());
        return rows.get(rows.size() - 1);
    }

    public NDocRichTextRow currRow() {
        if (rows.isEmpty()) {
            NDocRichTextRow e = new NDocRichTextRow();
            rows.add(e);
            return e;
        }
        return rows.get(rows.size() - 1);
    }

    public NDocBounds2 computeBound(NDocNodeRendererContext ctx) {
        NDocGraphics g = ctx.graphics();
        bounds = new Rectangle2D.Double(0, 0, 0, 0);
        double maxxY = 0;
        for (int i = 0; i < rows.size(); i++) {
            NDocRichTextRow row = rows.get(i);
            double minX = 0;
            double minY = 0;
            double maxX = 0;
            double maxY = 0;
            for (int j = 0; j < row.tokens.size(); j++) {
                NDocRichTextToken c = row.tokens.get(j);
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
        return new NDocBounds2(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), maxxY);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setCode(String rawText) {
        this.code = rawText;
    }

    public ImagePainter createLatex(String tex, double fontSize) {
        TeXFormula formula;
        boolean error = false;
        try {
            formula = new TeXFormula(tex);
        } catch (Exception ex) {
            error = true;
            formula = new TeXFormula("?error?");
            ex.printStackTrace();
        }
        float size = (float) (fontSize / 2.0);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

        // insert a border
        icon.setInsets(new Insets(0, 0, 0, 0));
        if (error) {
            return null;
        }
        return new ImagePainter() {
            @Override
            public void paint(NDocGraphics g, double x, double y) {
                Font plainFont = g.getFont().deriveFont(g.getFont().getSize() / 2f);
                g.setFont(plainFont);
                FontMetrics fontMetrics = g.getFontMetrics(plainFont);
                double xx = x;
                double yy = y;//+ascent-descent;
                icon.setForeground(g.getColor());
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);
                g.drawRect(xx, yy, icon.getIconWidth(), icon.getIconHeight());
            }

            public NDocDouble2 size() {
                return new NDocDouble2(icon.getIconWidth(), icon.getIconHeight());
            }
        };
    }

    @Override
    public void addToken(NDocRichTextToken col) {
        rows.get(rows.size() - 1).tokens.add(col);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public void render(NDocNode p, NDocNodeRendererContext ctx, NDocBounds2 bgBounds, NDocBounds2 selfBounds) {
        boolean debug = ctx.isDebug(p);
        double x = selfBounds.getX();
        double y = selfBounds.getY();
        NDocGraphics g = ctx.graphics();
        NDocTextOptions textOptions = new NDocTextOptions()
                .setFont(NDocValueByName.getFont(p, ctx))
                .setForegroundColor(NDocValueByName.getForegroundColor(p, ctx, true));


        NDocNodeRendererUtils.paintBackground(p, ctx, g, bgBounds);
        NOptional<Shadow> shadowOptional = NDocValueByName.readStyleAsShadow(p, NDocPropName.SHADOW, ctx);
        int ascent = g.getFontMetrics(textOptions.getFont()).getAscent();
        if (shadowOptional.isPresent()) {
            Shadow shadow = shadowOptional.get();
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
        for (NDocRichTextRow row : this.rows) {
            for (NDocRichTextToken col : row.tokens) {
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
                        NDocDouble2 b2 = col.imagePainter.size();
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
        NDocNodeRendererUtils.paintBorderLine(p, ctx, g, selfBounds);
    }
}
