package net.thevpc.ntexup.engine.base.nodes.line;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.eval.NTxValueByType;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxUtils;

import java.awt.*;

public class NTxArcBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.ARC)
                .parseParam().named(NTxPropName.FROM, NTxPropName.TO).then()
                .renderComponent(this::renderMain);
    }


    public void renderMain(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxBounds2 b = ctx.selfBounds(p, null, null);
        double x = b.getX();
        double y = b.getY();
        double startAngle = NTxValueByType.getDouble(p,ctx, NTxPropName.FROM).orElse(0.0);
        double endAngle = NTxValueByType.getDouble(p,ctx, NTxPropName.TO).orElse(0.0);
        NTxGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            ctx.applyForeground(p , true);
            Stroke oldStroke=g.getStroke();
            Stroke stroke= ctx.resolveStroke(p);
            if(stroke!=null){
                g.setStroke(stroke);
            }
            g.drawArc((int) x, (int) y, NTxUtils.intOf(b.getWidth()), NTxUtils.intOf(b.getHeight()),
                    (int) startAngle,
                    (int) endAngle
            );
            g.setStroke(oldStroke);
        }
        ctx.paintDebugBox(p, b);
    }

//    public void renderMain(NTxNode p, NTxNodeRendererContext ctx) {
//        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        Bounds2 b = selfBounds(p, ctx);
//        NTxPoint2D translation = new NTxPoint2D(b.getX(), b.getY());
//        NTxPoint2D from = NTxPoint.ofParent(ObjEx.ofProp(p, NTxPropName.FROM).asNTxPoint2D().get()).valueNTxPoint2D(b, ctx.getGlobalBounds())
//                .plus(translation);
//        NTxPoint2D to = NTxPoint.ofParent(ObjEx.ofProp(p, NTxPropName.TO).asNTxPoint2D().get()).valueNTxPoint2D(b, ctx.getGlobalBounds())
//                .plus(translation);
//        NTxGraphics g = ctx.graphics();
//        double startAngle = (double) p.getPropertyValue(NTxPropName.START_ANGLE).orElse(0.0);
//        double endAngle = (double) p.getPropertyValue(NTxPropName.END_ANGLE).orElse(0.0);
//
//        if (!ctx.isDry()) {
//            Paint fc = ctx.getForegroundColor(p, true);
//            g.setPaint(fc);
//            ctx.applyStroke(p);
//            g.drawArc( from.getX(), from.getY(),
//                    to.getX(), to.getY(),
//                    startAngle,
//                    endAngle
//            );
////            g.draw2D(NTxElement2DFactory.line(from, to)
////                    .setStartArrow(NTxValueByType.getArrow(p, ctx, NTxPropName.START_ARROW).orNull())
////                    .setEndArrow(NTxValueByType.getArrow(p, ctx, NTxPropName.END_ARROW).orNull())
////                    .setLineStroke(g.createStroke(ctx.getStroke(p)))
////                    .setLinePaint(fc)
////            );
//        }
//        double minx = Math.min(from.getX(), to.getX());
//        double miny = Math.min(from.getY(), to.getY());
//        double maxX = Math.max(from.getX(), to.getX());
//        double maxY = Math.max(from.getY(), to.getY());
//        Bounds2 b2 = new Bounds2(minx, miny, maxX, maxY);
//        ctxs.paintDebugBox(p, b2);
//    }

}
