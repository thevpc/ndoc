package net.thevpc.ndoc.extension.latex.eq;

import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.renderer.text.*;
import net.thevpc.ndoc.api.util.Colors;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.util.NMsg;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

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
            double fontSize = ctx.getFontSize(p);
            r.imagePainter = this.createLatex(text, fontSize, options, p, ctx);
            NDocDouble2 size = r.imagePainter.size();
            r.bounds = new Rectangle2D.Double(0, 0, size.getX(), size.getX());
            builder.currRow().addToken(r);
        }
    }

    @Override
    public List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx) {
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
                        new NDocTextTokenFlavored(type(), sb.toString().trim())
                );
            } else {
                char c = queue.read();
                sb.append(c);
            }
        }
        sb.flush();
        return Arrays.asList(
                new NDocTextTokenFlavored(type(), sb.toString().trim())
        );
    }


    public NDocTextRendererBuilder.ImagePainter createLatex(String tex, double fontSize, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx) {
        TeXFormula formula;
        boolean error = false;
        try {
            formula = new TeXFormula(tex);
        } catch (Exception ex) {
            error = true;
            formula = new TeXFormula("?error?");
            ctx.engine().log().log(NMsg.ofC("error evaluating latex formula %s : %s", tex, ex),NDocUtils.sourceOf(p));
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
            foregroundColor = Colors.resolveDefaultColorByIndex(options.foregroundColorIndex, null);
        } else if (options.foregroundColor instanceof Color) {
            foregroundColor = (Color) options.foregroundColor;
        }
        Color fg = NDocUtils.paintAsColor(NDocUtils.resolveForegroundColor(options));
        if (fg == null) {
            fg = NDocUtils.paintAsColor(ctx.getForegroundColor(p, true));
        }
        if (fg == null) {
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
