package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HLatexEquation;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.Gx;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;
import net.thevpc.halfa.spi.HUtils;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;

public class HLatexEquationRenderer extends AbstractHPartRenderer {

    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        HLatexEquation t = (HLatexEquation) p;
        String message = t.value();
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

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HLatexEquation t = (HLatexEquation) p;
        String message = t.value();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();


        String msg = NStringUtils.trim(message);
        if (msg.isEmpty()) {
            Bounds2 selfBounds = selfBounds(p, ctx);
            double x = selfBounds.getX();
            double y = selfBounds.getY();
            if (applyBackgroundColor(t, g, ctx)) {
                g.fillRect((int) x, (int) y, HUtils.intOf(selfBounds.getWidth()), HUtils.intOf(selfBounds.getHeight()));
            }
            return selfBounds;
        } else {
            TeXFormula formula;
            boolean error = false;
            try {
                formula = new TeXFormula(msg);
            } catch (Exception ex) {
                error = true;
                formula = new TeXFormula("?error?");
                ex.printStackTrace();
            }
            float size = (float) (resolveFontSize(p, g, ctx) * 1.188);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));

            Bounds2 selfBounds = selfBounds(t, new Double2(icon.getIconWidth(), icon.getIconHeight()), ctx);
            double x = selfBounds.getX();
            double y = selfBounds.getY();

            paintBackground(p, ctx, g, selfBounds);
            if (error) {
                Gx.paintBackground(g, selfBounds, Color.RED);
            }
            applyForeground(t, g, ctx);
            icon.setForeground(g.getColor());
            icon.paintIcon(null, g, (int) x, (int) y /*- icon.getIconHeight()*/);

            paintBorderLine(p, ctx, g, selfBounds);
//            paintBorderLine(t,  ctx,g,selfBounds);

            return selfBounds;
        }
    }


}
