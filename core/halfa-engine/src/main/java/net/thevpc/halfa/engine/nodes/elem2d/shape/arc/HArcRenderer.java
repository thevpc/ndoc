package net.thevpc.halfa.engine.nodes.elem2d.shape.arc;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HElement2DFactory;
import net.thevpc.halfa.api.model.elem2d.HPoint;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.eval.HValueByType;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;

import java.awt.*;

public class HArcRenderer extends HNodeRendererBase {
    HProperties defaultStyles = new HProperties();
    public HArcRenderer() {
        super(HNodeType.ARC);
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = HValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double startAngle = HValueByType.getDouble(p,ctx,HPropName.FROM).orElse(0.0);
        double endAngle = HValueByType.getDouble(p,ctx,HPropName.TO).orElse(0.0);
        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            HNodeRendererUtils.applyForeground(p, g, ctx, true);
            Stroke oldStroke=g.getStroke();
            Stroke stroke=HNodeRendererUtils.resolveStroke(p, g, ctx);
            if(stroke!=null){
                g.setStroke(stroke);
            }
            g.drawArc((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()),
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
