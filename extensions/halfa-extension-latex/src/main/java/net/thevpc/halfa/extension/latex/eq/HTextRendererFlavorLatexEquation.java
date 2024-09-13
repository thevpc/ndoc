package net.thevpc.halfa.extension.latex.eq;

import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.text.HRichTextToken;
import net.thevpc.halfa.spi.renderer.text.HRichTextTokenType;
import net.thevpc.halfa.spi.renderer.text.HTextRendererBuilder;
import net.thevpc.halfa.spi.HTextRendererFlavor;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HTextRendererFlavorLatexEquation implements HTextRendererFlavor {
    @Override
    public String type() {
        return "latex-equation";
    }

    @Override
    public void buildText(String text, HNode p, HNodeRendererContext ctx, HTextRendererBuilder builder) {
        if (!text.isEmpty()) {
            HRichTextToken r = new HRichTextToken(
                    HRichTextTokenType.IMAGE_PAINTER,
                    text.toString()
            );
            double fontSize = HValueByName.getFontSize(p, ctx);
            r.imagePainter = this.createLatex(text, fontSize);
            Double2 size = r.imagePainter.size();
            r.bounds = new Rectangle2D.Double(0, 0, size.getX(), size.getX());
            builder.currRow().addToken(r);
        }
    }


    public HTextRendererBuilder.ImagePainter createLatex(String tex, double fontSize) {
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
        return new HTextRendererBuilder.ImagePainter() {
            @Override
            public void paint(HGraphics g, double x, double y) {
                Font plainFont = g.getFont().deriveFont(g.getFont().getSize() / 2f);
                g.setFont(plainFont);
                FontMetrics fontMetrics = g.getFontMetrics(plainFont);
                double xx = x;
                double yy = y;//+ascent-descent;
                icon.setForeground(g.getColor());
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);
                //g.drawRect(xx, yy, icon.getIconWidth(), icon.getIconHeight());
            }

            public Double2 size() {
                return new Double2(icon.getIconWidth(), icon.getIconHeight());
            }
        };
    }
}
