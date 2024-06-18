package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.SizeD;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.renderer.screen.common.strokes.StrokeFactory;
import net.thevpc.halfa.spi.nodes.HPropValueByNameParser;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.tson.TsonElement;

import java.awt.*;

public class HNodeRendererUtils {

    public static SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    public static Stroke resolveStroke(HNode t, HGraphics g, HNodeRendererContext ctx) {
        TsonElement strokeElem = HPropValueByNameParser.getStroke(t,ctx);
        if (strokeElem != null) {
            return StrokeFactory.createStroke(strokeElem);
        }
        return null;
    }

    public static boolean applyStroke(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Stroke strokeElem = resolveStroke(t, g, ctx);
        if (strokeElem != null) {
            g.setStroke(strokeElem);
            return true;
        }
        return false;
    }

    public static void applyFont(HNode t, HGraphics g, HNodeRendererContext ctx) {
        g.setFont(HPropValueByNameParser.getFont(t, ctx));
    }

    public static SizeD mapDim(double w, double h, HNodeRendererContext ctx) {
        Bounds2 size = ctx.getBounds();
        return new SizeD(w / 100 * size.getWidth(), w / 100 * size.getHeight());
    }

    public static Bounds2 bounds(HNode t, HNodeRendererContext ctx) {
        Double2 size = ObjEx.of(ctx.computePropertyValue(t, HPropName.SIZE).orElse(new Double2(100, 100))).asDouble2().orNull();
        if (size == null) {
            size = new Double2(100, 100);
        }
        return new Bounds2(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                size.getX() / 100 * ctx.getBounds().getWidth(),
                size.getY() / 100 * ctx.getBounds().getHeight()
        );
    }

    public static boolean applyForeground(HNode t, HGraphics g, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return false;
        }
        Paint fg = HPropValueByNameParser.resolveForegroundColor(t, ctx);
        if (fg == null) {
            fg = Color.BLACK;
        }
        g.setPaint(fg);
        return true;
    }

    public static boolean applyBackgroundColor(HNode t, HGraphics g, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return false;
        }
        Paint bg = HPropValueByNameParser.resolveBackgroundColor(t, ctx);
        if (bg != null) {
            g.setPaint(bg);
            return true;
        }
        return false;
    }

    public static boolean applyGridColor(HNode t, HGraphics g, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint color = HPropValueByNameParser.resolveGridColor(t, ctx);
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

    public static boolean applyLineColor(HNode t, HGraphics g, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint color = HPropValueByNameParser.resolveLineColor(t, ctx);
        if (color != null) {
            g.setPaint(color);
            return true;
        }
        if (force) {
            //would resolve default color instead ?
            g.setPaint(Color.BLACK);
            return true;
        }
        return false;
    }

    public static void paintDebugBox(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a, boolean force) {
        if (ctx.isDry()) {
            return;
        }
        if (force || HPropValueByNameParser.isDebug(t, ctx)) {
            g.setColor(HPropValueByNameParser.getDebugColor(t, ctx));
            g.drawRect(
                    HUtils.doubleOf(a.getMinX()), HUtils.doubleOf(a.getMinY()),
                    HUtils.doubleOf(a.getWidth()), HUtils.doubleOf(a.getHeight())
            );
            Double2 origin = HPropValueByNameParser.getOrigin(t, ctx);
            double x = origin.getX() / 100 * a.getWidth() + a.getX();
            double y = origin.getY() / 100 * a.getHeight() + a.getY();
            g.setColor(HPropValueByNameParser.getDebugColor(t, ctx));
            int originSize = 6;
            g.fillOval(
                    x - originSize / 2, y - originSize / 2,
                    originSize, originSize
            );
        }
    }

    public static void paintDebugBox(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a) {
        paintDebugBox(t, ctx, g, a,false);
    }

    public static void paintBorderLine(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a) {
        if (ctx.isDry()) {
            return;
        }
        paintDebugBox(t, ctx, g, a);
        if (HPropValueByNameParser.requireDrawContour(t, ctx)) {
            if (applyLineColor(t, g, ctx, true)) {
                Stroke s = g.getStroke();
                applyStroke(t, g, ctx);
                g.drawRect(
                        HUtils.intOf(a.getMinX()), HUtils.intOf(a.getMinY()),
                        HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
                );
                g.setStroke(s);
            }
        }

    }

    public static void paintBackground(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a) {
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
