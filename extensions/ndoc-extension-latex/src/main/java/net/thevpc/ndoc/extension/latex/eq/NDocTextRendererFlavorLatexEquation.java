package net.thevpc.ndoc.extension.latex.eq;

import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.util.Colors;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.text.NDocRichTextToken;
import net.thevpc.ndoc.spi.renderer.text.NDocRichTextTokenType;
import net.thevpc.ndoc.spi.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.spi.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.spi.NDocTextRendererFlavor;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class NDocTextRendererFlavorLatexEquation implements NDocTextRendererFlavor {
    @Override
    public String type() {
        return "latex-equation";
    }

    @Override
    public void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        if (!text.isEmpty()) {
            NDocRichTextToken r = new NDocRichTextToken(
                    NDocRichTextTokenType.IMAGE_PAINTER,
                    text.toString()
            );
            double fontSize = NDocValueByName.getFontSize(p, ctx);
            r.imagePainter = this.createLatex(text, fontSize,options,p,ctx);
            NDocDouble2 size = r.imagePainter.size();
            r.bounds = new Rectangle2D.Double(0, 0, size.getX(), size.getX());
            builder.currRow().addToken(r);
        }
    }


    public NDocTextRendererBuilder.ImagePainter createLatex(String tex, double fontSize, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx) {
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
//        if (error) {
//            return null;
//        }
        Color foregroundColor=null;
        if(options.foregroundColorIndex!=null){
            foregroundColor=Colors.resolveDefaultColorByIndex(options.foregroundColorIndex,null);
        }else if(options.foregroundColor instanceof Color){
            foregroundColor=(Color) options.foregroundColor;
        }
        Color fg=HUtils.paintAsColor(HUtils.resolveForegroundColor(options));
        if(fg==null) {
            fg = HUtils.paintAsColor(NDocValueByName.getForegroundColor(p, ctx, true));
        }
        if(fg==null) {
            fg = Color.BLACK;
        }
        Color finalForegroundColor = fg;
        return new NDocTextRendererBuilder.ImagePainter() {
            @Override
            public void paint(NDocGraphics g, double x, double y) {
                Font plainFont = g.getFont().deriveFont(g.getFont().getSize() / 2f);
                g.setFont(plainFont);
                FontMetrics fontMetrics = g.getFontMetrics(plainFont);
                double xx = x;
                double yy = y;//+ascent-descent;
                icon.setForeground(finalForegroundColor);
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);
                //g.drawRect(xx, yy, icon.getIconWidth(), icon.getIconHeight());
            }

            public NDocDouble2 size() {
                return new NDocDouble2(icon.getIconWidth(), icon.getIconHeight());
            }
        };
    }
}
