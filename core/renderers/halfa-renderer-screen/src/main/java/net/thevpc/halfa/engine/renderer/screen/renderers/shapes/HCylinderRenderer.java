package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem2d.Shadow;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engin.spibase.renderer.AbstractHNodeRenderer;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NOptional;


import java.awt.Color;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class HCylinderRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();
    private static final Color DEFAULT_SIDE_COLOR = new Color(0x00215E);
    //private static final Color DEFAULT_TOP_COLOR = new Color(0xD1D8C5);

    public HCylinderRenderer() {
        super(HNodeType.CYLINDER);
    }

    @Override
    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NOptional<Shadow> shadowOptional = HValueByName.readStyleAsShadow(p, HPropName.SHADOW, ctx);


        Bounds2 b = HValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        double arcStroke = ObjEx.of(p.getPropertyValue(HPropName.STROKE)).asDouble().orElse(5.0);
        double ellipse_height = ObjEx.of(p.getPropertyValue(HPropName.ELLIPSE_H)).asDouble().orElse(50.0);
        ellipse_height = ellipse_height / 100 * height;
        double arcY = y + height - ellipse_height / 2;


        Color sideColor = ObjEx.of(p.getPropertyValue(HPropName.BACKGROUND_COLOR)).asColor().orElse(DEFAULT_SIDE_COLOR);
        Color topColor = ObjEx.of(p.getPropertyValue(HPropName.TOP_COLOR)).asColor().orElse(sideColor.brighter());

        int segmentCount = ObjEx.of(p.getPropertyValue(HPropName.SEGMENT_COUNT)).asInt().orElse(0);

        boolean someBG = false;
        HGraphics g = ctx.graphics();

        if (!ctx.isDry()) {
            if (shadowOptional.isPresent()) {
                Shadow shadow = shadowOptional.get();
                HPoint2D translation = shadow.getTranslation();
                if (translation == null) {
                    translation = new HPoint2D(0, 0);
                }
                Paint shadowColor = shadow.getColor();
                if (shadowColor == null) {
                    shadowColor = sideColor.darker();
                }
                HPoint2D shear = shadow.getShear();
                if (shear == null) {
                    shear = new HPoint2D (0,0);
                }

                g.setColor((Color) shadowColor);
                AffineTransform t = g.getTransform();
                g.shear( -1, 0);

                g.fillRect((int) (x + translation.getX()), (int) (y + ellipse_height / 2 + translation.getY()), HUtils.intOf(width), HUtils.intOf(height - ellipse_height));
                g.fillOval((int) (x + translation.getX()), (int) (arcY - ellipse_height / 2 + translation.getY()), HUtils.intOf(width), HUtils.intOf(ellipse_height));
                g.fillOval((int) (x + translation.getX()), (int) (y + translation.getY()), HUtils.intOf(width), HUtils.intOf(ellipse_height));

                g.setTransform(t);


            }
            if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {

                HNodeRendererUtils.applyStroke(p, g, ctx);
                g.setColor(sideColor);
                g.fillRect(x, y + ellipse_height / 2, width, height - ellipse_height);
                g.fillOval((int) x, (int) arcY - ellipse_height / 2, HUtils.intOf(width), HUtils.intOf(ellipse_height));

                g.setColor(sideColor.darker());
                Area rightButtomArc = new Area( new Arc2D.Double(x, (int) arcY - ellipse_height / 2, width, ellipse_height, -90, -180, Arc2D.PIE));
                g.fill(rightButtomArc);

                g.fillRect(x, y + ellipse_height / 2, width/2, height - ellipse_height);


                g.setColor(topColor);
                g.fillOval((int) x, (int) y, width,ellipse_height);

                Area rightHalfEllipse = new Area( new Arc2D.Double(x, y, width, ellipse_height, 90, 180, Arc2D.PIE));
                g.setColor(topColor.darker());
                g.fill(rightHalfEllipse);



                double segmentHeight = (height - ellipse_height) / (segmentCount + 1);
                for (int i = 1; i <= segmentCount; i++) {
                    double segY = y + i * segmentHeight;

                    g.setColor(topColor.darker());
                    g.drawArc(x, segY + arcStroke, width, ellipse_height, 0, -180);
                    g.setColor(topColor);
                    g.drawArc(x, segY + arcStroke, width, ellipse_height, 0, -90);
                    g.setColor(Color.gray);
                    g.drawArc(x, segY, width, ellipse_height, 0, -180);

                }



            }



            if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                HNodeRendererUtils.applyStroke(p, g, ctx);

                g.drawOval((int) x, (int) y, HUtils.doubleOf(width), HUtils.intOf(ellipse_height));

                g.drawLine((int) x, (int) (y + ellipse_height / 2), (int) x, (int) (arcY));
                g.drawLine((int) (x + width), (int) (y + ellipse_height / 2), (int) (x + width), (int) (arcY));

                g.drawArc((int) x, (int) arcY - ellipse_height / 2, HUtils.intOf(width), HUtils.intOf(ellipse_height), 0, -180);

                double segmentHeight = (height - ellipse_height) / (segmentCount + 1);
                for (int i = 1; i <= segmentCount; i++) {
                    double segY = y + i * segmentHeight;
                    g.drawArc(x, segY, HUtils.doubleOf(width), HUtils.doubleOf(ellipse_height), 0, -180);
                }
            }
            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }
}
