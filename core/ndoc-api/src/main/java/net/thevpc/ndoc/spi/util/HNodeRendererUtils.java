package net.thevpc.ndoc.spi.util;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.elem2d.SizeD;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class HNodeRendererUtils {

    public static SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    public static Stroke resolveStroke(HNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        NElement strokeElem = NDocValueByName.getStroke(t, ctx);
        if (strokeElem != null) {
            return ctx.graphics().createStroke(strokeElem);
        }
        return null;
    }

    public static boolean withStroke(HNode t, NDocGraphics g, NDocNodeRendererContext ctx, Runnable r) {
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

    public static boolean applyStroke(HNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        Stroke strokeElem = resolveStroke(t, g, ctx);
        if (strokeElem != null) {
            g.setStroke(strokeElem);
            return true;
        }
        return false;
    }

    public static void applyFont(HNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        g.setFont(NDocValueByName.getFont(t, ctx));
    }

    public static SizeD mapDim(double w, double h, NDocNodeRendererContext ctx) {
        Bounds2 size = ctx.getBounds();
        return new SizeD(w / 100 * size.getWidth(), w / 100 * size.getHeight());
    }

    public static Bounds2 bounds(HNode t, NDocNodeRendererContext ctx) {
        NDocObjEx oSize = NDocObjEx.of(ctx.computePropertyValue(t, HPropName.SIZE));
        NOptional<NElement[]> a = oSize.asNArrayElement();
        Double2 size=null;
        if(a.isPresent()){
            NElement[] tt = a.get();
            switch (tt.length){
                case 1:{
                    size=new Double2(
                            ctx.sizeRef().x(tt[0]).orElse(100.0),
                            ctx.sizeRef().y(tt[0]).orElse(100.0)
                    );
                    break;
                }
                case 2:{
                    size=new Double2(
                            ctx.sizeRef().x(tt[0]).orElse(100.0),
                            ctx.sizeRef().y(tt[1]).orElse(100.0)
                    );
                    break;
                }
            }
        }
        if (size == null) {
            size = new Double2(ctx.getBounds().getWidth(), ctx.getBounds().getHeight());
        }
        return new Bounds2(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                size.getX(),
                size.getY()
        );
    }

    public static boolean applyForeground(HNode t, NDocGraphics g, NDocNodeRendererContext ctx, boolean force) {
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

    public static boolean applyBackgroundColor(HNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
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

    public static boolean applyGridColor(HNode t, NDocGraphics g, NDocNodeRendererContext ctx, boolean force) {
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

//    public static boolean applyLineColor(HNode t, HGraphics g, HNodeRendererContext ctx, boolean force) {
//        if (ctx.isDry()) {
//            return false;
//        }
//        Paint color = HPropValueByNameParser.resolveForegroundColor(t, ctx);
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

    public static void paintDebugBox(HNode t, NDocNodeRendererContext ctx, NDocGraphics g, Bounds2 a, boolean force) {
        if (ctx.isDry()) {
            return;
        }
        if (force || NDocValueByName.isDebug(t, ctx)) {
            g.setColor(NDocValueByName.getDebugColor(t, ctx));
            g.drawRect(
                    net.thevpc.ndoc.api.util.HUtils.doubleOf(a.getMinX()), net.thevpc.ndoc.api.util.HUtils.doubleOf(a.getMinY()),
                    net.thevpc.ndoc.api.util.HUtils.doubleOf(a.getWidth()), net.thevpc.ndoc.api.util.HUtils.doubleOf(a.getHeight())
            );
            Double2 origin = NDocValueByName.getOrigin(t, ctx,new Double2(a.getWidth(),a.getHeight()));
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

    public static void paintDebugBox(HNode t, NDocNodeRendererContext ctx, NDocGraphics g, Bounds2 a) {
        paintDebugBox(t, ctx, g, a, false);
    }

    public static NOptional<Color> colorFromPaint(Paint p) {
        if(p instanceof Color){
            return NOptional.of((Color) p);
        }
        return NOptional.ofNamedEmpty("color");
    }

    public static void paintBorderLine(HNode t, NDocNodeRendererContext ctx, NDocGraphics g, Bounds2 a) {
        if (ctx.isDry()) {
            return;
        }
        paintDebugBox(t, ctx, g, a);
        if (NDocValueByName.isDrawContour(t, ctx)) {
            if (applyForeground(t, g, ctx, true)) {
                Stroke s = g.getStroke();
                applyStroke(t, g, ctx);
                g.drawRect(
                        net.thevpc.ndoc.api.util.HUtils.intOf(a.getMinX()), net.thevpc.ndoc.api.util.HUtils.intOf(a.getMinY()),
                        net.thevpc.ndoc.api.util.HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
                );
                g.setStroke(s);
            }
        }

    }

    public static void paintBackground(HNode t, NDocNodeRendererContext ctx, NDocGraphics g, Bounds2 a) {
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
