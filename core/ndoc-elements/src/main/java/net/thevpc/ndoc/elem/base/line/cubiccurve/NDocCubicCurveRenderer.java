package net.thevpc.ndoc.elem.base.line.cubiccurve;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2DFactory;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.eval.NDocValueByType;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

import java.awt.*;

public class NDocCubicCurveRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocCubicCurveRenderer() {
        super(NDocNodeType.CUBIC_CURVE);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = selfBounds(p, ctx);
        NDocPoint2D translation = new NDocPoint2D(b.getX(), b.getY());
        NDocPoint2D from = NDocPoint.ofParent(NDocObjEx.ofProp(p, NDocPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        NDocPoint2D to = NDocPoint.ofParent(NDocObjEx.ofProp(p, NDocPropName.TO).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        NDocPoint2D ctrl1 = NDocPoint.ofParent(NDocObjEx.ofProp(p, NDocPropName.CTRL1).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        NDocPoint2D ctrl2 = NDocPoint.ofParent(NDocObjEx.ofProp(p, NDocPropName.CTRL2).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
                .plus(translation);
        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            Paint fc = NDocValueByName.getForegroundColor(p, ctx, true);
            g.draw2D(NDocElement2DFactory.cubic(from, ctrl1, ctrl2, to)
                    .setStartArrow(NDocValueByType.getArrow(p, ctx, NDocPropName.START_ARROW).orNull())
                    .setEndArrow(NDocValueByType.getArrow(p, ctx, NDocPropName.END_ARROW).orNull())
                    .setLineStroke(g.createStroke(NDocValueByName.getStroke(p, ctx)))
                    .setLinePaint(fc)
            );
        }
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        NDocBounds2 b2 = new NDocBounds2(minx, miny, maxX, maxY);
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b2);
    }

}
