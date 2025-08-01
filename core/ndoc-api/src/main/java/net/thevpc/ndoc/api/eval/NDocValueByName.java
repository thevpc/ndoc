package net.thevpc.ndoc.api.eval;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.*;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.*;

import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NNumberElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.util.Map;

public class NDocValueByName {
    public static NOptional<Paint> getColorProperty(String propName, NDocNode t, NDocNodeRendererContext ctx) {
        NDocObjEx r = NDocObjEx.of(ctx.computePropertyValue(t, propName).orElse(null));
        return NOptional.of(r.asColor().orElse(null));
    }

    protected static boolean isPreserveShapeRatio(NDocNode t, NDocNodeRendererContext ctx) {
        Boolean b = NDocObjEx.of(ctx.computePropertyValue(t, NDocPropName.PRESERVE_ASPECT_RATIO).orNull()).asBoolean().orElse(false);
        return b == null ? false : b;
    }

    private static NDocDouble2 getSize(NDocNode t, NDocDouble2 minSize, NDocNodeRendererContext ctx, NDocSizeRef hSizeRef) {
        if(hSizeRef==null) {
            hSizeRef = ctx.sizeRef();
        }
        NDocElemNumber2 double2OrHAlign = NDocValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NDocPropName.SIZE).orElse(
                new NDocElemNumber2((NNumberElement) NElement.ofDouble(100.0), (NNumberElement) NElement.ofDouble(100.0))
        );

