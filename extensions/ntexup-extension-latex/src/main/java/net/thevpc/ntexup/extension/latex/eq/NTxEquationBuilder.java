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
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.eval.NTxValueByName;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.*;
import net.thevpc.ntexup.api.util.NTxColors;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * @author vpc
 */
public class NTxEquationBuilder implements NTxNodeBuilder {

    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.EQUATION)
                .alias("equation")
                .parseParam().named(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().matchesAnyNonPair().store(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderComponent(this::renderMain)
                .renderText()
                .buildText(this::buildText)
                .parseTokens(this::parseTokens)
                .startSeparators("\\(")
                .end()
                .sizeRequirements(this::sizeRequirements)
                .selfBounds(this::selfBounds);
    }


    public NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext rendererContext, NTxNodeCustomBuilderContext builderContext) {
        NTxBounds2 s = rendererContext.selfBounds(p);
        NTxBounds2 bb = rendererContext.getBounds();
        return new NTxSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public NTxBounds2 selfBounds(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        String message = NTxValue.ofProp(p, NTxPropName.VALUE).asStringOrName().orNull();
        if (message == null) {
            message = "";
        }
        NTxGraphics g = ctx.graphics();
        String tex = NStringUtils.trim(message);
        if (tex.isEmpty()) {
            return new NTxBounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), 0.0, 0.0);
        } else {
            TeXFormula formula;
            try {
                formula = new TeXFormula(tex);
            } catch (Exception ex) {
                formula = new TeXFormula("?error?");
                ctx.engine().log().log(NMsg.ofC("error evaluating latex formula %s : %s", tex, ex), NTxUtils.sourceOf(p));
            }
            float size = (float) (ctx.getFontSize(p) * 1.0);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // insert a border
            icon.setInsets(new Insets(0, 0, 0, 0));
            return new NTxBounds2(ctx.getBounds().getX(), ctx.getBounds().getY(), icon.getIconWidth(), icon.getIconHeight());
        }
    }


    public void renderMain(NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext builderContext) {
        NElement vElemExpr = p.getPropertyValue(NTxPropName.VALUE).orNull();
        NElement vElemValue = renderContext.engine().evalExpression(vElemExpr, p, renderContext.varProvider());
        String text = NTxValue.of(vElemValue).asStringOrName().orElse("");

        NTxGraphics g = renderContext.graphics();


        String tex = NStringUtils.trim(renderContext.engine().tools().trimBloc(text));
        if (tex.isEmpty()) {
            NTxBounds2 selfBounds = renderContext.selfBounds(p);
            double x = selfBounds.getX();
            double y = selfBounds.getY();
            if (!renderContext.isDry()) {
                if (renderContext.applyBackgroundColor(p)) {
                    g.fillRect((int) x, (int) y, NTxUtils.intOf(selfBounds.getWidth()), NTxUtils.intOf(selfBounds.getHeight()));
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
                builderContext.engine().log().log(NMsg.ofC("error evaluating latex formula %s : %s", tex, ex), NTxUtils.sourceOf(p));
            }

            NTxTextOptions oo=new NTxTextOptions();
            oo.defaultFont=NTxValueByName.getFontInfo(p, renderContext);
            oo.sr=renderContext.sizeRef();
            Font font = oo.resolveFont(g);


            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, font.getSize());

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
                Paint fg = renderContext.getForegroundColor(p, true);
                icon.setForeground(renderContext.colorFromPaint(fg).orElse(Color.BLACK));
                icon.paintIcon(null, g.graphics2D(), (int) x, (int) y /*- icon.getIconHeight()*/);

                renderContext.paintBorderLine(p, selfBounds);
            }
        }
    }

    private void buildText(String text, NTxTextOptions options, NTxNode p, NTxNodeRendererContext renderContext, NTxTextRendererBuilder builder, NTxNodeCustomBuilderContext buildContext) {
        if (!text.isEmpty()) {
            NTxRichTextToken r = new NTxRichTextToken(
                    NTxRichTextTokenType.IMAGE_PAINTER,
                    text
            );
            options=options.copy();
            if(options.defaultFont==null){
                options.defaultFont=NTxValueByName.getFontInfo(p, renderContext);
            }
            if(options.sr==null){
                options.sr=renderContext.sizeRef();
            }
            Font font = options.resolveFont(renderContext.graphics());
            r.imagePainter = this.createLatex(text, font.getSize(), options, p, renderContext);
            NTxDouble2 size = r.imagePainter.size();
            r.bounds = new Rectangle2D.Double(0, 0, size.getX(), size.getX());
            builder.currRow().addToken(r);
        }
    }

    private List<NTxTextToken> parseTokens(NTxTextRendererFlavorParseContext renderContext, NTxNodeCustomBuilderContext buildContext) {
        return renderContext.parseDefault(buildContext.idAndAliases(), new String[]{
                "\\(", "\\)"
        }, s -> {
            NExtendedLatexMathBuilder sb = new NExtendedLatexMathBuilder();
            sb.append(s);
            sb.flush();
            return sb.toString();
        });
    }

    public NTxTextRendererBuilder.ImagePainter createLatex(String tex, double fontSize, NTxTextOptions options, NTxNode node, NTxNodeRendererContext ctx) {
        TeXFormula formula;
        boolean error = false;
        try {
            formula = new TeXFormula(tex);
        } catch (Exception ex) {
            error = true;
            formula = new TeXFormula("?error?");
            ctx.engine().log().log(NMsg.ofC("error evaluating latex formula %s : %s", tex, ex), NTxUtils.sourceOf(node));
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
            foregroundColor = NTxColors.resolveDefaultColorByIndex(options.foregroundColorIndex, null,node,ctx);
        } else if (options.foregroundColor instanceof Color) {
            foregroundColor = (Color) options.foregroundColor;
        }
        Color fg = NTxUtils.paintAsColor(NTxUtils.resolveForegroundColor(options, node, ctx));
        if (fg == null) {
            fg = NTxUtils.paintAsColor(ctx.getForegroundColor(node, true));
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
