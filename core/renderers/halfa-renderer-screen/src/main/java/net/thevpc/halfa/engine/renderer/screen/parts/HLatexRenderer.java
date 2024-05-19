package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.model.HLatexEquation;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.svg.SvgRasterizer;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HLatexRenderer extends AbstractHPartRenderer {

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        HLatexEquation t = (HLatexEquation) p;
        String message = t.latex();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();


        Point2D.Double expectedBounds = size(p, ctx); //just ignored?
        final SvgRasterizer rasterizer = new SvgRasterizer();
        TeXFormula formula = new TeXFormula(message);
// render the formla to an icon of the same size as the formula.
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) resolveFontSize(p, g, ctx));

        // insert a border
        icon.setInsets(new Insets(5, 5, 5, 5));

        Rectangle2D b = new Rectangle2D.Double(0, 0, icon.getIconWidth(), icon.getIconHeight());
        Point2D.Double pos = pos(t, b, ctx);
        double x = pos.getX();
        double y = pos.getY();

        if (applyBackgroundColor(t, g, ctx)) {
            g.fillRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
        }

        applyForeground(t, g, ctx);
        icon.setForeground(g.getColor());
        icon.paintIcon(null, g, (int) x, (int) (y - b.getMinY()));

        if (applyLineColor(t, g, ctx)) {
            g.drawRect((int) x, (int) y, (int) b.getWidth(), (int) b.getHeight());
        }
        return new Rectangle2D.Double(x, y, b.getWidth(), b.getWidth());
    }


}
