package net.thevpc.halfa.spi.eval;

import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HSizeRef;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.util.Map;

public class HValueByName {
    public static NOptional<Paint> getColorProperty(String propName, HNode t, HNodeRendererContext ctx) {
        ObjEx r = ObjEx.of(ctx.computePropertyValue(t, propName).orElse(null));
        return NOptional.of(r.asColor().orElse(null));
    }

    protected static boolean isPreserveShapeRatio(HNode t, HNodeRendererContext ctx) {
        Boolean b = ObjEx.of(ctx.computePropertyValue(t, HPropName.PRESERVE_ASPECT_RATIO).orNull()).asBoolean().orElse(false);
        return b == null ? false : b;
    }

    private static Double2 getSize(HNode t, Double2 minSize, HNodeRendererContext ctx) {
        TsonNumber2 double2OrHAlign = HValueByType.getTsonNumber2Or1OrHAlign(t, ctx, HPropName.SIZE).orElse(
                new TsonNumber2(Tson.of(100.0).toNumber(), Tson.of(100.0).toNumber())
        );

        Double2 size = new Double2(
                ctx.sizeRef().x(double2OrHAlign.getX()).get(),
                ctx.sizeRef().y(double2OrHAlign.getY()).get()
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
            return new Double2(
                    sx,
                    sy
            );
        }
        return new Double2(
                sx,
                sy
        );
    }

    public static Double2 getOrigin(HNode t, HNodeRendererContext ctx, Double2 a) {
        TsonNumber2 double2OrHAlign = HValueByType.getTsonNumber2Or1OrHAlign(t, ctx, HPropName.ORIGIN)
                .orElseUse(() -> HValueByType.getTsonNumber2Or1OrHAlign(t, ctx, HPropName.AT))
                .orElse(new TsonNumber2(Tson.of(0).toNumber(), Tson.of(0).toNumber()));
        HSizeRef sr = new HSizeRef(a.getX(), a.getY(), ctx.getGlobalBounds().getWidth(), ctx.getGlobalBounds().getHeight());
        return new Double2(
                sr.x(double2OrHAlign.getX()).get(),
                sr.y(double2OrHAlign.getY()).get()
        );
    }

    public static Double2 getPosition(HNode t, HNodeRendererContext ctx, Double2 a) {
        TsonNumber2 double2OrHAlign = HValueByType.getTsonNumber2Or1OrHAlign(t, ctx, HPropName.POSITION)
                .orElseUse(() -> HValueByType.getTsonNumber2Or1OrHAlign(t, ctx, HPropName.AT))
                .orElse(new TsonNumber2(Tson.of(0).toNumber(), Tson.of(0).toNumber()));
        HSizeRef sr = new HSizeRef(a.getX(), a.getY(), ctx.getGlobalBounds().getWidth(), ctx.getGlobalBounds().getHeight());
        return new Double2(
                sr.x(double2OrHAlign.getX()).get(),
                sr.y(double2OrHAlign.getY()).get()
        );
    }

    public static TsonElement getStroke(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getTson(t, ctx, HPropName.STROKE).orNull();
    }

    public static Bounds2 selfBounds(HNode t, Double2 selfSize, Double2 minSize, HNodeRendererContext ctx) {
        if (selfSize == null) {
            selfSize = getSize(t, minSize, ctx);
        }
        Bounds2 parentBounds = ctx.getBounds();
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();

        Double2 pos = getPosition(t, ctx, new Double2(pw, ph));

        Double2 origin = getOrigin(t, ctx, selfSize);

        double x = pos.getX() - origin.getX() + parentBounds.getX();
        double y = pos.getY() - origin.getY() + parentBounds.getY();

        Padding padding = HValueByType.getPadding(t, ctx, HPropName.PADDING).orElse(Padding.of(0))
                .mul(pw / 100, ph / 100);

        return new Bounds2(
                x + padding.getLeft(),
                y + padding.getTop(),
                Math.max(selfSize.getX() - padding.getLeft() - padding.getRight(),0),
                Math.max(selfSize.getY() - padding.getTop() - padding.getBottom(),0)
        );
    }

