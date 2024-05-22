package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.node.HLatexEquation;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;
import net.thevpc.halfa.engine.renderer.screen.svg.SvgRasterizer;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HLatexEquationRenderer extends AbstractHPartRenderer {

    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        HLatexEquation t = (HLatexEquation) p;
        String message = t.latex();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();

        TeXFormula formula = new TeXFormula(message);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) resolveFontSize(p, g, ctx));

        // insert a border
        icon.setInsets(new Insets(5, 5, 5, 5));

        int ww = icon.getIconWidth();
        int hh = icon.getIconHeight();
        HSizeRequirements r = new HSizeRequirements();
        r.minX = ww;
        r.maxX = ww;
        r.preferredX = ww;
        r.minY = hh;
        r.maxY = hh;
        r.preferredY = hh;
        return r;
    }

    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        HLatexEquation t = (HLatexEquation) p;
        String message = t.latex();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();


        Point2D.Double expectedBounds = size(p, ctx); //just ignored?
        final SvgRasterizer rasterizer = new SvgRasterizer();
        TeXFormula formula = new TeXFormula(message);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) resolveFontSize(p, g, ctx));
        // insert a border
//        icon.setInsets(new Insets(5, 5, 5, 5));

        Rectangle2D.Double selfBounds = selfBounds(t, new Point2D.Double(icon.getIconWidth(), icon.getIconHeight()), ctx);
        double x = selfBounds.getX();
        double y = selfBounds.getY();

        if (applyBackgroundColor(t, g, ctx)) {
            g.fillRect((int) x, (int) y, (int) selfBounds.getWidth(), (int) selfBounds.getHeight());
        }

        applyForeground(t, g, ctx);
        icon.setForeground(g.getColor());
        icon.paintIcon(null, g, (int) x, (int) y-icon.getIconHeight());

        if (applyLineColor(t, g, ctx)) {
            g.drawRect((int) x, (int) y, (int) selfBounds.getWidth(), (int) selfBounds.getHeight());
        }
        return selfBounds;
    }


}
