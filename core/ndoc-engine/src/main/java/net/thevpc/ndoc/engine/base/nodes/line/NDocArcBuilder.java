package net.thevpc.ndoc.engine.base.nodes.line;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByType;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocUtils;

import java.awt.*;

public class NDocArcBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.ARC)
                .parseParam().named(NDocPropName.FROM,NDocPropName.TO).then()
                .renderComponent(this::renderMain);
    }


    public void renderMain(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = ctx.selfBounds(p, null, null);
        double x = b.getX();
        double y = b.getY();
        double startAngle = NDocValueByType.getDouble(p,ctx, NDocPropName.FROM).orElse(0.0);
        double endAngle = NDocValueByType.getDouble(p,ctx, NDocPropName.TO).orElse(0.0);
        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            ctx.applyForeground(p , true);
            Stroke oldStroke=g.getStroke();
            Stroke stroke= ctx.resolveStroke(p);
            if(stroke!=null){
                g.setStroke(stroke);
            }
            g.drawArc((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()),
                    (int) startAngle,
                    (int) endAngle
            );
            g.setStroke(oldStroke);
        }
        ctx.paintDebugBox(p, b);
    }

//    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
//        ctx = ctx.withDefaultStyles(p, defaultStyles);
//        Bounds2 b = selfBounds(p, ctx);
//        NDocPoint2D translation = new NDocPoint2D(b.getX(), b.getY());
//        NDocPoint2D from = NDocPoint.ofParent(ObjEx.ofProp(p, NDocPropName.FROM).asNDocPoint2D().get()).valueNDocPoint2D(b, ctx.getGlobalBounds())
//                .plus(translation);
//        NDocPoint2D to = NDocPoint.ofParent(ObjEx.ofProp(p, NDocPropName.TO).asNDocPoint2D().get()).valueNDocPoint2D(b, ctx.getGlobalBounds())
//                .plus(translation);
//        NDocGraphics g = ctx.graphics();
//        double startAngle = (double) p.getPropertyValue(NDocPropName.START_ANGLE).orElse(0.0);
//        double endAngle = (double) p.getPropertyValue(NDocPropName.END_ANGLE).orElse(0.0);
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
////            g.draw2D(NDocElement2DFactory.line(from, to)
////                    .setStartArrow(NDocValueByType.getArrow(p, ctx, NDocPropName.START_ARROW).orNull())
////                    .setEndArrow(NDocValueByType.getArrow(p, ctx, NDocPropName.END_ARROW).orNull())
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
