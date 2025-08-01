package net.thevpc.ndoc.extension.shapes2d;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem2d.Shadow;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;


public class NDocCylinderBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();
    private static final Color DEFAULT_SIDE_COLOR = new Color(0x00215E);
    //private static final Color DEFAULT_TOP_COLOR = new Color(0xD1D8C5);

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.CYLINDER)
                .parseParam().named(NDocPropName.ELLIPSE_H,NDocPropName.TOP_COLOR,NDocPropName.SEGMENT_COUNT).end()
                .renderComponent(this::render);
    }




    public void render(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext builderContext) {
        renderContext = renderContext.withDefaultStyles(p, defaultStyles);
        NOptional<Shadow> shadowOptional = renderContext.readStyleAsShadow(p, NDocPropName.SHADOW);


        NDocBounds2 b = renderContext.selfBounds(p, null, null);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        double arcStroke = NDocValue.of(p.getPropertyValue(NDocPropName.STROKE)).asDouble().orElse(5.0);
        double ellipse_height = NDocValue.of(p.getPropertyValue(NDocPropName.ELLIPSE_H)).asDouble().orElse(50.0);
        ellipse_height = ellipse_height / 100 * height;
        double arcY = y + height - ellipse_height / 2;


        Color sideColor = NDocValue.of(p.getPropertyValue(NDocPropName.BACKGROUND_COLOR)).asColor().orElse(DEFAULT_SIDE_COLOR);
        Color topColor = NDocValue.of(p.getPropertyValue(NDocPropName.TOP_COLOR)).asColor().orElse(sideColor.brighter());

        int segmentCount = NDocValue.of(p.getPropertyValue(NDocPropName.SEGMENT_COUNT)).asInt().orElse(0);

        boolean someBG = false;
        NDocGraphics g = renderContext.graphics();

        if (!renderContext.isDry()) {
            if (shadowOptional.isPresent()) {
                Shadow shadow = shadowOptional.get();
                NDocPoint2D translation = shadow.getTranslation();
                if (translation == null) {
                    translation = new NDocPoint2D(0, 0);
                }
                Paint shadowColor = shadow.getColor();
                if (shadowColor == null) {
                    shadowColor = sideColor.darker();
                }
                NDocPoint2D shear = shadow.getShear();
                if (shear == null) {
                    shear = new NDocPoint2D(0, 0);
                }

                g.setColor((Color) shadowColor);
                AffineTransform t = g.getTransform();
                g.shear(-1, 0);

                g.fillRect((int) (x + translation.getX()), (int) (y + ellipse_height / 2 + translation.getY()), NDocUtils.intOf(width), NDocUtils.intOf(height - ellipse_height));
                g.fillOval((int) (x + translation.getX()), (int) (arcY - ellipse_height / 2 + translation.getY()), NDocUtils.intOf(width), NDocUtils.intOf(ellipse_height));
                g.fillOval((int) (x + translation.getX()), (int) (y + translation.getY()), NDocUtils.intOf(width), NDocUtils.intOf(ellipse_height));

                g.setTransform(t);


            }
            double finalEllipse_height = ellipse_height;
            if (someBG = renderContext.applyBackgroundColor(p)) {

                renderContext.withStroke(p, () -> {
                    g.setColor(sideColor);
                    g.fillRect(x, y + finalEllipse_height / 2, width, height - finalEllipse_height);
                    g.fillOval((int) x, (int) arcY - finalEllipse_height / 2, NDocUtils.intOf(width), NDocUtils.intOf(finalEllipse_height));

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


            if (renderContext.applyForeground(p, !someBG)) {
                renderContext.withStroke(p, () -> {
                    g.drawOval((int) x, (int) y, NDocUtils.doubleOf(width), NDocUtils.intOf(finalEllipse_height));

                    g.drawLine((int) x, (int) (y + finalEllipse_height / 2), (int) x, (int) (arcY));
                    g.drawLine((int) (x + width), (int) (y + finalEllipse_height / 2), (int) (x + width), (int) (arcY));

                    g.drawArc((int) x, (int) arcY - finalEllipse_height / 2, NDocUtils.intOf(width), NDocUtils.intOf(finalEllipse_height), 0, -180);

                    double segmentHeight = (height - finalEllipse_height) / (segmentCount + 1);
                    for (int i = 1; i <= segmentCount; i++) {
                        double segY = y + i * segmentHeight;
                        g.drawArc(x, segY, NDocUtils.doubleOf(width), NDocUtils.doubleOf(finalEllipse_height), 0, -180);
                    }
                });
            }
            renderContext.paintDebugBox(p, b);
        }
    }
}
