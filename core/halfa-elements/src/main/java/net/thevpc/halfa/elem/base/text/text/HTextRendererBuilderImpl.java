package net.thevpc.halfa.elem.base.text.text;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem2d.Shadow;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.HTextRendererFlavor;
import net.thevpc.halfa.spi.renderer.text.*;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTextRendererBuilderImpl implements HTextRendererBuilder {

    public String lang;
    public String code;
    public List<HRichTextRow> rows = new ArrayList<>();
    public Rectangle2D.Double bounds;
    private Map<String, HTextRendererFlavor> flavors;
    private Paint defaultColor;

    public HTextRendererBuilderImpl(Map<String, HTextRendererFlavor> flavors,Paint defaultColor) {
        this.flavors = flavors;
        this.defaultColor = defaultColor;
    }

    public HTextRendererBuilderImpl(Paint defaultColor) {
        this.flavors = new HashMap<>();
        this.defaultColor=defaultColor;
    }

    public void appendNText(String lang, String rawText, NText text, HNode node, HNodeRendererContext ctx) {
        NutsHighlighterMapper.highlightNutsText(lang, rawText, text, node, ctx, this);
    }

    @Override
    public void appendCustom(String lang, String rawText, HNode node, HNodeRendererContext ctx) {
        HTextRendererFlavor hTextRendererFlavor = flavors.get(lang);
        if (hTextRendererFlavor == null) {
            throw new IllegalArgumentException("unsupported flavor for language " + lang);
        }
        hTextRendererFlavor.buildText(rawText, node, ctx, this);
    }

    public void appendEq(String text, HNode node, HNodeRendererContext ctx) {
        if (!text.isEmpty()) {
            HRichTextToken r = new HRichTextToken(
                    HRichTextTokenType.IMAGE_PAINTER,
                    text.toString()
            );
            double fontSize = HValueByName.getFontSize(node, ctx);
            r.imagePainter = this.createLatex(text, fontSize);
            Double2 size = r.imagePainter.size();
            r.bounds = new Rectangle2D.Double(0, 0, size.getX(), size.getX());
            this.currRow().addToken(r);
        }
    }

    public void appendPlain(String text, HNodeRendererContext ctx) {
        while (text.startsWith("\n")) {
            this.nextLine();
            text = text.substring(1);
        }
        int end = 0;
        while (text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
            end++;
        }

        List<String> a = NStringUtils.split(text, "\n",false,true);

        HGraphics g = ctx.graphics();
        for (int j = 0; j < a.size(); j++) {
            if (j > 0) {
                this.nextLine();
            }
            HRichTextToken c = new HRichTextToken(HRichTextTokenType.PLAIN, a.get(j));
            g.setFont(c.font);
            c.bounds = g.getStringBounds(c.text);
            this.currRow().addToken(
                    c
            );
        }
        for (int i = 0; i < end; i++) {
            this.nextLine();
        }
    }


    public HRichTextRow nextLine() {
        rows.add(new HRichTextRow());
        return rows.get(rows.size() - 1);
    }

    public HRichTextRow currRow() {
        if (rows.isEmpty()) {
            HRichTextRow e = new HRichTextRow();
            rows.add(e);
            return e;
        }
        return rows.get(rows.size() - 1);
    }

    public Bounds2 computeBound(HNodeRendererContext ctx) {
        HGraphics g = ctx.graphics();
        bounds = new Rectangle2D.Double(0, 0, 0, 0);
        double maxxY = 0;
        for (int i = 0; i < rows.size(); i++) {
            HRichTextRow row = rows.get(i);
            double minX = 0;
            double minY = 0;
            double maxX = 0;
            double maxY = 0;
            for (int j = 0; j < row.tokens.size(); j++) {
                HRichTextToken c = row.tokens.get(j);
                c.xOffset = maxX;
                g.setFont(c.font);
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
        return new Bounds2(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), maxxY);
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
            public void paint(HGraphics g, double x, double y) {
                Font plainFont = g.getFont().deriveFont(g.getFont().getSize() / 2f);
                g.setFont(plainFont);
                FontMetrics fontMetrics = g.getFontMetrics(plainFont);
                double xx = x;
                double yy = y;//+ascent-descent;
                icon.setForeground(g.getColor());
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);
                g.drawRect(xx, yy, icon.getIconWidth(), icon.getIconHeight());
            }

            public Double2 size() {
                return new Double2(icon.getIconWidth(), icon.getIconHeight());
            }
        };
    }

    @Override
    public void addToken(HRichTextToken col) {
        rows.get(rows.size() - 1).tokens.add(col);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public void render(HNode p, HNodeRendererContext ctx, Bounds2 bgBounds, Bounds2 selfBounds) {
        boolean debug = HValueByName.isDebug(p, ctx);
        double x = selfBounds.getX();
        double y = selfBounds.getY();
        HGraphics g = ctx.graphics();
        Font plainFont = HValueByName.getFont(p, ctx);
        HNodeRendererUtils.paintBackground(p, ctx, g, bgBounds);
        Paint foreground = HValueByName.getForegroundColor(p, ctx, true);
        NOptional<Shadow> shadowOptional = HValueByName.readStyleAsShadow(p, HPropName.SHADOW, ctx);
        if (shadowOptional.isPresent()) {
            Shadow shadow = shadowOptional.get();
            HPoint2D translation = shadow.getTranslation();
            if (translation == null) {
                translation = new HPoint2D(0, 0);
            }
            Paint shadowColor = shadow.getColor();
            if (shadowColor == null) {
                if (foreground instanceof Color) {
                    shadowColor = ((Color) foreground).darker();
                } else {
                    shadowColor = foreground;
                }
            }
            for (HRichTextRow row : this.rows) {
                for (HRichTextToken col : row.tokens) {
                    g.setPaint(foreground);
                    int ascent = g.getFontMetrics(plainFont).getAscent();
                    switch (col.type) {
                        case PLAIN: {
                            if (shadowColor != null) {
                                g.setPaint(shadowColor);
                            }
                            if(defaultColor!=null) {
                                g.setPaint(defaultColor);
                            }
                            g.drawString(col.text
                                    , x + col.xOffset + translation.getX()
                                    , (y + row.yOffset) + ascent + translation.getY()
                            );
                            break;
                        }
                        case STYLED: {
                            col.attributedShadowString.addAttribute(TextAttribute.FOREGROUND, shadowColor);
                            g.drawString(col.attributedShadowString.getIterator()
                                    , x + col.xOffset + translation.getX()
                                    , (y + row.yOffset) + ascent + translation.getY()
                            );
                            break;
                        }
                        default:
                            throw new AssertionError();
                    }
                }
            }
        }
        for (HRichTextRow row : this.rows) {
            for (HRichTextToken col : row.tokens) {
                g.setPaint(foreground);
                g.setFont(plainFont);
                switch (col.type) {
                    case PLAIN: {
                        int ascent = g.getFontMetrics(plainFont).getAscent();
                        g.drawString(col.text, x + col.xOffset
                                , (y + row.yOffset) + ascent
                        );
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
                    case STYLED: {
                        int ascent = g.getFontMetrics(col.font).getAscent();
                        g.drawString(col.attributedString.getIterator(), x + col.xOffset
                                , (y + row.yOffset) + ascent
                        );
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
                    case IMAGE_PAINTER: {
                        Rectangle2D b1 = col.bounds;
                        Double2 b2 = col.imagePainter.size();
                        col.imagePainter.paint(g, (x + col.xOffset), y + row.yOffset);
                        if (debug) {
                            g.drawRect(
                                    x + col.xOffset,
                                    y + row.yOffset,
                                    col.bounds.getWidth(),
                                    col.bounds.getHeight()
                            );
                        }
                    }
                }
            }
        }
        HNodeRendererUtils.paintBorderLine(p, ctx, g, selfBounds);
    }
}
