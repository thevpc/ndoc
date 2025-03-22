package net.thevpc.ndoc.elem.base.line.arc;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.eval.NDocValueByType;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.awt.*;

public class NDocArcRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();
    public NDocArcRenderer() {
        super(HNodeType.ARC);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double startAngle = NDocValueByType.getDouble(p,ctx,HPropName.FROM).orElse(0.0);
        double endAngle = NDocValueByType.getDouble(p,ctx,HPropName.TO).orElse(0.0);
        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            HNodeRendererUtils.applyForeground(p, g, ctx, true);
            Stroke oldStroke=g.getStroke();
            Stroke stroke=HNodeRendererUtils.resolveStroke(p, g, ctx);
            if(stroke!=null){
                g.setStroke(stroke);
            }
            g.drawArc((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()),
                    (int) startAngle,
                    (int) endAngle
            );
            g.setStroke(oldStroke);
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

//    public void renderMain(HNode p, HNodeRendererContext ctx) {
//        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        Bounds2 b = selfBounds(p, ctx);
//        HPoint2D translation = new HPoint2D(b.getX(), b.getY());
//        HPoint2D from = HPoint.ofParent(ObjEx.ofProp(p, HPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
//                .plus(translation);
//        HPoint2D to = HPoint.ofParent(ObjEx.ofProp(p, HPropName.TO).asHPoint2D().get()).valueHPoint2D(b, ctx.getGlobalBounds())
//                .plus(translation);
//        HGraphics g = ctx.graphics();
//        double startAngle = (double) p.getPropertyValue(HPropName.START_ANGLE).orElse(0.0);
//        double endAngle = (double) p.getPropertyValue(HPropName.END_ANGLE).orElse(0.0);
//
//        if (!ctx.isDry()) {
//            Paint fc = HValueByName.getForegroundColor(p, ctx, true);
//            g.setPaint(fc);
//            HNodeRendererUtils.applyStroke(p, g, ctx);
//            g.drawArc( from.getX(), from.getY(),
//                    to.getX(), to.getY(),
//                    startAngle,
//                    endAngle
//            );
////            g.draw2D(HElement2DFactory.line(from, to)
////                    .setStartArrow(HValueByType.getArrow(p, ctx, HPropName.START_ARROW).orNull())
////                    .setEndArrow(HValueByType.getArrow(p, ctx, HPropName.END_ARROW).orNull())
////                    .setLineStroke(g.createStroke(HValueByName.getStroke(p, ctx)))
////                    .setLinePaint(fc)
////            );
//        }
//        double minx = Math.min(from.getX(), to.getX());
//        double miny = Math.min(from.getY(), to.getY());
//        double maxX = Math.max(from.getX(), to.getX());
//        double maxY = Math.max(from.getY(), to.getY());
//        Bounds2 b2 = new Bounds2(minx, miny, maxX, maxY);
//        HNodeRendererUtils.paintDebugBox(p, ctx, g, b2);
//    }

}
