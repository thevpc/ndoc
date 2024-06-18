package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.util.Map;

public class HPropValueByNameParser {
    public static NOptional<Paint> getColorProperty(String propName, HNode t, HNodeRendererContext ctx) {
        ObjEx r = ObjEx.of(ctx.computePropertyValue(t, propName).orElse(null));
        return NOptional.of(r.asColor().orElse(null));
    }

    protected static boolean isPreserveShapeRatio(HNode t, HNodeRendererContext ctx) {
        Boolean b = ObjEx.of(ctx.computePropertyValue(t, HPropName.PRESERVE_ASPECT_RATIO).orNull()).asBoolean().orElse(false);
        return b == null ? false : b;
    }

    protected static Double2 getSize(HNode t, Double2 minSize, HNodeRendererContext ctx) {
        Double2 size = HValueTypeParser.getDouble2OrHAlign(t, ctx, HPropName.SIZE).orElse(new Double2(100, 100));
        boolean shapeRatio = isPreserveShapeRatio(t, ctx);
        double ww = ctx.getBounds().getWidth();
        double hh = ctx.getBounds().getHeight();
        //ratio depends on the smallest
        double sx = size.getX() / 100 * ww;
        double sy = size.getY() / 100 * hh;
        if (minSize != null) {
            sx = Math.max(minSize.getX(), sx);
            sy = Math.max(minSize.getY(), sy);
        }
        if (shapeRatio) {
            if (sx < sy) {
                sx = sy;
            }
            if (sy < sx) {
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

    public static Double2 getOrigin(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getDouble2OrHAlign(t, ctx, HPropName.ORIGIN)
                .orElseUse(()->HValueTypeParser.getDouble2OrHAlign(t, ctx, HPropName.AT))
                .orElse(new Double2(0, 0));
    }

    public static Double2 getPosition(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getDouble2OrHAlign(t, ctx, HPropName.POSITION)
                .orElseUse(()->HValueTypeParser.getDouble2OrHAlign(t, ctx, HPropName.AT))
                .orElse(new Double2(0, 0));
    }

    public static TsonElement getStroke(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getTson(t, ctx, HPropName.STROKE).orNull();
    }

    public static Bounds2 selfBounds(HNode t, Double2 selfSize, Double2 minSize, HNodeRendererContext ctx) {
        if (selfSize == null) {
            selfSize = getSize(t, minSize, ctx);
        }
        Bounds2 parentBounds = ctx.getBounds();
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();

        Double2 pos = getPosition(t, ctx).mul(pw / 100, ph / 100);

        double sw = selfSize.getX();
        double sh = selfSize.getY();
        Double2 origin = getOrigin(t, ctx).mul(sw / 100, sh / 100);

        double x = pos.getX() - origin.getX() + parentBounds.getX();
        double y = pos.getY() - origin.getY() + parentBounds.getY();

        Padding padding = HValueTypeParser.getPadding(t, ctx, HPropName.PADDING).orElse(Padding.of(0))
                .mul(pw / 100, ph / 100);

        return new Bounds2(
                x + padding.getLeft(),
                y + padding.getTop(),
                selfSize.getX() - padding.getLeft() - padding.getRight(),
                selfSize.getY() - padding.getTop() - padding.getBottom()
        );
    }

    public static boolean isVisible(HNode t, HNodeRendererContext ctx) {
        NOptional<Boolean> b = HValueTypeParser.getBoolean(t, ctx, HPropName.HIDE);
        if (b.isPresent()) {
            return !b.get();
        }
        b = HValueTypeParser.getBoolean(t, ctx, "show");
        if (b.isPresent()) {
            return !b.get();
        }
        b = HValueTypeParser.getBoolean(t, ctx, "visible");
        if (b.isPresent()) {
            return b.get();
        }
        return true;
    }

    public static double getFontSize(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getDouble(t, ctx, HPropName.FONT_SIZE).orElse(40.0);
    }
    public static String getFontFamily(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getStringOrName(t, ctx, HPropName.FONT_FAMILY).orElse("Serif");
    }

    public static boolean isFontUnderlined(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(t, ctx, HPropName.FONT_UNDERLINED, "underlined").orElse(false);
    }

    public static boolean isFontBold(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(t, ctx, HPropName.FONT_BOLD, "bold").orElse(false);
    }

    public static boolean isFontItalic(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(t, ctx, HPropName.FONT_ITALIC, "italic").orElse(false);
    }

    public static Font getFont(HNode t, HNodeRendererContext ctx) {
        double fontSize = getFontSize(t, ctx);
        boolean fontItalic = isFontItalic(t, ctx);
        boolean fontBold = isFontBold(t, ctx);
        String fontFamily = getFontFamily(t, ctx);
        return (new Font(fontFamily, Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), (int) fontSize));
    }

    public static Double2 getRoundCornerArcs(HNode t, HNodeRendererContext ctx) {
        return ObjEx.of(ctx.computePropertyValue(t, HPropName.ROUND_CORNER).orNull()).asDouble2().orNull();
    }

    public static int getColSpan(HNode t, HNodeRendererContext ctx) {
        Integer i = HValueTypeParser.getInt(t, ctx, HPropName.COLSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static int getRowSpan(HNode t, HNodeRendererContext ctx) {
        Integer i = HValueTypeParser.getInt(t, ctx, HPropName.ROWSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static Boolean get3D(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(t, ctx, HPropName.THEED).orElse(false);
    }

    public static Boolean getRaised(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(t, ctx, HPropName.RAISED).orElse(false);
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
        return NOptional.ofNamedEmpty("shadow");
    }

    public static Paint resolveForegroundColor(HNode t, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        return HValueTypeParser.getPaint(t, ctx, HPropName.FOREGROUND_COLOR, "foreground", "color", "fg").orElse(Color.BLACK);
    }

    public static Paint resolveGridColor(HNode t, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        return HValueTypeParser.getPaint(t, ctx, HPropName.GRID_COLOR).orElse(Color.BLACK);
    }

    public static Paint resolveLineColor(HNode t, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        return HValueTypeParser.getPaint(t, ctx, HPropName.LINE_COLOR).orNull();
    }

    public static Paint resolveBackgroundColor(HNode t, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        Paint color = HValueTypeParser.getPaint(t, ctx, HPropName.BACKGROUND_COLOR, "background", "bg").orNull();
        return color;
    }

    public static boolean requireDrawContour(HNode node, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(node, ctx, HPropName.DRAW_CONTOUR, "contour").orElse(false);
    }

    public static boolean requireDrawGrid(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(t, ctx, HPropName.DRAW_GRID, "grid").orElse(false);
    }

    public static boolean requireFillBackground(HNode t, HNodeRendererContext ctx) {
        return HValueTypeParser.getBoolean(t, ctx, HPropName.FILL_BACKGROUND, "fill").orElse(false);
    }

    public static Paint resolveLineColor(HNode t, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return null;
        }
        Paint color = HValueTypeParser.getPaint(t, ctx, HPropName.LINE_COLOR).orNull();
        if (color != null) {
            return color;
        }
        if (force) {
            //would resolve default color instead ?
            Color cc = ctx.graphics().getColor();
            if (cc == null) {
                return Color.BLACK;
            }
            return cc;
        }
        return null;
    }

    public static int getColumns(HNode node, HNodeRendererContext ctx) {
        return HValueTypeParser.getInt(node, ctx, HPropName.COLUMNS, "cols").orElse(-1);
    }

    public static int getRows(HNode node, HNodeRendererContext ctx) {
        return HValueTypeParser.getInt(node, ctx, HPropName.ROWS, "rows").orElse(-1);
    }

    public static boolean isDebug(HNode p, HNodeRendererContext ctx) {
        return getDebugLevel(p, ctx)>0;
    }

    public static int getDebugLevel(HNode p, HNodeRendererContext ctx) {
        return HValueTypeParser.getInt(p, ctx, HPropName.DEBUG).orElse(0);
    }

    public static Color getDebugColor(HNode t, HNodeRendererContext ctx) {
        return (Color) HValueTypeParser.getPaint(t, ctx, HPropName.DEBUG_COLOR).orElse(Color.GRAY);
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
