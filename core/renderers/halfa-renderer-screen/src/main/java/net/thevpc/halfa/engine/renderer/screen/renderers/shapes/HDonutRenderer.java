package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import javafx.scene.transform.Shear;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem2d.Shadow;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engine.renderer.screen.common.HNodeRendererUtils;
import net.thevpc.halfa.spi.nodes.HPropValueByNameParser;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class HDonutRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HDonutRenderer() {
        super(HNodeType.DONUT);
    }

    @Override
    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);

        Bounds2 b = HPropValueByNameParser.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        double outerRadius = Math.min(width, height) / 2;
        double innerRadius = ObjEx.of(p.getPropertyValue(HPropName.INNER_RADIUS)).asDouble().orElse(outerRadius /2);
        innerRadius = innerRadius / 100 * outerRadius;

        Color bgColor = ObjEx.of(p.getPropertyValue(HPropName.BACKGROUND_COLOR)).asColor().orElse(Color.blue);
        double startAngle = ObjEx.of(p.getPropertyValue(HPropName.START_ANGLE)).asDouble().orElse(0.0);
        double extentAngle = ObjEx.of(p.getPropertyValue(HPropName.EXTENT_ANGLE)).asDouble().orElse(360.0);
        //double extentAngle = 360 * (extentPercent / 100);


        boolean someBG = false;
        HGraphics g = ctx.graphics();

        if (!ctx.isDry()) {
            if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                HNodeRendererUtils.applyStroke(p, g, ctx);

                double centerX = x + width/2;
                double centerY = y + height/2;

                Area outerCircle = new Area(new Arc2D.Double(centerX - outerRadius, centerY - outerRadius, outerRadius * 2, outerRadius * 2, startAngle, extentAngle, Arc2D.PIE));
                Area innerCircle = new Area(new Arc2D.Double(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2, startAngle, extentAngle, Arc2D.PIE));
                outerCircle.subtract(innerCircle);

                g.setColor(bgColor);
                g.fill(outerCircle);
            }

            if (HNodeRendererUtils.applyLineColor(p, g, ctx, !someBG)) {
                HNodeRendererUtils.applyStroke(p, g, ctx);

                double centerX = x + width/2;
                double centerY = y + height/2;

                g.drawOval((int) (centerX - outerRadius), (int) (centerY - outerRadius), (int) (outerRadius * 2), (int) (outerRadius * 2));
                g.drawOval((int) (centerX - innerRadius), (int) (centerY - innerRadius), (int) (innerRadius * 2), (int) (innerRadius * 2));
            }

            HNodeRendererUtils.paintDebugBox(p, ctx, g, b,true);
        }
    }
}
