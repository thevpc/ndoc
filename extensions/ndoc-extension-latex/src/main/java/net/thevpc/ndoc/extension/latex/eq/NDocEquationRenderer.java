package net.thevpc.ndoc.extension.latex.eq;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.spi.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;

public class NDocEquationRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocEquationRenderer() {
        super(NDocNodeType.EQUATION);
    }

    //    @Override
//    public HSizeRequirements computeSizeRequirements(NDocNode p, NDocNodeRendererContext ctx) {
//        ctx=ctx.withDefaultStyles(p,defaultStyles);
//        String message = ObjEx.ofProp(p, NDocPropName.VALUE).asStringValue().orNull();
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
    public NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx) {
        NDocBounds2 s = selfBounds(p, ctx);
        NDocBounds2 bb = ctx.getBounds();
        return new NDocSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public NDocBounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx) {
        String message = NDocObjEx.ofProp(p, NDocPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        NDocGraphics g = ctx.graphics();
        String msg = NStringUtils.trim(message);
        if (msg.isEmpty()) {
            return new NDocBounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), 0.0, 0.0);
        } else {
            TeXFormula formula;
            try {
                formula = new TeXFormula(msg);
            } catch (Exception ex) {
                formula = new TeXFormula("?error?");
                ex.printStackTrace();
            }
            float size = (float) (NDocValueByName.getFontSize(p, ctx) * 1.0);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));
            return new NDocBounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), icon.getIconWidth(), icon.getIconHeight());
        }
    }


    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        String message = NDocObjEx.ofProp(p, NDocPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        NDocGraphics g = ctx.graphics();


        String msg = NStringUtils.trim(message);
        if (msg.isEmpty()) {
            NDocBounds2 selfBounds = selfBounds(p, ctx);
            double x = selfBounds.getX();
            double y = selfBounds.getY();
            if (!ctx.isDry()) {
                if (NDocNodeRendererUtils.applyBackgroundColor((NDocNode) p, g, ctx)) {
                    g.fillRect((int) x, (int) y, NDocUtils.intOf(selfBounds.getWidth()), NDocUtils.intOf(selfBounds.getHeight()));
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
            float size = (float) (NDocValueByName.getFontSize(p, ctx) /* * 0.43 */);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));

            NDocBounds2 selfBounds = NDocValueByName.selfBounds((NDocNode) p
                    , new NDocDouble2(icon.getIconWidth(), icon.getIconHeight())
                    , null
                    , ctx);
            double x = selfBounds.getX();
            double y = selfBounds.getY();

            if (!ctx.isDry()) {
                NDocNodeRendererUtils.paintBackground(p, ctx, g, selfBounds);
                if (error) {
                    g.setColor(Color.RED);
                    g.fillRect(selfBounds);
                }
                Paint fg = NDocValueByName.getForegroundColor(p, ctx,true);
                icon.setForeground(NDocNodeRendererUtils.colorFromPaint(fg).orElse(Color.BLACK));
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);

                NDocNodeRendererUtils.paintBorderLine(p, ctx, g, selfBounds);
            }
        }
    }


}
