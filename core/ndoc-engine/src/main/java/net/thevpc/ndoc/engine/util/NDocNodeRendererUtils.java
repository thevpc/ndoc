package net.thevpc.ndoc.engine.util;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.SizeD;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class NDocNodeRendererUtils {

    public static SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    public static Stroke resolveStroke(NDocNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        NElement strokeElem = NDocValueByName.getStroke(t, ctx);
        if (strokeElem != null) {
            return ctx.graphics().createStroke(strokeElem);
        }
        return null;
    }

    public static boolean withStroke(NDocNode t, NDocGraphics g, NDocNodeRendererContext ctx, Runnable r) {
        Stroke strokeElem = resolveStroke(t, g, ctx);
        if (strokeElem != null) {
            Stroke o = g.getStroke();
            g.setStroke(strokeElem);
            r.run();
            g.setStroke(o);
            return true;
        }else{
            r.run();
            return false;
        }
    }

    public static boolean applyStroke(NDocNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        Stroke strokeElem = resolveStroke(t, g, ctx);
        if (strokeElem != null) {
            g.setStroke(strokeElem);
            return true;
        }
        return false;
    }

    public static void applyFont(NDocNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        g.setFont(NDocValueByName.getFont(t, ctx));
    }

    public static SizeD mapDim(double w, double h, NDocNodeRendererContext ctx) {
        NDocBounds2 size = ctx.getBounds();
        return new SizeD(w / 100 * size.getWidth(), w / 100 * size.getHeight());
    }

    public static NDocBounds2 bounds(NDocNode t, NDocNodeRendererContext ctx) {
        NDocValue oSize = NDocValue.of(ctx.computePropertyValue(t, NDocPropName.SIZE));
        NOptional<NElement[]> a = oSize.asElementArray();
        NDocDouble2 size=null;
        if(a.isPresent()){
            NElement[] tt = a.get();
            switch (tt.length){
                case 1:{
                    size=new NDocDouble2(
                            ctx.sizeRef().x(tt[0]).orElse(100.0),
                            ctx.sizeRef().y(tt[0]).orElse(100.0)
                    );
                    break;
                }
                case 2:{
                    size=new NDocDouble2(
                            ctx.sizeRef().x(tt[0]).orElse(100.0),
                            ctx.sizeRef().y(tt[1]).orElse(100.0)
                    );
                    break;
                }
            }
        }
        if (size == null) {
            size = new NDocDouble2(ctx.getBounds().getWidth(), ctx.getBounds().getHeight());
        }
        return new NDocBounds2(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                size.getX(),
                size.getY()
        );
    }

    public static boolean applyForeground(NDocNode t, NDocGraphics g, NDocNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint fg = NDocValueByName.getForegroundColor(t, ctx,force);
        if (fg != null) {
            g.setPaint(fg);
            return true;
        }
        return false;
    }

    public static boolean applyBackgroundColor(NDocNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return false;
        }
        Paint bg = NDocValueByName.resolveBackgroundColor(t, ctx);
        if (bg != null) {
            g.setPaint(bg);
            return true;
        }
        return false;
    }

    public static boolean applyGridColor(NDocNode t, NDocGraphics g, NDocNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint color = NDocValueByName.resolveGridColor(t, ctx);
        if (color != null) {
            g.setPaint(color);
            return true;
        }
        if (force) {
            g.setColor(Color.gray);
            return true;
        }
        return false;
    }

//    public static boolean applyLineColor(NDocNode t, HGraphics g, NDocNodeRendererContext ctx, boolean force) {
//        if (ctx.isDry()) {
//            return false;
//        }
//        Paint color = NDocPropValueByNameParser.resolveForegroundColor(t, ctx);
//        if (color != null) {
//            g.setPaint(color);
//            return true;
//        }
//        if (force) {
//            //would resolve default color instead ?
//            g.setPaint(Color.BLACK);
//            return true;
//        }
//        return false;
//    }

    public static void paintDebugBox(NDocNode t, NDocNodeRendererContext ctx, NDocGraphics g, NDocBounds2 a, boolean force) {
        if (ctx.isDry()) {
            return;
        }
        if (force || NDocValueByName.isDebug(t, ctx)) {
            g.setColor(NDocValueByName.getDebugColor(t, ctx));
            g.drawRect(
                    NDocUtils.doubleOf(a.getMinX()), NDocUtils.doubleOf(a.getMinY()),
                    NDocUtils.doubleOf(a.getWidth()), NDocUtils.doubleOf(a.getHeight())
            );
            NDocDouble2 origin = NDocValueByName.getOrigin(t, ctx,new NDocDouble2(a.getWidth(),a.getHeight()));
            double x = origin.getX() + a.getX();
            double y = origin.getY() + a.getY();
            g.setColor(NDocValueByName.getDebugColor(t, ctx));
            int originSize = 6;
            g.fillOval(
                    x - originSize / 2, y - originSize / 2,
                    originSize, originSize
            );
        }
    }

    public static void paintDebugBox(NDocNode t, NDocNodeRendererContext ctx, NDocGraphics g, NDocBounds2 a) {
        paintDebugBox(t, ctx, g, a, false);
    }

    public static NOptional<Color> colorFromPaint(Paint p) {
        if(p instanceof Color){
            return NOptional.of((Color) p);
        }
        return NOptional.ofNamedEmpty("color");
    }

    public static void paintBorderLine(NDocNode t, NDocNodeRendererContext ctx, NDocGraphics g, NDocBounds2 a) {
        if (ctx.isDry()) {
            return;
        }
        paintDebugBox(t, ctx, g, a);
        if (NDocValueByName.isDrawContour(t, ctx)) {
            if (applyForeground(t, g, ctx, true)) {
                Stroke s = g.getStroke();
                applyStroke(t, g, ctx);
                g.drawRect(
                        NDocUtils.intOf(a.getMinX()), NDocUtils.intOf(a.getMinY()),
                        NDocUtils.intOf(a.getWidth()), NDocUtils.intOf(a.getHeight())
                );
                g.setStroke(s);
            }
        }

    }

    public static void paintBackground(NDocNode t, NDocNodeRendererContext ctx, NDocGraphics g, NDocBounds2 a) {
        if (ctx.isDry()) {
            return;
        }
//        if (HPropValueByNameParser.requireFillBackground(t, ctx)) {
        if (applyBackgroundColor(t, g, ctx)) {
            g.fillRect(a);
        }
//        }
    }
}