        NDocDouble2 size = new NDocDouble2(
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
            return new NDocDouble2(
                    sx,
                    sy
            );
        }
        return new NDocDouble2(
                sx,
                sy
        );
    }

    public static NDocDouble2 getOrigin(NDocNode t, NDocNodeRendererContext ctx, NDocDouble2 a) {
        NDocElemNumber2 double2OrHAlign = NDocValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NDocPropName.ORIGIN)
                .orElseUse(() -> NDocValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NDocPropName.AT))
                .orElse(new NDocElemNumber2(NElement.ofDouble(0), NElement.ofDouble(0)));
        NDocSizeRef sr = new NDocSizeRef(a.getX(), a.getY(), ctx.getGlobalBounds().getWidth(), ctx.getGlobalBounds().getHeight());
        return new NDocDouble2(
                sr.x(double2OrHAlign.getX()).get(),
                sr.y(double2OrHAlign.getY()).get()
        );
    }

    public static NDocDouble2 getPosition(NDocNode t, NDocNodeRendererContext ctx, NDocDouble2 a) {
        NDocElemNumber2 double2OrHAlign = NDocValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NDocPropName.POSITION)
                .orElseUse(() -> NDocValueByType.getNNumberElement2Or1OrHAlign(t, ctx, NDocPropName.AT))
                .orElse(new NDocElemNumber2(NElement.ofDouble(0), NElement.ofDouble(0)));
        NDocSizeRef sr = new NDocSizeRef(a.getX(), a.getY(), ctx.getGlobalBounds().getWidth(), ctx.getGlobalBounds().getHeight());
        return new NDocDouble2(
                sr.x(double2OrHAlign.getX()).get(),
                sr.y(double2OrHAlign.getY()).get()
        );
    }

    public static NElement getStroke(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getElement(t, ctx, NDocPropName.STROKE).orNull();
    }

    public static NDocBounds2 selfBounds(NDocNode t, NDocDouble2 selfSize, NDocDouble2 minSize, NDocNodeRendererContext ctx) {
        NDocBounds2 parentBounds = ctx.getBounds();
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();
        Padding padding = NDocValueByType.getPadding(t, ctx, NDocPropName.MARGIN).orElse(Padding.of(0))
                .mul(pw / 100, ph / 100);
        NDocSizeRef hSizeRef = new NDocSizeRef(
                Math.max(pw - padding.getLeft() - padding.getRight(), 0),
                Math.max(ph - padding.getTop() - padding.getBottom(), 0),
                ctx.getGlobalBounds().getWidth(),
                ctx.getGlobalBounds().getHeight()
        );
        if (selfSize == null) {
            selfSize = getSize(t, minSize, ctx, hSizeRef);
        }

        NDocDouble2 pos = getPosition(t, ctx, new NDocDouble2(hSizeRef.getParentWidth(), hSizeRef.getParentHeight()));

        NDocDouble2 origin = getOrigin(t, ctx, selfSize);

        double x = pos.getX() - origin.getX() + parentBounds.getX();
        double y = pos.getY() - origin.getY() + parentBounds.getY();


        return new NDocBounds2(
                x + padding.getLeft(),
                y + padding.getTop(),
                selfSize.getX(),
                selfSize.getY()
        );
    }

    public static boolean isVisible(NDocNode t, NDocNodeRendererContext ctx) {
        NOptional<Boolean> b = NDocValueByType.getBoolean(t, ctx, NDocPropName.HIDE);
        if (b.isPresent()) {
            return !b.get();
        }
        b = NDocValueByType.getBoolean(t, ctx, "show");
        if (b.isPresent()) {
            return !b.get();
        }
        b = NDocValueByType.getBoolean(t, ctx, "visible");
        if (b.isPresent()) {
            return b.get();
        }
        return true;
    }

    public static double getFontSize(NDocNode t, NDocNodeRendererContext ctx) {
        NElement e = NDocValueByType.getElement(t, ctx, NDocPropName.FONT_SIZE).orNull();
        NDocSizeRef sr = ctx.sizeRef();
        NOptional<Double> srpx = sr.x(e);
        NOptional<Double> srpy = sr.y(e);
        return Math.min(srpx.orElse(16.0), srpy.orElse(16.0));
    }

    public static String getFontFamily(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getStringOrName(t, ctx, NDocPropName.FONT_FAMILY).orElse("Serif");
    }

    public static boolean isFontUnderlined(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.FONT_UNDERLINED, "underlined").orElse(false);
    }

    public static boolean isFontStrike(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.FONT_STRIKE, "strike").orElse(false);
    }

    public static boolean isFontBold(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.FONT_BOLD, "bold").orElse(false);
    }

    public static boolean isFontItalic(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.FONT_ITALIC, "italic").orElse(false);
    }

    public static Font getFont(NDocNode t, NDocNodeRendererContext ctx) {
        double fontSize = getFontSize(t, ctx);
        boolean fontItalic = isFontItalic(t, ctx);
        boolean fontBold = isFontBold(t, ctx);
        String fontFamily = getFontFamily(t, ctx);

        return NDocFontBySizeResolver.INSTANCE.getFont(fontFamily, Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), fontSize,
                f -> ctx.graphics().getFontMetrics(f)
        );
    }

    public static NDocDouble2 getRoundCornerArcs(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocObjEx.of(ctx.computePropertyValue(t, NDocPropName.ROUND_CORNER).orNull()).asDouble2().orNull();
    }

    public static int getColSpan(NDocNode t, NDocNodeRendererContext ctx) {
        Integer i = NDocValueByType.getInt(t, ctx, NDocPropName.COLSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static int getRowSpan(NDocNode t, NDocNodeRendererContext ctx) {
        Integer i = NDocValueByType.getInt(t, ctx, NDocPropName.ROWSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static Boolean get3D(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.THEED).orElse(false);
    }

    public static Boolean getRaised(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.RAISED).orElse(false);
    }

    public static NOptional<Shadow> readStyleAsShadow(NDocNode t, String s, NDocNodeRendererContext ctx) {
        Object sv = ctx.computePropertyValue(t, s).orNull();
        NDocObjEx o = NDocObjEx.of(sv);
        NOptional<NDocPoint2D> r = getStyleAsShadowDistance(sv, ctx);
        if (r.isPresent()) {
            Shadow ss = new Shadow();
            ss.setTranslation(r.get());
            return NOptional.of(ss);
        }
        if (sv instanceof NElement && ((NElement) sv).isListContainer()) {
            Shadow shadow = new Shadow();
            for (Map.Entry<String, NDocObjEx> e : o.argsOrBodyMap().entrySet()) {
                switch (e.getKey()) {
                    case "distance":
                    case "shift":
                    case "origin": {
                        NOptional<NDocPoint2D> d = getStyleAsShadowDistance(e.getValue(), ctx);
                        if (d.isPresent()) {
                            shadow.setTranslation(d.get());
                        } else {
                            return (NOptional) d;
                        }
                        break;
                    }
                    case "shear": {
                        NOptional<NDocPoint2D> d = getStyleAsShadowDistance(e.getValue(), ctx);
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
        NOptional<Boolean> rb = NDocObjEx.of(sv).asBoolean();
        if (rb.isPresent()) {
            if (rb.get()) {
                Shadow ss = new Shadow();
                ss.setTranslation(new NDocPoint2D(1, 1));
                return NOptional.of(ss);
            }
        }
        NOptional<Double> rd = NDocObjEx.of(sv).asDouble();
        if (r.isPresent()) {
            Shadow ss = new Shadow();
            ss.setTranslation(new NDocPoint2D(rd.get(), rd.get()));
            return NOptional.of(ss);
        }
        return NOptional.ofNamedEmpty("shadow");
    }

    public static Paint getForegroundColor(NDocNode t, NDocNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return null;
        }
        return NDocValueByType.getPaint(t, ctx, NDocPropName.FOREGROUND_COLOR, "foreground", "color", "fg").orElse(force ? Color.BLACK : null);
    }

    public static Paint resolveGridColor(NDocNode t, NDocNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        return NDocValueByType.getPaint(t, ctx, NDocPropName.GRID_COLOR).orElse(Color.BLACK);
    }

//    public static Paint resolveLineColor(NDocNode t, NDocNodeRendererContext ctx) {
//        if (ctx.isDry()) {
//            return null;
//        }
//        return NDocValueTypeParser.getPaint(t, ctx, NDocPropName.LINE_COLOR).orNull();
//    }

    public static Paint resolveBackgroundColor(NDocNode t, NDocNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        Paint color = NDocValueByType.getPaint(t, ctx, NDocPropName.BACKGROUND_COLOR, "background", "bg").orNull();
        return color;
    }

    public static boolean isDrawContour(NDocNode node, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(node, ctx, NDocPropName.DRAW_CONTOUR, "contour").orElse(false);
    }

    public static boolean requireDrawGrid(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.DRAW_GRID, "grid").orElse(false);
    }

    public static boolean requireFillBackground(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByType.getBoolean(t, ctx, NDocPropName.FILL_BACKGROUND, "fill").orElse(false);
    }

//    public static Paint resolveLineColor(NDocNode t, NDocNodeRendererContext ctx, boolean force) {
//        if (ctx.isDry()) {
//            return null;
//        }
//        Paint color = HValueTypeParser.getPaint(t, ctx, NDocPropName.LINE_COLOR).orNull();
//        if (color != null) {
//            return color;
//        }
//        if (force) {
//            //would resolve default color instead ?
//            Color cc = ctx.graphics().getColor();
//            if (cc == null) {
//                return Color.BLACK;
//            }
//            return cc;
//        }
//        return null;
//    }

    public static int getColumns(NDocNode node, NDocNodeRendererContext ctx) {
        return NDocValueByType.getInt(node, ctx, NDocPropName.COLUMNS, "cols").orElse(-1);
    }

    public static int getRows(NDocNode node, NDocNodeRendererContext ctx) {
        return NDocValueByType.getInt(node, ctx, NDocPropName.ROWS, "rows").orElse(-1);
    }

    public static boolean isDebug(NDocNode p, NDocNodeRendererContext ctx) {
        return /*true || */getDebugLevel(p, ctx) > 0;
    }

    public static int getDebugLevel(NDocNode p, NDocNodeRendererContext ctx) {
        return NDocValueByType.getIntOrBoolean(p, ctx, NDocPropName.DEBUG).orElse(0);
    }

    public static Color getDebugColor(NDocNode t, NDocNodeRendererContext ctx) {
        return (Color) NDocValueByType.getPaint(t, ctx, NDocPropName.DEBUG_COLOR).orElse(Color.GRAY);
    }

    public static NOptional<NDocPoint2D> getStyleAsShadowDistance(Object sv, NDocNodeRendererContext ctx) {
        NDocObjEx o = NDocObjEx.of(sv);
        double ww = ctx.getBounds().getWidth();
        double hh = ctx.getBounds().getHeight();
        if (o.isNumber()) {
            NOptional<Number> n = o.asNumber();
            if (n.isPresent()) {
                return NOptional.of(new NDocPoint2D(
                        n.get().doubleValue() / 100.0 * ww,
                        n.get().doubleValue() / 100.0 * hh
                ));
            }
        } else {
            NOptional<NDocPoint2D> n = o.asHPoint2D();
            if (n.isPresent()) {
                return NOptional.of(new NDocPoint2D(
                        n.get().getX() / 100.0 * ww,
                        n.get().getY() / 100.0 * hh
                ));
            }
        }
        return NOptional.ofNamedEmpty("shadow-distance");
    }
}
