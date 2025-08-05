package net.thevpc.ntexup.extension.latex.eq;

import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.*;
import net.thevpc.ntexup.api.renderer.text.*;
import net.thevpc.ntexup.api.util.NTxColors;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.util.NMsg;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

public class NTxTextRendererFlavorLatexEquation implements NTxTextRendererFlavor {
    @Override
    public String type() {
        return "latex-equation";
    }


    @Override
    public void buildText(String text, NTxTextOptions options, NTxNode p, NTxNodeRendererContext ctx, NTxTextRendererBuilder builder) {
        if (!text.isEmpty()) {
            NTxRichTextToken r = new NTxRichTextToken(
                    NTxRichTextTokenType.IMAGE_PAINTER,
                    text.toString()
            );
            double fontSize = ctx.getFontSize(p);
            r.imagePainter = this.createLatex(text, fontSize, options, p, ctx);
            NTxDouble2 size = r.imagePainter.size();
            r.bounds = new Rectangle2D.Double(0, 0, size.getX(), size.getX());
            builder.currRow().addToken(r);
        }
    }

    @Override
    public List<NTxTextToken> parseImmediate(NReservedSimpleCharQueue queue, NTxNodeRendererContext ctx) {
        String end;
        if (queue.peek(2).equals("\\(")) {
            end = "\\)";
            queue.read(2);
        } else if (queue.peek(5).equals("[[eq:")) {
            end = "]]";
            queue.read(5);
        } else {
            return null;
        }
        NExtendedLatexMathBuilder sb = new NExtendedLatexMathBuilder();
        while (queue.hasNext()) {
            String u = queue.peek(3);
            if (u.equals("\\" + end)) {
                sb.append(queue.read(3));
            } else if (queue.peek(2).equals(end)) {
                queue.read(2);
                sb.flush();
                return Arrays.asList(
                        new NTxTextTokenFlavored(type(), sb.toString().trim())
                );
            } else {
                char c = queue.read();
                sb.append(c);
            }
        }
        sb.flush();
        return Arrays.asList(
                new NTxTextTokenFlavored(type(), sb.toString().trim())
        );
    }


    public NTxTextRendererBuilder.ImagePainter createLatex(String tex, double fontSize, NTxTextOptions options, NTxNode p, NTxNodeRendererContext ctx) {
        TeXFormula formula;
        boolean error = false;
        try {
            formula = new TeXFormula(tex);
        } catch (Exception ex) {
            error = true;
            formula = new TeXFormula("?error?");
            ctx.engine().log().log(NMsg.ofC("error evaluating latex formula %s : %s", tex, ex), NTxUtils.sourceOf(p));
        }
        float size = (float) (fontSize / 2.0);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

        // insert a border
        icon.setInsets(new Insets(0, 0, 0, 0));
//        if (error) {
//            return null;
//        }
        Color foregroundColor = null;
        if (options.foregroundColorIndex != null) {
            foregroundColor = NTxColors.resolveDefaultColorByIndex(options.foregroundColorIndex, null);
        } else if (options.foregroundColor instanceof Color) {
            foregroundColor = (Color) options.foregroundColor;
        }
        Color fg = NTxUtils.paintAsColor(NTxUtils.resolveForegroundColor(options));
        if (fg == null) {
            fg = NTxUtils.paintAsColor(ctx.getForegroundColor(p, true));
        }
        if (fg == null) {
            fg = Color.BLACK;
        }
        Color finalForegroundColor = fg;
        return new NTxTextRendererBuilder.ImagePainter() {
            @Override
            public void paint(NTxGraphics g, double x, double y) {
                Font plainFont = g.getFont().deriveFont(g.getFont().getSize() / 2f);
                g.setFont(plainFont);
                FontMetrics fontMetrics = g.getFontMetrics(plainFont);
                double xx = x;
                double yy = y;//+ascent-descent;
                icon.setForeground(finalForegroundColor);
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);
                //g.drawRect(xx, yy, icon.getIconWidth(), icon.getIconHeight());
            }

            public NTxDouble2 size() {
                return new NTxDouble2(icon.getIconWidth(), icon.getIconHeight());
            }
        };
    }

}
