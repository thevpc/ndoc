package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.renderer.screen.common.HNodeRendererUtils;
import net.thevpc.halfa.spi.nodes.HPropValueByNameParser;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.Gx;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;

public class HEquationRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HEquationRenderer() {
        super(HNodeType.EQUATION);
    }

    //    @Override
//    public HSizeRequirements computeSizeRequirements(HNode p, HNodeRendererContext ctx) {
//        ctx=ctx.withDefaultStyles(p,defaultStyles);
//        String message = ObjEx.ofProp(p, HPropName.VALUE).asString().orNull();
//        if (message == null) {
//            message = "";
//        }
//        HGraphics g = ctx.graphics();
//
//        TeXFormula formula = new TeXFormula(message);
//        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) resolveFontSize(p, g, ctx));
//
//        // insert a border
//        icon.setInsets(new Insets(5, 5, 5, 5));
//
//        int ww = icon.getIconWidth();
//        int hh = icon.getIconHeight();
//        HSizeRequirements r = new HSizeRequirements();
//        r.minX = ww;
//        r.maxX = ww;
//        r.preferredX = ww;
//        r.minY = hh;
//        r.maxY = hh;
//        r.preferredY = hh;
//        return r;
//    }
    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        Bounds2 s = selfBounds(p, ctx);
        Bounds2 bb = ctx.getBounds();
        return new HSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public Bounds2 selfBounds(HNode p, HNodeRendererContext ctx) {
        String message = ObjEx.ofProp(p, HPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        HGraphics g = ctx.graphics();
        String msg = NStringUtils.trim(message);
        if (msg.isEmpty()) {
            return new Bounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), 0.0, 0.0);
        } else {
            TeXFormula formula;
            try {
                formula = new TeXFormula(msg);
            } catch (Exception ex) {
                formula = new TeXFormula("?error?");
                ex.printStackTrace();
            }
            float size = (float) (HPropValueByNameParser.getFontSize(p, ctx) * 1.188);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));
            return new Bounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), icon.getIconWidth(), icon.getIconHeight());
        }
    }


    public void render0(HNode p, HNodeRendererContext ctx) {
        String message = ObjEx.ofProp(p, HPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        HGraphics g = ctx.graphics();


        String msg = NStringUtils.trim(message);
        if (msg.isEmpty()) {
            Bounds2 selfBounds = selfBounds(p, ctx);
            double x = selfBounds.getX();
            double y = selfBounds.getY();
            if (!ctx.isDry()) {
                if (HNodeRendererUtils.applyBackgroundColor((HNode) p, g, ctx)) {
                    g.fillRect((int) x, (int) y, HUtils.intOf(selfBounds.getWidth()), HUtils.intOf(selfBounds.getHeight()));
                }
            }
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
            float size = (float) (HPropValueByNameParser.getFontSize(p, ctx) * 1.188);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));

            Bounds2 selfBounds = HPropValueByNameParser.selfBounds((HNode) p
                    , new Double2(icon.getIconWidth(), icon.getIconHeight())
                    , null
                    , ctx);
            double x = selfBounds.getX();
            double y = selfBounds.getY();

            if (!ctx.isDry()) {
                HNodeRendererUtils.paintBackground(p, ctx, g, selfBounds);
                if (error) {
                    Gx.paintBackground(g, selfBounds, Color.RED);
                }
                HNodeRendererUtils.applyForeground((HNode) p, g, ctx,false);
                icon.setForeground(g.getColor());
                icon.paintIcon(null, g.context(), (int) x, (int) y /*- icon.getIconHeight()*/);

                HNodeRendererUtils.paintBorderLine(p, ctx, g, selfBounds);
            }
        }
    }


}