    public static boolean isVisible(HNode t, HNodeRendererContext ctx) {
        NOptional<Boolean> b = HValueByType.getBoolean(t, ctx, HPropName.HIDE);
        if (b.isPresent()) {
            return !b.get();
        }
        b = HValueByType.getBoolean(t, ctx, "show");
        if (b.isPresent()) {
            return !b.get();
        }
        b = HValueByType.getBoolean(t, ctx, "visible");
        if (b.isPresent()) {
            return b.get();
        }
        return true;
    }

    public static double getFontSize(HNode t, HNodeRendererContext ctx) {
        TsonElement e = HValueByType.getTson(t, ctx, HPropName.FONT_SIZE).orNull();
        HSizeRef sr = ctx.sizeRef();
        return Math.min(sr.x(e).orElse(16.0),sr.y(e).orElse(16.0));
    }

    public static String getFontFamily(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getStringOrName(t, ctx, HPropName.FONT_FAMILY).orElse("Serif");
    }

    public static boolean isFontUnderlined(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.FONT_UNDERLINED, "underlined").orElse(false);
    }

    public static boolean isFontStrike(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.FONT_STRIKE, "strike").orElse(false);
    }

    public static boolean isFontBold(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.FONT_BOLD, "bold").orElse(false);
    }

    public static boolean isFontItalic(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.FONT_ITALIC, "italic").orElse(false);
    }

    public static Font getFont(HNode t, HNodeRendererContext ctx) {
        double fontSize = getFontSize(t, ctx);
        boolean fontItalic = isFontItalic(t, ctx);
        boolean fontBold = isFontBold(t, ctx);
        String fontFamily = getFontFamily(t, ctx);

        return FontBySizeResolver.INSTANCE.getFont(fontFamily, Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), fontSize,
                f -> ctx.graphics().getFontMetrics(f)
        );
    }

    public static Double2 getRoundCornerArcs(HNode t, HNodeRendererContext ctx) {
        return ObjEx.of(ctx.computePropertyValue(t, HPropName.ROUND_CORNER).orNull()).asDouble2().orNull();
    }

    public static int getColSpan(HNode t, HNodeRendererContext ctx) {
        Integer i = HValueByType.getInt(t, ctx, HPropName.COLSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static int getRowSpan(HNode t, HNodeRendererContext ctx) {
        Integer i = HValueByType.getInt(t, ctx, HPropName.ROWSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static Boolean get3D(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.THEED).orElse(false);
    }

    public static Boolean getRaised(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.RAISED).orElse(false);
    }

    public static NOptional<Shadow> readStyleAsShadow(HNode t, String s, HNodeRendererContext ctx) {
        Object sv = ctx.computePropertyValue(t, s).orNull();
        ObjEx o = ObjEx.of(sv);
        NOptional<HPoint2D> r = getStyleAsShadowDistance(sv, ctx);
        if (r.isPresent()) {
            Shadow ss = new Shadow();
            ss.setTranslation(r.get());
            return NOptional.of(ss);
        }
        if (sv instanceof TsonElement && ((TsonElement) sv).isContainer()) {
            Shadow shadow = new Shadow();
            for (Map.Entry<String, ObjEx> e : o.argsOrBodyMap().entrySet()) {
                switch (e.getKey()) {
                    case "distance":
                    case "shift":
                    case "origin": {
                        NOptional<HPoint2D> d = getStyleAsShadowDistance(e.getValue(), ctx);
                        if (d.isPresent()) {
                            shadow.setTranslation(d.get());
                        } else {
                            return (NOptional) d;
                        }
                        break;
                    }
                    case "shear": {
                        NOptional<HPoint2D> d = getStyleAsShadowDistance(e.getValue(), ctx);
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
        NOptional<Boolean> rb = ObjEx.of(sv).asBoolean();
        if (rb.isPresent()) {
            if (rb.get()) {
                Shadow ss = new Shadow();
                ss.setTranslation(new HPoint2D(1, 1));
                return NOptional.of(ss);
            }
        }
        NOptional<Double> rd = ObjEx.of(sv).asDouble();
        if (r.isPresent()) {
            Shadow ss = new Shadow();
            ss.setTranslation(new HPoint2D(rd.get(), rd.get()));
            return NOptional.of(ss);
        }
        return NOptional.ofNamedEmpty("shadow");
    }

    public static Paint getForegroundColor(HNode t, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return null;
        }
        return HValueByType.getPaint(t, ctx, HPropName.FOREGROUND_COLOR, "foreground", "color", "fg").orElse(force ? Color.BLACK : null);
    }

    public static Paint resolveGridColor(HNode t, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        return HValueByType.getPaint(t, ctx, HPropName.GRID_COLOR).orElse(Color.BLACK);
    }

//    public static Paint resolveLineColor(HNode t, HNodeRendererContext ctx) {
//        if (ctx.isDry()) {
//            return null;
//        }
//        return HValueTypeParser.getPaint(t, ctx, HPropName.LINE_COLOR).orNull();
//    }

    public static Paint resolveBackgroundColor(HNode t, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        Paint color = HValueByType.getPaint(t, ctx, HPropName.BACKGROUND_COLOR, "background", "bg").orNull();
        return color;
    }

    public static boolean isDrawContour(HNode node, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(node, ctx, HPropName.DRAW_CONTOUR, "contour").orElse(false);
    }

    public static boolean requireDrawGrid(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.DRAW_GRID, "grid").orElse(false);
    }

    public static boolean requireFillBackground(HNode t, HNodeRendererContext ctx) {
        return HValueByType.getBoolean(t, ctx, HPropName.FILL_BACKGROUND, "fill").orElse(false);
    }

//    public static Paint resolveLineColor(HNode t, HNodeRendererContext ctx, boolean force) {
//        if (ctx.isDry()) {
//            return null;
//        }
//        Paint color = HValueTypeParser.getPaint(t, ctx, HPropName.LINE_COLOR).orNull();
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

    public static int getColumns(HNode node, HNodeRendererContext ctx) {
        return HValueByType.getInt(node, ctx, HPropName.COLUMNS, "cols").orElse(-1);
    }

    public static int getRows(HNode node, HNodeRendererContext ctx) {
        return HValueByType.getInt(node, ctx, HPropName.ROWS, "rows").orElse(-1);
    }

    public static boolean isDebug(HNode p, HNodeRendererContext ctx) {
        return getDebugLevel(p, ctx) > 0;
    }

    public static int getDebugLevel(HNode p, HNodeRendererContext ctx) {
        return HValueByType.getIntOrBoolean(p, ctx, HPropName.DEBUG).orElse(0);
    }

    public static Color getDebugColor(HNode t, HNodeRendererContext ctx) {
        return (Color) HValueByType.getPaint(t, ctx, HPropName.DEBUG_COLOR).orElse(Color.GRAY);
    }

    public static NOptional<HPoint2D> getStyleAsShadowDistance(Object sv, HNodeRendererContext ctx) {
        ObjEx o = ObjEx.of(sv);
        double ww = ctx.getBounds().getWidth();
        double hh = ctx.getBounds().getHeight();
        if (o.isNumber()) {
            NOptional<Number> n = o.asNumber();
            if (n.isPresent()) {
                return NOptional.of(new HPoint2D(
                        n.get().doubleValue() / 100.0 * ww,
                        n.get().doubleValue() / 100.0 * hh
                ));
            }
        } else {
            NOptional<HPoint2D> n = o.asHPoint2D();
            if (n.isPresent()) {
                return NOptional.of(new HPoint2D(
                        n.get().getX() / 100.0 * ww,
                        n.get().getY() / 100.0 * hh
                ));
            }
        }
        return NOptional.ofNamedEmpty("shadow-distance");
    }
}
