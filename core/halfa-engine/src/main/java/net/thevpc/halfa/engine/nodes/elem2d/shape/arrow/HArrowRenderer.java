package net.thevpc.halfa.engine.nodes.elem2d.shape.arrow;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererBase;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class HArrowRenderer extends HNodeRendererBase {
    public HArrowRenderer() {
        super(HNodeType.ARROW);
    }

    @Override
    public void renderMain(HNode p, HNodeRendererContext ctx) {
        Bounds2 b = HValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        Paint color = HValueByName.getForegroundColor(p,ctx, true);
        double[] points = ObjEx.of(p.getPropertyValue("points")).asDoubleArray().orElse(null);

        HGraphics g = ctx.graphics();
        if (!ctx.isDry() && points != null && points.length == 18) {
            g.setPaint(color);

            GeneralPath arrow = new GeneralPath();
            arrow.moveTo(points[0], points[1]);
            arrow.lineTo(points[2], points[3]);
            arrow.lineTo(points[4], points[5]);
            arrow.lineTo(points[6], points[7]);
            arrow.lineTo(points[8], points[9]);
            arrow.lineTo(points[10], points[11]);
            arrow.lineTo(points[12], points[13]);
            arrow.lineTo(points[14], points[15]);
            arrow.lineTo(points[16], points[17]);


            arrow.closePath();

            g.fill(arrow);

            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }

}
