package net.thevpc.ndoc.elem.base.line.cubiccurve;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.HElement2DFactory;
import net.thevpc.ndoc.api.model.elem2d.HPoint;
import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.eval.NDocValueByType;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.awt.*;

public class NDocCubicCurveRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public NDocCubicCurveRenderer() {
        super(HNodeType.CUBIC_CURVE);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        HPoint2D translation = new HPoint2D(b.getX(), b.getY());
        HPoint2D from = HPoint.ofParent(NDocObjEx.ofProp(p, HPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        HPoint2D to = HPoint.ofParent(NDocObjEx.ofProp(p, HPropName.TO).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        HPoint2D ctrl1 = HPoint.ofParent(NDocObjEx.ofProp(p, HPropName.CTRL1).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        HPoint2D ctrl2 = HPoint.ofParent(NDocObjEx.ofProp(p, HPropName.CTRL2).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint fc = NDocValueByName.getForegroundColor(p, ctx, true);
            g.draw2D(HElement2DFactory.cubic(from, ctrl1, ctrl2, to)
                    .setStartArrow(NDocValueByType.getArrow(p, ctx, HPropName.START_ARROW).orNull())
                    .setEndArrow(NDocValueByType.getArrow(p, ctx, HPropName.END_ARROW).orNull())
                    .setLineStroke(g.createStroke(NDocValueByName.getStroke(p, ctx)))
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
