package net.thevpc.ntexup.engine.util;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.elem2d.NTxSizeD;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.eval.NDocValueByName;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class NDocNodeRendererUtils {

    public static NTxSizeD mapDim(NTxSizeD d, NTxSizeD base) {
        return new NTxSizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    public static Stroke resolveStroke(NTxNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        NElement strokeElem = NDocValueByName.getStroke(t, ctx);
        if (strokeElem != null) {
            return ctx.graphics().createStroke(strokeElem);
        }
        return null;
    }

    public static boolean withStroke(NTxNode t, NDocGraphics g, NDocNodeRendererContext ctx, Runnable r) {
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

    public static boolean applyStroke(NTxNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        Stroke strokeElem = resolveStroke(t, g, ctx);
        if (strokeElem != null) {
            g.setStroke(strokeElem);
            return true;
        }
        return false;
    }

    public static void applyFont(NTxNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
        g.setFont(NDocValueByName.getFont(t, ctx));
    }

    public static NTxSizeD mapDim(double w, double h, NDocNodeRendererContext ctx) {
        NTxBounds2 size = ctx.getBounds();
        return new NTxSizeD(w / 100 * size.getWidth(), w / 100 * size.getHeight());
    }

    public static NTxBounds2 bounds(NTxNode t, NDocNodeRendererContext ctx) {
        NDocValue oSize = NDocValue.of(ctx.computePropertyValue(t, NTxPropName.SIZE));
        NOptional<NElement[]> a = oSize.asElementArray();
        NTxDouble2 size=null;
        if(a.isPresent()){
            NElement[] tt = a.get();
            switch (tt.length){
                case 1:{
                    size=new NTxDouble2(
                            ctx.sizeRef().x(tt[0]).orElse(100.0),
                            ctx.sizeRef().y(tt[0]).orElse(100.0)
                    );
                    break;
                }
                case 2:{
                    size=new NTxDouble2(
                            ctx.sizeRef().x(tt[0]).orElse(100.0),
                            ctx.sizeRef().y(tt[1]).orElse(100.0)
                    );
                    break;
                }
            }
        }
        if (size == null) {
            size = new NTxDouble2(ctx.getBounds().getWidth(), ctx.getBounds().getHeight());
        }
        return new NTxBounds2(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                size.getX(),
                size.getY()
        );
    }

    public static boolean applyForeground(NTxNode t, NDocGraphics g, NDocNodeRendererContext ctx, boolean force) {
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

    public static boolean applyBackgroundColor(NTxNode t, NDocGraphics g, NDocNodeRendererContext ctx) {
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

    public static boolean applyGridColor(NTxNode t, NDocGraphics g, NDocNodeRendererContext ctx, boolean force) {
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

    public static void paintDebugBox(NTxNode t, NDocNodeRendererContext ctx, NDocGraphics g, NTxBounds2 a, boolean force) {
        if (ctx.isDry()) {
            return;
        }
        if (force || NDocValueByName.isDebug(t, ctx)) {
            g.setColor(NDocValueByName.getDebugColor(t, ctx));
            g.drawRect(
                    NDocUtils.doubleOf(a.getMinX()), NDocUtils.doubleOf(a.getMinY()),
                    NDocUtils.doubleOf(a.getWidth()), NDocUtils.doubleOf(a.getHeight())
            );
            NTxDouble2 origin = NDocValueByName.getOrigin(t, ctx,new NTxDouble2(a.getWidth(),a.getHeight()));
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

    public static void paintDebugBox(NTxNode t, NDocNodeRendererContext ctx, NDocGraphics g, NTxBounds2 a) {
        paintDebugBox(t, ctx, g, a, false);
    }

    public static NOptional<Color> colorFromPaint(Paint p) {
        if(p instanceof Color){
            return NOptional.of((Color) p);
        }
        return NOptional.ofNamedEmpty("color");
    }

    public static void paintBorderLine(NTxNode t, NDocNodeRendererContext ctx, NDocGraphics g, NTxBounds2 a) {
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

    public static void paintBackground(NTxNode t, NDocNodeRendererContext ctx, NDocGraphics g, NTxBounds2 a) {
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
