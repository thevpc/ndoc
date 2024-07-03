package net.thevpc.halfa.engine.nodes.elem2d.shape.quadcurve;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HElement2DFactory;
import net.thevpc.halfa.api.model.elem2d.HPoint;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererBase;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.eval.HValueByType;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.awt.*;

public class HQuadCurveRenderer extends HNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public HQuadCurveRenderer() {
        super(HNodeType.QUAD_CURVE);
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        HPoint2D translation = new HPoint2D(b.getX(), b.getY());
        HPoint2D from = HPoint.ofParent(ObjEx.ofProp(p, HPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        HPoint2D to = HPoint.ofParent(ObjEx.ofProp(p, HPropName.TO).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        HPoint2D ctrl = HPoint.ofParent(ObjEx.ofProp(p, HPropName.CTRL).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint fc = HValueByName.getForegroundColor(p, ctx, true);
            g.draw2D(HElement2DFactory.quad(from,ctrl, to)
                    .setStartArrow(HValueByType.getArrow(p, ctx, HPropName.START_ARROW).orNull())
                    .setEndArrow(HValueByType.getArrow(p, ctx, HPropName.END_ARROW).orNull())
                    .setLineStroke(g.createStroke(HValueByName.getStroke(p, ctx)))
                    .setLinePaint(fc)
            );
        }
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        Bounds2 b2 = new Bounds2(minx, miny, maxX, maxY);
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b2);
    }

}
