package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.style.HStyleValue;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.renderer.screen.common.strokes.StrokeFactory;
import net.thevpc.halfa.spi.renderer.HNodeRenderer;
import net.thevpc.halfa.engine.renderer.screen.renderers.HGraphicsImpl;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.util.ColorPalette;
import net.thevpc.halfa.spi.util.DefaultColorPalette;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.TsonElement;

import java.awt.*;

public abstract class AbstractHNodeRenderer implements HNodeRenderer {
    private String[] types;

    public AbstractHNodeRenderer(String... types) {
        this.types = types;
    }

    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        Bounds2 bounds = ctx.getBounds();
        return new HSizeRequirements(
                0,
                bounds.getWidth(),
                bounds.getWidth(),
                0,
                bounds.getHeight(),
                bounds.getHeight()
        );
    }

    @Override
    public String[] types() {
        return types;
    }

    public void render(HNode p, HNodeRendererContext ctx) {
        boolean v = resolveVisible(p, ctx.graphics(), ctx);
        if (!v) {
            return;
        }
        Bounds2 selfBounds = selfBounds(p, ctx);
        Graphics2D nv = null;
        try {
            if (!ctx.isDry()) {
                Rotation rotation = readStyleAsRotation(p, HPropName.ROTATE, ctx).orElse(null);
                if (rotation != null) {
                    Double angle = rotation.getAngle();
                    if (angle != 0) {
                        angle = angle / 180.0 * Math.PI;
                        if (angle != 0) {
                            HGraphics g = ctx.graphics();
                            nv = (Graphics2D) g.context().create();
                            double rotX = rotation.getX() / 100.0 * selfBounds.getWidth() + selfBounds.getX();
                            double rotY = rotation.getY() / 100.0 * selfBounds.getHeight() + selfBounds.getY();
                            if (getDebugLevel(p, ctx) > 0) {
                                g.setColor(getDebugColor(p, ctx));
                                g.drawRect(selfBounds);
                                g.fillRect(rotX - 3, rotY - 3, 6, 6);
//                            g.drawString(rotX + "," + rotY+" : "+p, 100, 100);
                            }
                            nv.rotate(
                                    angle,
                                    rotX,
                                    rotY
                            );
                            ctx = ctx.withGraphics(new HGraphicsImpl(nv));
                        }
                    }
                }
            }
            render0(p, ctx);
        } finally {
            if (nv != null) {
                nv.dispose();
            }
        }
    }

    public abstract void render0(HNode p, HNodeRendererContext ctx);

    protected SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    protected boolean resolveVisible(HNode t, HGraphics g, HNodeRendererContext ctx) {
        boolean d = (boolean) ctx.computePropertyValue(t, HPropName.HIDE).orElse(false);
        return !d;
    }

    protected double resolveFontSize(HNode t, HGraphics g, HNodeRendererContext ctx) {
//        Bounds2 size = ctx.getBounds();
        double fontSize = (double) ctx.computePropertyValue(t, HPropName.FONT_SIZE).orElse(40.0);
        fontSize = fontSize;// / Math.max(PageView.REF_SIZE.width, PageView.REF_SIZE.height) * Math.max(size.getWidth(), size.getHeight());
        return fontSize;

    }

    protected Stroke resolveStroke(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Stroke stroke = null;
        TsonElement strokeElem = (TsonElement) ctx.computePropertyValue(t, HPropName.STROKE).orElse(null);
        if (strokeElem != null) {
            return StrokeFactory.createStroke(strokeElem);
        }
        return null;
    }

    protected boolean applyStroke(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Stroke stroke = null;
        TsonElement strokeElem = (TsonElement) ctx.computePropertyValue(t, HPropName.STROKE).orElse(null);
        if (strokeElem != null) {
            g.setStroke(StrokeFactory.createStroke(strokeElem));
            return true;
        }
        return false;
    }


    protected void applyFont(HNode t, HGraphics g, HNodeRendererContext ctx) {
//        Bounds2 size = ctx.getBounds();
        double fontSize = resolveFontSize(t, g, ctx);
        boolean fontItalic = (boolean) ctx.computePropertyValue(t, HPropName.FONT_ITALIC).orElse(false);
        boolean fontBold = (boolean) ctx.computePropertyValue(t, HPropName.FONT_BOLD).orElse(false);
        String fontFamily = (String) ctx.computePropertyValue(t, HPropName.FONT_FAMILY).orElse("Arial");
        g.setFont(new Font(NStringUtils.firstNonBlank(fontFamily, "Arial").trim(), Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), (int) fontSize));
    }

    protected SizeD mapDim(double w, double h, HNodeRendererContext ctx) {
        Bounds2 size = ctx.getBounds();
        return new SizeD(w / 100 * size.getWidth(), w / 100 * size.getHeight());
    }

    protected Double2 roundCornerArcs(HNode t, HNodeRendererContext ctx) {
        return (Double2) ctx.computePropertyValue(t, HPropName.ROUND_CORNER).orElse(null);
    }

    protected int colspan(HNode t, HNodeRendererContext ctx) {
        Integer i = (Integer) ctx.computePropertyValue(t, HPropName.COLSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    protected int rowspan(HNode t, HNodeRendererContext ctx) {
        Integer i = (Integer) ctx.computePropertyValue(t, HPropName.ROWSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    protected boolean preserveShapeRatio(HNode t, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.computePropertyValue(t, HPropName.PRESERVE_ASPECT_RATIO).orElse(false);
        return b == null ? false : b;
    }

    protected Boolean get3D(HNode t, HNodeRendererContext ctx) {
        return (Boolean) ctx.computePropertyValue(t, HPropName.THEED).orElse(false);
    }

    protected Boolean getRaised(HNode t, HNodeRendererContext ctx) {
        return (Boolean) ctx.computePropertyValue(t, HPropName.RAISED).orElse(null);
    }

    public static NOptional<Double2> readStyleAsDouble2(HNode t, String s, HNodeRendererContext ctx) {
        Object sv = ctx.computePropertyValue(t, s).orNull();
        if (sv != null) {
            return HStyleValue.toDouble2(sv);
        }
        return NOptional.ofNamedEmpty(s);
    }

    public static NOptional<Double4> readStyleAsDouble4(HNode t, String s, HNodeRendererContext ctx) {
        Object sv = ctx.computePropertyValue(t, s).orNull();
        if (sv != null) {
            return ObjEx.of(s).asDouble4();
        }
        return NOptional.ofNamedEmpty(s);
    }

    public static NOptional<Padding> readStyleAsPadding(HNode t, String s, HNodeRendererContext ctx) {
        Object sv = ctx.computePropertyValue(t, s).orNull();
        if (sv != null) {
            return ObjEx.of(s).asPadding();
        }
        return NOptional.ofNamedEmpty(s);
    }

    public static NOptional<Rotation> readStyleAsRotation(HNode t, String s, HNodeRendererContext ctx) {
        Object sv = ctx.computePropertyValue(t, s).orNull();
        if (sv != null) {
            return ObjEx.of(sv).asRotation();
        }
        return NOptional.ofNamedEmpty(s);
    }

    protected Double2 size(HNode t, Double2 minSize, HNodeRendererContext ctx) {
        Double2 size = readStyleAsDouble2(t, HPropName.SIZE, ctx).orElse(new Double2(100, 100));
        boolean shapeRatio = preserveShapeRatio(t, ctx);
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
            if (ww < hh) {
                hh = ww;
            }
            if (hh < ww) {
                ww = hh;
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

    protected Bounds2 bounds(HNode t, HNodeRendererContext ctx) {
        Double2 size = (Double2) ctx.computePropertyValue(t, HPropName.SIZE).orElse(new Double2(100, 100));
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

    public Bounds2 bgBounds(HNode p, HNodeRendererContext ctx) {
        return selfBounds(p, null, null, ctx);
    }

    public Bounds2 selfBounds(HNode t, HNodeRendererContext ctx) {
        return selfBounds(t, null, null, ctx);
    }

    protected Bounds2 selfBounds(HNode t, Double2 selfSize, Double2 minSize, HNodeRendererContext ctx) {
        if (selfSize == null) {
            selfSize = size(t, minSize, ctx);
        }
        Bounds2 parentBounds = ctx.getBounds();
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();

        Double2 pos = readStyleAsDouble2(t, HPropName.POSITION, ctx).orElse(new Double2(0, 0))
                .mul(pw / 100, ph / 100);

        double sw = selfSize.getX();
        double sh = selfSize.getY();
        Double2 origin = readStyleAsDouble2(t, HPropName.ORIGIN, ctx).orElse(new Double2(0, 0))
                .mul(sw / 100, sh / 100);

        double x = pos.getX() - origin.getX() + parentBounds.getX();
        double y = pos.getY() - origin.getY() + parentBounds.getY();

        Padding padding = readStyleAsPadding(t, HPropName.PADDING, ctx).orElse(Padding.of(0))
                .mul(pw / 100, ph / 100);

        return new Bounds2(
                x + padding.getLeft()
                , y + padding.getTop()
                , selfSize.getX() - padding.getLeft() - padding.getRight()
                , selfSize.getY() - padding.getTop() - padding.getBottom()
        );
    }


    protected NOptional<ColorPalette> getColorPalette(HNode t, HNodeRendererContext ctx) {
        NOptional<Object> cp = ctx.computePropertyValue(t, HPropName.COLOR_PALETTE);
        if (cp.isPresent()) {
            ObjEx o = ObjEx.of(cp.get());
            NOptional<Color[]> a = o.asColorArray();
            if (a.isPresent()) {
                return NOptional.of(new DefaultColorPalette(a.get()));
            }
        }
        return NOptional.ofNamedEmpty(HPropName.COLOR_PALETTE);
    }

    protected NOptional<Paint> getColorProperty(String propName, HNode t, HNodeRendererContext ctx) {
        return NOptional.of(new ObjEx(ctx.computePropertyValue(t, propName).orElse(null)).parseColor(
                getColorPalette(t, ctx).orNull()
        ).orElse(null));
    }

    protected Color getDebugColor(HNode t, HNodeRendererContext ctx) {
        return (Color) getColorProperty(HPropName.DEBUG_COLOR, t, ctx).orElse(Color.GRAY);
    }

    protected boolean applyForeground(HNode t, HGraphics g, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return false;
        }
        Paint color = getColorProperty(HPropName.FOREGROUND_COLOR, t, ctx).orElse(Color.BLACK);
        if (color instanceof Color) {
            g.setColor((Color) color);
        } else {
            g.setPaint(color);
        }
        return true;
    }

    public Paint resolveBackgroundColor(HNode t, HGraphics g, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return null;
        }
        Paint color = getColorProperty(HPropName.BACKGROUND_COLOR, t, ctx).orNull();
        return color;
    }

    public boolean applyBackgroundColor(HNode t, HGraphics g, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return false;
        }
        Paint color = getColorProperty(HPropName.BACKGROUND_COLOR, t, ctx).orNull();
        if (color != null) {
            if (color instanceof Color) {
                g.setColor((Color) color);
            } else {
                g.setPaint(color);
            }
            return true;
        }
        return false;
    }

    public boolean applyGridColor(HNode t, HGraphics g, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint color = getColorProperty(HPropName.GRID_COLOR, t, ctx).orNull();
        if (color != null) {
            if (color instanceof Color) {
                g.setColor((Color) color);
            } else {
                g.setPaint(color);
            }
            return true;
        }
        if (force) {
            g.setColor(Color.gray);
            return true;
        }
        return false;
    }

    public boolean requireDrawContour(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.computePropertyValue(t, HPropName.DRAW_CONTOUR).orElse(false);
        if (b == null) {
            return false;
        }
        return b;
    }

    public boolean requireDrawGrid(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.computePropertyValue(t, HPropName.DRAW_GRID).orElse(false);
        if (b == null) {
            return false;
        }
        return b;
    }

    public boolean requireFillBackground(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.computePropertyValue(t, HPropName.FILL_BACKGROUND).orElse(false);
        if (b == null) {
            return false;
        }
        return true;
    }

    public Paint resolveLineColor(HNode t, HGraphics g, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return null;
        }
        Paint color = getColorProperty(HPropName.LINE_COLOR, t, ctx).orNull();
        if (color != null) {
            return color;
        }
        if (force) {
            //would resolve default color instead ?
            Color cc = g.getColor();
            if(cc==null){
                return Color.BLACK;
            }
            return cc;
        }
        return null;
    }

    public boolean applyLineColor(HNode t, HGraphics g, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint color = getColorProperty(HPropName.LINE_COLOR, t, ctx).orNull();
        if (color != null) {
            if (color instanceof Color) {
                g.setColor((Color) color);
            } else {
                g.setPaint(color);
            }
            return true;
        }
        if (force) {
            //would resolve default color instead ?
            g.setPaint(Color.BLACK);
            return true;
        }
        return false;
    }

    protected void paintDebugBox(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a) {
        if (ctx.isDry()) {
            return;
        }
        if (getDebugLevel(t, ctx) > 0) {
            g.setColor(getDebugColor(t, ctx));
            g.drawRect(
                    HUtils.doubleOf(a.getMinX()), HUtils.doubleOf(a.getMinY()),
                    HUtils.doubleOf(a.getWidth()), HUtils.doubleOf(a.getHeight())
            );
            Double2 origin = readStyleAsDouble2(t, HPropName.ORIGIN, ctx).orElse(new Double2(0, 0));
            double x = origin.getX() / 100 * a.getWidth() + a.getX();
            double y = origin.getY() / 100 * a.getHeight() + a.getY();
            g.setColor(getDebugColor(t, ctx));
            int originSize = 6;
            g.fillOval(
                    x - originSize / 2, y - originSize / 2,
                    originSize, originSize
            );
        }
    }

    protected void paintBorderLine(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a) {
        if (ctx.isDry()) {
            return;
        }
        paintDebugBox(t, ctx, g, a);
        if (requireDrawContour(t, g, ctx)) {
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

    protected void paintBackground(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a) {
        if (ctx.isDry()) {
            return;
        }
        if (requireFillBackground(t, g, ctx)) {
            if (applyBackgroundColor(t, g, ctx)) {
                g.fillRect(a);
            }
        }
    }

    protected Bounds2 expand(Bounds2 a, Bounds2 b) {
        Bounds2 r1 = a;
        double minX = r1.getMinX();
        double minY = r1.getMinY();
        double maxX = r1.getMaxX();
        double maxY = r1.getMaxY();
        r1 = b;
        if (r1.getMinX() < minX) {
            minX = r1.getMinX();
        }
        if (r1.getMaxX() > minX) {
            maxX = r1.getMaxX();
        }
        if (r1.getMinY() < minY) {
            minY = r1.getMinY();
        }
        if (r1.getMaxY() > minY) {
            maxY = r1.getMaxY();
        }
        return new Bounds2(minX, minY, maxX - minX, maxY - minY);
    }

    protected int getDebugLevel(HNode p, HNodeRendererContext ctx) {
        ObjEx h = ObjEx.of(ctx.computePropertyValue(p, HPropName.DEBUG).orNull());
        NOptional<Boolean> b = h.asBoolean();
        if (b.isPresent()) {
            return b.get() ? 1 : 0;
        }
        return h.asInt().orElse(0);
    }

}
