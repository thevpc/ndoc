package net.thevpc.ndoc.extension.shapes2d.cylinder;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.elem2d.Shadow;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.util.NOptional;


import java.awt.Color;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;

public class NDocCylinderRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();
    private static final Color DEFAULT_SIDE_COLOR = new Color(0x00215E);
    //private static final Color DEFAULT_TOP_COLOR = new Color(0xD1D8C5);

    public NDocCylinderRenderer() {
        super(HNodeType.CYLINDER);
    }

    @Override
    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NOptional<Shadow> shadowOptional = NDocValueByName.readStyleAsShadow(p, HPropName.SHADOW, ctx);


        Bounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        double arcStroke = NDocObjEx.of(p.getPropertyValue(HPropName.STROKE)).asDouble().orElse(5.0);
        double ellipse_height = NDocObjEx.of(p.getPropertyValue(HPropName.ELLIPSE_H)).asDouble().orElse(50.0);
        ellipse_height = ellipse_height / 100 * height;
        double arcY = y + height - ellipse_height / 2;


        Color sideColor = NDocObjEx.of(p.getPropertyValue(HPropName.BACKGROUND_COLOR)).asColor().orElse(DEFAULT_SIDE_COLOR);
        Color topColor = NDocObjEx.of(p.getPropertyValue(HPropName.TOP_COLOR)).asColor().orElse(sideColor.brighter());

        int segmentCount = NDocObjEx.of(p.getPropertyValue(HPropName.SEGMENT_COUNT)).asInt().orElse(0);

        boolean someBG = false;
        NDocGraphics g = ctx.graphics();

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
                    shear = new HPoint2D(0, 0);
                }

                g.setColor((Color) shadowColor);
                AffineTransform t = g.getTransform();
                g.shear(-1, 0);

                g.fillRect((int) (x + translation.getX()), (int) (y + ellipse_height / 2 + translation.getY()), net.thevpc.ndoc.api.util.HUtils.intOf(width), net.thevpc.ndoc.api.util.HUtils.intOf(height - ellipse_height));
                g.fillOval((int) (x + translation.getX()), (int) (arcY - ellipse_height / 2 + translation.getY()), net.thevpc.ndoc.api.util.HUtils.intOf(width), net.thevpc.ndoc.api.util.HUtils.intOf(ellipse_height));
                g.fillOval((int) (x + translation.getX()), (int) (y + translation.getY()), net.thevpc.ndoc.api.util.HUtils.intOf(width), net.thevpc.ndoc.api.util.HUtils.intOf(ellipse_height));

                g.setTransform(t);


            }
            double finalEllipse_height = ellipse_height;
            if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {

                HNodeRendererUtils.withStroke(p, g, ctx, () -> {
                    g.setColor(sideColor);
                    g.fillRect(x, y + finalEllipse_height / 2, width, height - finalEllipse_height);
                    g.fillOval((int) x, (int) arcY - finalEllipse_height / 2, net.thevpc.ndoc.api.util.HUtils.intOf(width), net.thevpc.ndoc.api.util.HUtils.intOf(finalEllipse_height));

                    g.setColor(sideColor.darker());
                    Area rightButtomArc = new Area(new Arc2D.Double(x, (int) arcY - finalEllipse_height / 2, width, finalEllipse_height, -90, -180, Arc2D.PIE));
                    g.fill(rightButtomArc);

                    g.fillRect(x, y + finalEllipse_height / 2, width / 2, height - finalEllipse_height);


                    g.setColor(topColor);
                    g.fillOval((int) x, (int) y, width, finalEllipse_height);

                    Area rightHalfEllipse = new Area(new Arc2D.Double(x, y, width, finalEllipse_height, 90, 180, Arc2D.PIE));
                    g.setColor(topColor.darker());
                    g.fill(rightHalfEllipse);


                    double segmentHeight = (height - finalEllipse_height) / (segmentCount + 1);
                    for (int i = 1; i <= segmentCount; i++) {
                        double segY = y + i * segmentHeight;

                        g.setColor(topColor.darker());
                        g.drawArc(x, segY + arcStroke, width, finalEllipse_height, 0, -180);
                        g.setColor(topColor);
                        g.drawArc(x, segY + arcStroke, width, finalEllipse_height, 0, -90);
                        g.setColor(Color.gray);
                        g.drawArc(x, segY, width, finalEllipse_height, 0, -180);

                    }
                });
            }


            if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                HNodeRendererUtils.withStroke(p, g, ctx, () -> {
                    g.drawOval((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.doubleOf(width), net.thevpc.ndoc.api.util.HUtils.intOf(finalEllipse_height));

                    g.drawLine((int) x, (int) (y + finalEllipse_height / 2), (int) x, (int) (arcY));
                    g.drawLine((int) (x + width), (int) (y + finalEllipse_height / 2), (int) (x + width), (int) (arcY));

                    g.drawArc((int) x, (int) arcY - finalEllipse_height / 2, net.thevpc.ndoc.api.util.HUtils.intOf(width), HUtils.intOf(finalEllipse_height), 0, -180);

                    double segmentHeight = (height - finalEllipse_height) / (segmentCount + 1);
                    for (int i = 1; i <= segmentCount; i++) {
                        double segY = y + i * segmentHeight;
                        g.drawArc(x, segY, net.thevpc.ndoc.api.util.HUtils.doubleOf(width), net.thevpc.ndoc.api.util.HUtils.doubleOf(finalEllipse_height), 0, -180);
                    }
                });
            }
            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }
}
