/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.extension.latex.eq;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.model.NDocSizeRequirements;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;

/**
 * @author vpc
 */
public class NDocEquationBuilder implements NDocNodeCustomBuilder {

    NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.EQUATION)
                .alias("eq")
                .parseParam().named(NDocPropName.VALUE).then()
                .parseParam().matchesStringOrName().set(NDocPropName.VALUE).then()
                .renderComponent(this::renderMain)
                .sizeRequirements(this::sizeRequirements)
                .selfBounds(this::selfBounds);
    }


    public NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext rendererContext,NDocNodeCustomBuilderContext builderContext) {
        NDocBounds2 s = rendererContext.selfBounds(p);
        NDocBounds2 bb = rendererContext.getBounds();
        return new NDocSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public NDocBounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
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


    public void renderMain(NDocNode p, NDocNodeRendererContext rendererContext,NDocNodeCustomBuilderContext builderContext) {
        String message = NDocObjEx.ofProp(p, NDocPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        NDocGraphics g = rendererContext.graphics();


        String msg = NStringUtils.trim(message);
        if (msg.isEmpty()) {
            NDocBounds2 selfBounds = rendererContext.selfBounds(p);
            double x = selfBounds.getX();
            double y = selfBounds.getY();
            if (!rendererContext.isDry()) {
                if (NDocNodeRendererUtils.applyBackgroundColor((NDocNode) p, g, rendererContext)) {
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
            float size = (float) (NDocValueByName.getFontSize(p, rendererContext) /* * 0.43 */);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));

            NDocBounds2 selfBounds = NDocValueByName.selfBounds((NDocNode) p
                    , new NDocDouble2(icon.getIconWidth(), icon.getIconHeight())
                    , null
                    , rendererContext);
            double x = selfBounds.getX();
            double y = selfBounds.getY();

            if (!rendererContext.isDry()) {
                NDocNodeRendererUtils.paintBackground(p, rendererContext, g, selfBounds);
                if (error) {
                    g.setColor(Color.RED);
                    g.fillRect(selfBounds);
                }
                Paint fg = NDocValueByName.getForegroundColor(p, rendererContext,true);
                icon.setForeground(NDocNodeRendererUtils.colorFromPaint(fg).orElse(Color.BLACK));
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);

                NDocNodeRendererUtils.paintBorderLine(p, rendererContext, g, selfBounds);
            }
        }
    }

}
