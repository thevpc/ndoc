package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HStyleValue;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.renderer.screen.HNodeRenderer;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;

public abstract class AbstractHNodeRenderer implements HNodeRenderer {
    private String[] types;

    public AbstractHNodeRenderer(String... types) {
        this.types = types;
    }

    @Override
    public String[] types() {
        return types;
    }


    public abstract HSizeRequirements render(HNode p, HNodeRendererContext ctx);


    protected SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    protected double resolveFontSize(HNode t, HGraphics g, HNodeRendererContext ctx) {
//        Bounds2 size = ctx.getBounds();
        double fontSize = (double) ctx.getProperty(t, HPropName.FONT_SIZE).orElse(40.0);
        fontSize = fontSize;// / Math.max(PageView.REF_SIZE.width, PageView.REF_SIZE.height) * Math.max(size.getWidth(), size.getHeight());
        return fontSize;

    }

    protected void applyFont(HNode t, HGraphics g, HNodeRendererContext ctx) {
//        Bounds2 size = ctx.getBounds();
        double fontSize = resolveFontSize(t, g, ctx);
        boolean fontItalic = (boolean) ctx.getProperty(t, HPropName.FONT_ITALIC).orElse(false);
        boolean fontBold = (boolean) ctx.getProperty(t, HPropName.FONT_BOLD).orElse(false);
        String fontFamily = (String) ctx.getProperty(t, HPropName.FONT_FAMILY).orElse("Arial");
        g.setFont(new Font(NStringUtils.firstNonBlank(fontFamily, "Arial").trim(), Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), (int) fontSize));
    }

    protected SizeD mapDim(double w, double h, HNodeRendererContext ctx) {
        Bounds2 size = ctx.getBounds();
        return new SizeD(w / 100 * size.getWidth(), w / 100 * size.getHeight());
    }

    protected Double2 roundCornerArcs(HNode t, HNodeRendererContext ctx) {
        return (Double2) ctx.getProperty(t, HPropName.ROUND_CORNER).orElse(null);
    }

    protected int colspan(HNode t, HNodeRendererContext ctx) {
        Integer i = (Integer) ctx.getProperty(t, HPropName.COLSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    protected int rowspan(HNode t, HNodeRendererContext ctx) {
        Integer i = (Integer) ctx.getProperty(t, HPropName.ROWSPAN).orElse(1);
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    protected boolean preserveShapeRatio(HNode t, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.getProperty(t, HPropName.PRESERVE_SHAPE_RATIO).orElse(false);
        return b == null ? false : b;
    }

    protected Boolean get3D(HNode t, HNodeRendererContext ctx) {
        return (Boolean) ctx.getProperty(t, HPropName.THEED).orElse(false);
    }

    protected Boolean getRaised(HNode t, HNodeRendererContext ctx) {
        return (Boolean) ctx.getProperty(t, HPropName.RAISED).orElse(null);
    }

    public static NOptional<Double2> readStyleAsDouble2(HNode t, String s, HNodeRendererContext ctx) {
        Object sv = ctx.getProperty(t, s).orNull();
        if (sv != null) {
            return HStyleValue.toDouble2(sv);
        }
        return NOptional.ofNamedEmpty(s);
    }

    protected Double2 size(HNode t, HNodeRendererContext ctx) {
        Double2 size = readStyleAsDouble2(t, HPropName.SIZE, ctx).orElse(new Double2(100, 100));
        boolean shapeRatio = preserveShapeRatio(t, ctx);
        double ww = ctx.getBounds().getWidth();
        double hh = ctx.getBounds().getHeight();
        //ratio depends on the smallest
        if (shapeRatio) {
            if (ww < hh) {
                hh = ww;
            }
            if (hh < ww) {
                ww = hh;
            }
            return new Double2(
                    size.getX() / 100 * ww,
                    size.getY() / 100 * hh
            );
        }
        return new Double2(
                size.getX() / 100 * ww,
                size.getY() / 100 * hh
        );
    }

    protected Bounds2 bounds(HNode t, HNodeRendererContext ctx) {
        Double2 size = (Double2) ctx.getProperty(t, HPropName.SIZE).orElse(new Double2(100, 100));
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

    protected Bounds2 selfBounds(HNode t, HNodeRendererContext ctx) {
        return selfBounds(t, null, ctx);
    }

    protected Bounds2 selfBounds(HNode t, Double2 selfSize, HNodeRendererContext ctx) {
        if (selfSize == null) {
            selfSize = size(t, ctx);
        }
        Bounds2 parentBounds = ctx.getBounds();
        Double2 pos = readStyleAsDouble2(t, HPropName.POSITION, ctx).orElse(new Double2(0, 0));
        Double2 origin = readStyleAsDouble2(t, HPropName.ORIGIN, ctx).orElse(new Double2(0, 0));
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();
        double sw = selfSize.getX();
        double sh = selfSize.getY();
        double x = pos.getX() / 100 * pw - origin.getX() / 100 * sw + parentBounds.getX();
        double y = pos.getY() / 100 * ph - origin.getY() / 100 * sh + parentBounds.getY();

        return new Bounds2(x, y, selfSize.getX(), selfSize.getY());
    }


    protected boolean applyForeground(HNode t, HGraphics g, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return false;
        }
        Paint foregroundColor = (Paint) ctx.getProperty(t, HPropName.FOREGROUND_COLOR).orElse(Color.BLACK);
        if (foregroundColor == null) {
            g.setPaint(Color.BLACK);
        } else if (foregroundColor instanceof Color) {
            g.setColor((Color) foregroundColor);
        } else {
            g.setPaint(foregroundColor);
        }
        return true;
    }

    public boolean applyBackgroundColor(HNode t, HGraphics g, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return false;
        }
        Paint backgroundColor = (Paint) ctx.getProperty(t, HPropName.BACKGROUND_COLOR).orElse(null);
        if (backgroundColor != null) {
            if (backgroundColor instanceof Color) {
                g.setColor((Color) backgroundColor);
            } else {
                g.setPaint(backgroundColor);
            }
            return true;
        }
        return false;
    }

    public boolean applyGridColor(HNode t, HGraphics g, HNodeRendererContext ctx,boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint backgroundColor = (Paint) ctx.getProperty(t, HPropName.GRID_COLOR).orElse(null);
        if (backgroundColor != null) {
            if (backgroundColor instanceof Color) {
                g.setColor((Color) backgroundColor);
            } else {
                g.setPaint(backgroundColor);
            }
            return true;
        }
        if(force){
            g.setColor(Color.gray);
            return true;
        }
        return false;
    }

    public boolean requireDrawContour(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.getProperty(t, HPropName.DRAW_CONTOUR).orElse(false);
        if (b == null) {
            return false;
        }
        return b;
    }

    public boolean requireDrawGrid(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.getProperty(t, HPropName.DRAW_GRID).orElse(false);
        if (b == null) {
            return false;
        }
        return b;
    }

    public boolean requireFillBackground(HNode t, HGraphics g, HNodeRendererContext ctx) {
        Boolean b = (Boolean) ctx.getProperty(t, HPropName.FILL_BACKGROUND).orElse(false);
        if (b == null) {
            return false;
        }
        return true;
    }

    public boolean applyLineColor(HNode t, HGraphics g, HNodeRendererContext ctx, boolean force) {
        if (ctx.isDry()) {
            return false;
        }
        Paint lineColor = (Paint) ctx.getProperty(t, HPropName.LINE_COLOR).orElse(null);
        if (lineColor != null) {
            if (lineColor instanceof Color) {
                g.setColor((Color) lineColor);
            } else {
                g.setPaint(lineColor);
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

    protected void paintBorderLine(HNode t, HNodeRendererContext ctx, HGraphics g, Bounds2 a) {
        if (ctx.isDry()) {
            return;
        }
        if (requireDrawContour(t, g, ctx)) {
            if (applyLineColor(t, g, ctx, true)) {
                g.drawRect(
                        HUtils.intOf(a.getMinX()), HUtils.intOf(a.getMinY()),
                        HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
                );
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
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        Bounds2 r1 = a;
        if (r1.getMinX() < minX) {
            minX = r1.getMinX();
        }
        if (r1.getMaxX() < minX) {
            maxX = r1.getMaxX();
        }
        if (r1.getMinY() < minY) {
            minY = r1.getMinY();
        }
        if (r1.getMaxY() < minY) {
            maxY = r1.getMaxY();
        }
        r1 = b;
        if (r1.getMinX() < minX) {
            minX = r1.getMinX();
        }
        if (r1.getMaxX() < minX) {
            maxX = r1.getMaxX();
        }
        if (r1.getMinY() < minY) {
            minY = r1.getMinY();
        }
        if (r1.getMaxY() < minY) {
            maxY = r1.getMaxY();
        }
        return new Bounds2(minX, minY, maxX - minX, maxY - minY);
    }

}
