/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.extension.latex.eq;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;

/**
 * @author vpc
 */
public class NDocEquationBuilder implements NDocNodeBuilder {

    NTxProperties defaultStyles = new NTxProperties();
    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.EQUATION)
                .alias("eq")
                .parseParam().named(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().matchesStringOrName().store(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderComponent(this::renderMain)
                .sizeRequirements(this::sizeRequirements)
                .selfBounds(this::selfBounds);
    }


    public NDocSizeRequirements sizeRequirements(NTxNode p, NDocNodeRendererContext rendererContext, NTxNodeCustomBuilderContext builderContext) {
        NTxBounds2 s = rendererContext.selfBounds(p);
        NTxBounds2 bb = rendererContext.getBounds();
        return new NDocSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public NTxBounds2 selfBounds(NTxNode p, NDocNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        String message = NDocValue.ofProp(p, NTxPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        NDocGraphics g = ctx.graphics();
        String tex = NStringUtils.trim(message);
        if (tex.isEmpty()) {
            return new NTxBounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), 0.0, 0.0);
        } else {
            TeXFormula formula;
            try {
                formula = new TeXFormula(tex);
            } catch (Exception ex) {
                formula = new TeXFormula("?error?");
                ctx.engine().log().log(NMsg.ofC("error evaluating latex formula %s : %s", tex, ex),NDocUtils.sourceOf(p));
            }
            float size = (float) (ctx.getFontSize(p) * 1.0);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));
            return new NTxBounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), icon.getIconWidth(), icon.getIconHeight());
        }
    }


    public void renderMain(NTxNode p, NDocNodeRendererContext renderContext, NTxNodeCustomBuilderContext builderContext) {
        String message = NDocValue.ofProp(p, NTxPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        NDocGraphics g = renderContext.graphics();


        String tex = NStringUtils.trim(message);
        if (tex.isEmpty()) {
            NTxBounds2 selfBounds = renderContext.selfBounds(p);
            double x = selfBounds.getX();
            double y = selfBounds.getY();
            if (!renderContext.isDry()) {
                if (renderContext.applyBackgroundColor(p)) {
                    g.fillRect((int) x, (int) y, NDocUtils.intOf(selfBounds.getWidth()), NDocUtils.intOf(selfBounds.getHeight()));
                }
            }
        } else {
            TeXFormula formula;
            boolean error = false;
            try {
                formula = new TeXFormula(tex);
            } catch (Exception ex) {
                error = true;
                formula = new TeXFormula("?error?");
                builderContext.engine().log().log(NMsg.ofC("error evaluating latex formula %s : %s", tex, ex),NDocUtils.sourceOf(p));
            }
            float size = (float) (renderContext.getFontSize(p) /* * 0.43 */);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));

            NTxBounds2 selfBounds = renderContext.selfBounds((NTxNode) p
                    , new NTxDouble2(icon.getIconWidth(), icon.getIconHeight())
                    , null
                    );
            double x = selfBounds.getX();
            double y = selfBounds.getY();

            if (!renderContext.isDry()) {
                renderContext.paintBackground(p, selfBounds);
                if (error) {
                    g.setColor(Color.RED);
                    g.fillRect(selfBounds);
                }
                Paint fg = renderContext.getForegroundColor(p,true);
                icon.setForeground(renderContext.colorFromPaint(fg).orElse(Color.BLACK));
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);

                renderContext.paintBorderLine(p, selfBounds);
            }
        }
    }

}
