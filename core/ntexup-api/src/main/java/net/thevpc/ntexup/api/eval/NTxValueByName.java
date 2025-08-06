package net.thevpc.ntexup.api.eval;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.document.style.*;

import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxSizeRef;
import net.thevpc.ntexup.api.util.NtxFontInfo;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NNumberElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.util.Map;

public class NTxValueByName {
    public static NOptional<Paint> getColorProperty(String propName, NTxNode t, NTxNodeRendererContext ctx) {
        NTxValue r = NTxValue.of(ctx.computePropertyValue(t, propName).orElse(null));
        return NOptional.of(r.asColor().orElse(null));
    }

    protected static boolean isPreserveShapeRatio(NTxNode t, NTxNodeRendererContext ctx) {
        Boolean b = NTxValue.of(ctx.computePropertyValue(t, NTxPropName.PRESERVE_ASPECT_RATIO).orNull()).asBoolean().orElse(false);
        return b == null ? false : b;
    }

    private static NTxDouble2 getSize(NTxNode t, NTxDouble2 minSize, NTxNodeRendererContext ctx, NTxSizeRef hSizeRef) {
        if(hSizeRef==null) {
            hSizeRef = ctx.sizeRef();
        }
        NTxElemNumber2 double2OrHAlign = NTxValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NTxPropName.SIZE).orElse(
                new NTxElemNumber2((NNumberElement) NElement.ofDouble(100.0), (NNumberElement) NElement.ofDouble(100.0))
        );

        NTxDouble2 size = new NTxDouble2(
                hSizeRef.x(double2OrHAlign.getX()).get(),
                hSizeRef.y(double2OrHAlign.getY()).get()
        );

        boolean shapeRatio = isPreserveShapeRatio(t, ctx);
        //ratio depends on the smallest
        double sx = size.getX();
        double sy = size.getY();
        if (minSize != null) {
            sx = Math.max(minSize.getX(), sx);
            sy = Math.max(minSize.getY(), sy);
        }
        if (shapeRatio) {
            if (sx > sy) {
                sx = sy;
            }
            if (sy > sx) {
                sy = sx;
            }
            return new NTxDouble2(
                    sx,
                    sy
            );
        }
        return new NTxDouble2(
                sx,
                sy
        );
    }

    public static NTxDouble2 getOrigin(NTxNode t, NTxNodeRendererContext ctx, NTxDouble2 a) {
        NTxElemNumber2 double2OrHAlign = NTxValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NTxPropName.ORIGIN)
                .orElseUse(() -> NTxValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NTxPropName.AT))
                .orElse(new NTxElemNumber2(NElement.ofDouble(0), NElement.ofDouble(0)));
        NTxSizeRef sr = new NTxSizeRef(a.getX(), a.getY(), ctx.getGlobalBounds().getWidth(), ctx.getGlobalBounds().getHeight());
        return new NTxDouble2(
                sr.x(double2OrHAlign.getX()).get(),
                sr.y(double2OrHAlign.getY()).get()
        );
    }

    public static NTxDouble2 getPosition(NTxNode t, NTxNodeRendererContext ctx, NTxDouble2 a) {
        NTxElemNumber2 double2OrHAlign = NTxValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NTxPropName.POSITION)
                .orElseUse(() -> NTxValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NTxPropName.AT))
                .orElse(new NTxElemNumber2(NElement.ofDouble(0), NElement.ofDouble(0)));
        NTxSizeRef sr = new NTxSizeRef(a.getX(), a.getY(), ctx.getGlobalBounds().getWidth(), ctx.getGlobalBounds().getHeight());
        return new NTxDouble2(
                sr.x(double2OrHAlign.getX()).get(),
                sr.y(double2OrHAlign.getY()).get()
        );
    }

    public static NElement getStroke(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getElement(t, ctx, NTxPropName.STROKE).orNull();
    }

    public static NTxBounds2 selfBounds(NTxNode t, NTxDouble2 selfSize, NTxDouble2 minSize, NTxNodeRendererContext ctx) {
        NTxBounds2 parentBounds = ctx.getBounds();
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();
        NTxPadding padding = NTxValueByType.getPadding(t, ctx, NTxPropName.MARGIN).orElse(NTxPadding.of(0))
                .mul(pw / 100, ph / 100);
        NTxSizeRef hSizeRef = new NTxSizeRef(
                Math.max(pw - padding.getLeft() - padding.getRight(), 0),
                Math.max(ph - padding.getTop() - padding.getBottom(), 0),
                ctx.getGlobalBounds().getWidth(),
                ctx.getGlobalBounds().getHeight()
        );
        if (selfSize == null) {
            selfSize = getSize(t, minSize, ctx, hSizeRef);
        }

        NTxDouble2 pos = getPosition(t, ctx, new NTxDouble2(hSizeRef.getParentWidth(), hSizeRef.getParentHeight()));

        NTxDouble2 origin = getOrigin(t, ctx, selfSize);

        double x = pos.getX() - origin.getX() + parentBounds.getX();
        double y = pos.getY() - origin.getY() + parentBounds.getY();


        return new NTxBounds2(
                x + padding.getLeft(),
                y + padding.getTop(),
                selfSize.getX(),
                selfSize.getY()
        );
    }

    public static boolean isVisible(NTxNode t, NTxNodeRendererContext ctx) {
        NOptional<Boolean> b = NTxValueByType.getBoolean(t, ctx, NTxPropName.HIDE);
        if (b.isPresent()) {
            return !b.get();
        }
        b = NTxValueByType.getBoolean(t, ctx, "show");
        if (b.isPresent()) {
            return !b.get();
        }
        b = NTxValueByType.getBoolean(t, ctx, "visible");
        if (b.isPresent()) {
            return b.get();
        }
        return true;
    }

    public static double getFontSize(NTxNode t, NTxNodeRendererContext ctx) {
        NElement e = NTxValueByType.getElement(t, ctx, NTxPropName.FONT_SIZE).orNull();
        NTxSizeRef sr = ctx.sizeRef();
        NOptional<Double> srpx = sr.x(e);
        NOptional<Double> srpy = sr.y(e);
        return Math.min(srpx.orElse(16.0), srpy.orElse(16.0));
    }

    public static String getFontFamily(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getStringOrName(t, ctx, NTxPropName.FONT_FAMILY).orElse("Serif");
    }

    public static boolean isFontUnderlined(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.FONT_UNDERLINED, "underlined").orElse(false);
    }

    public static boolean isFontStrike(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.FONT_STRIKE, "strike").orElse(false);
    }

    public static boolean isFontBold(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.FONT_BOLD, "bold").orElse(false);
    }

    public static boolean isFontItalic(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.FONT_ITALIC, "italic").orElse(false);
    }

    public static Font getFont(NTxNode t, NTxNodeRendererContext ctx) {
        NElement e = NTxValueByType.getElement(t, ctx, NTxPropName.FONT_SIZE).orNull();
        NTxSizeRef sr = ctx.sizeRef();
        NOptional<Double> srpx = sr.x(e);
        NOptional<Double> srpy = sr.y(e);
        boolean fontItalic = isFontItalic(t, ctx);
        boolean fontBold = isFontBold(t, ctx);
        String fontFamily = getFontFamily(t, ctx);

        return NTxFontBySizeResolver.INSTANCE.getFont(fontFamily, Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0),
                srpx.orElse(16.0),
                srpy.orElse(16.0),
                ctx.graphics()
        );
    }
    public static NtxFontInfo getFontInfo(NTxNode t, NTxNodeRendererContext ctx) {
        NtxFontInfo f = new NtxFontInfo();
        f.size = NTxSize.ofElement(NTxValueByType.getElement(t, ctx, NTxPropName.FONT_SIZE).orNull());
        f.italic = NTxValueByType.getBoolean(t, ctx, NTxPropName.FONT_ITALIC).orNull();
        f.bold = NTxValueByType.getBoolean(t, ctx, NTxPropName.FONT_BOLD).orNull();
        f.family = NTxValueByType.getStringOrName(t, ctx, NTxPropName.FONT_FAMILY).orNull();
        return f;
    }

    public static NTxDouble2 getRoundCornerArcs(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValue.of(ctx.computePropertyValue(t, NTxPropName.ROUND_CORNER).orNull()).asDouble2OrDouble().orNull();
    }

    public static int getColSpan(NTxNode t, NTxNodeRendererContext ctx) {
        Integer i = NTxValueByType.getInt(t, ctx, NTxPropName.COLSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static int getRowSpan(NTxNode t, NTxNodeRendererContext ctx) {
        Integer i = NTxValueByType.getInt(t, ctx, NTxPropName.ROWSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static Boolean get3D(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.THEED).orElse(false);
    }

    public static Boolean getRaised(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.RAISED).orElse(false);
    }

    public static NOptional<NTxShadow> readStyleAsShadow(NTxNode t, String s, NTxNodeRendererContext ctx) {
        Object sv = ctx.computePropertyValue(t, s).orNull();
        NTxValue o = NTxValue.of(sv);
        NOptional<NTxPoint2D> r = getStyleAsShadowDistance(sv, ctx);
        if (r.isPresent()) {
            NTxShadow ss = new NTxShadow();
            ss.setTranslation(r.get());
            return NOptional.of(ss);
        }
        if (sv instanceof NElement && ((NElement) sv).isListContainer()) {
            NTxShadow shadow = new NTxShadow();
            for (Map.Entry<String, NTxValue> e : o.argsOrBodyMap().entrySet()) {
                switch (e.getKey()) {
                    case "distance":
                    case "shift":
                    case "origin": {
                        NOptional<NTxPoint2D> d = getStyleAsShadowDistance(e.getValue(), ctx);
                        if (d.isPresent()) {
                            shadow.setTranslation(d.get());
                        } else {
                            return (NOptional) d;
                        }
                        break;
                    }
                    case "shear": {
                        NOptional<NTxPoint2D> d = getStyleAsShadowDistance(e.getValue(), ctx);
                        if (d.isPresent()) {
                            shadow.setShear(d.get());
                        } else {
                            return (NOptional) d;
                        }
                        break;
                    }
                    case "color": {
                        NOptional<Color> d = e.getValue().asColor();
                        if (d.isPresent()) {
                            shadow.setColor(d.get());
                        } else {
                            return (NOptional) d;
                        }
                        break;
                    }
                    default: {
                        return NOptional.ofNamedEmpty("shadow");
                    }
                }
            }
            return NOptional.of(shadow);
        }
        NOptional<Boolean> rb = NTxValue.of(sv).asBoolean();
        if (rb.isPresent()) {
            if (rb.get()) {
                NTxShadow ss = new NTxShadow();
                ss.setTranslation(new NTxPoint2D(1, 1));
                return NOptional.of(ss);
            }
        }
        NOptional<Double> rd = NTxValue.of(sv).asDouble();
        if (r.isPresent()) {
            NTxShadow ss = new NTxShadow();
            ss.setTranslation(new NTxPoint2D(rd.get(), rd.get()));
            return NOptional.of(ss);
        }
        return NOptional.ofNamedEmpty("shadow");
    }

    public static Paint getForegroundColor(NTxNode t, NTxNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return null;
        }
        return NTxValueByType.getPaint(t, ctx, NTxPropName.FOREGROUND_COLOR, "foreground", "color", "fg").orElse(force ? Color.BLACK : null);
    }

    public static Paint resolveGridColor(NTxNode t, NTxNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        return NTxValueByType.getPaint(t, ctx, NTxPropName.GRID_COLOR).orElse(Color.BLACK);
    }

    public static Paint resolveBackgroundColor(NTxNode t, NTxNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        Paint color = NTxValueByType.getPaint(t, ctx, NTxPropName.BACKGROUND_COLOR, "background", "bg").orNull();
        return color;
    }

    public static boolean isDrawContour(NTxNode node, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(node, ctx, NTxPropName.DRAW_CONTOUR, "contour").orElse(false);
    }

    public static boolean requireDrawGrid(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.DRAW_GRID, "grid").orElse(false);
    }

    public static boolean requireFillBackground(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByType.getBoolean(t, ctx, NTxPropName.FILL_BACKGROUND, "fill").orElse(false);
    }

    public static int getColumns(NTxNode node, NTxNodeRendererContext ctx) {
        return NTxValueByType.getInt(node, ctx, NTxPropName.COLUMNS, "cols").orElse(-1);
    }

    public static int getRows(NTxNode node, NTxNodeRendererContext ctx) {
        return NTxValueByType.getInt(node, ctx, NTxPropName.ROWS, "rows").orElse(-1);
    }

    public static boolean isDebug(NTxNode p, NTxNodeRendererContext ctx) {
        return /*true || */getDebugLevel(p, ctx) > 0;
    }

    public static int getDebugLevel(NTxNode p, NTxNodeRendererContext ctx) {
        return NTxValueByType.getIntOrBoolean(p, ctx, NTxPropName.DEBUG).orElse(0);
    }

    public static Color getDebugColor(NTxNode t, NTxNodeRendererContext ctx) {
        return (Color) NTxValueByType.getPaint(t, ctx, NTxPropName.DEBUG_COLOR).orElse(Color.GRAY);
    }

    public static NOptional<NTxPoint2D> getStyleAsShadowDistance(Object sv, NTxNodeRendererContext ctx) {
        NTxValue o = NTxValue.of(sv);
        double ww = ctx.getBounds().getWidth();
        double hh = ctx.getBounds().getHeight();
        if (o.isNumber()) {
            NOptional<Number> n = o.asNumber();
            if (n.isPresent()) {
                return NOptional.of(new NTxPoint2D(
                        n.get().doubleValue() / 100.0 * ww,
                        n.get().doubleValue() / 100.0 * hh
                ));
            }
        } else {
            NOptional<NTxPoint2D> n = o.asHPoint2D();
            if (n.isPresent()) {
                return NOptional.of(new NTxPoint2D(
                        n.get().getX() / 100.0 * ww,
                        n.get().getY() / 100.0 * hh
                ));
            }
        }
        return NOptional.ofNamedEmpty("shadow-distance");
    }
}
