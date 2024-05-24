package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.PageView;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;
import net.thevpc.halfa.spi.HUtils;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;

public abstract class AbstractHPartRenderer {

    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        Bounds2 b = bounds(p, ctx);
        HSizeRequirements r = new HSizeRequirements();
        double ww = b.getWidth();
        double hh = b.getHeight();
        r.minX = 0;
        r.maxX = ww;
        r.preferredX = 0;
        r.minY = 0;
        r.maxY = hh;
        r.preferredY = 0;
        return r;
    }

    public abstract Bounds2 paintPagePart(HNode p, HPartRendererContext ctx);


    protected SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    protected double resolveFontSize(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Bounds2 size = ctx.getBounds();
        double fontSize = (double) ctx.getStyle(t, HStyleType.FONT_SIZE).orElse(HStyles.fontSize(40.0)).getValue();
        fontSize = fontSize / Math.max(PageView.REF_SIZE.width, PageView.REF_SIZE.height) * Math.max(size.getWidth(), size.getHeight());
        return fontSize;

    }

    protected void applyFont(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Bounds2 size = ctx.getBounds();
        double fontSize = resolveFontSize(t, g, ctx);
        boolean fontItalic = (boolean) ctx.getStyle(t, HStyleType.FONT_ITALIC).orElse(HStyles.fontItalic(false)).getValue();
        boolean fontBold = (boolean) ctx.getStyle(t, HStyleType.FONT_BOLD).orElse(HStyles.fontItalic(false)).getValue();
        String fontFamily = (String) ctx.getStyle(t, HStyleType.FONT_FAMILY).orElse(HStyles.fontFamily("Arial")).getValue();
        g.setFont(new Font(NStringUtils.firstNonBlank(fontFamily, "Arial").trim(), Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), (int) fontSize));
    }

    protected SizeD mapDim(double w, double h, HPartRendererContext ctx) {
        Bounds2 size = ctx.getBounds();
        return new SizeD(w / 100 * size.getWidth(), w / 100 * size.getHeight());
    }

    protected Double2 roundCornerArcs(HNode t, HPartRendererContext ctx) {
        return (Double2) ctx.getStyle(t, HStyleType.ROUND_CORNER).orElse(HStyles.roundCorner(null)).getValue();
    }

    protected int colspan(HNode t, HPartRendererContext ctx) {
        Integer i = (Integer) ctx.getStyle(t, HStyleType.COLSPAN).orElse(HStyles.colspan(1)).getValue();
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    protected int rowspan(HNode t, HPartRendererContext ctx) {
        Integer i = (Integer) ctx.getStyle(t, HStyleType.ROWSPAN).orElse(HStyles.colspan(1)).getValue();
        if (i == null) {
            return 1;
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    protected boolean preserveShapeRatio(HNode t, HPartRendererContext ctx) {
        Boolean b = (Boolean) ctx.getStyle(t, HStyleType.PRESERVE_SHAPE_RATIO).orElse(HStyles.preserveShapeRatio(false)).getValue();
        return b == null ? false : b;
    }

    protected Boolean get3D(HNode t, HPartRendererContext ctx) {
        return (Boolean) ctx.getStyle(t, HStyleType.THEED).orElse(HStyles.threeD(false)).getValue();
    }

    protected Boolean getRaised(HNode t, HPartRendererContext ctx) {
        return (Boolean) ctx.getStyle(t, HStyleType.RAISED).orElse(HStyles.raised(null)).getValue();
    }

    protected Double2 size(HNode t, HPartRendererContext ctx) {
        Double2 size = (Double2) ctx.getStyle(t, HStyleType.SIZE).orElse(HStyles.position(40, 40)).getValue();
        if (size == null) {
            size = new Double2(40, 40);
        }
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

    protected Bounds2 bounds(HNode t, HPartRendererContext ctx) {
        Double2 size = (Double2) ctx.getStyle(t, HStyleType.SIZE).orElse(HStyles.position(40, 40)).getValue();
        if (size == null) {
            size = new Double2(40, 40);
        }
        return new Bounds2(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                size.getX() / 100 * ctx.getBounds().getWidth(),
                size.getY() / 100 * ctx.getBounds().getHeight()
        );
    }

    protected Bounds2 selfBounds(HNode t, HPartRendererContext ctx) {
        return selfBounds(t, null, ctx);
    }

    protected NOptional<Double2> readStyleAsDouble2(HNode t, HStyleType s, HPartRendererContext ctx) {
        HStyle sv = ctx.getStyle(t, s).orNull();
        if(sv!=null){
            Object svv = sv.getValue();
            if(svv instanceof Double2){
                return NOptional.of((Double2) svv);
            }
            if(svv instanceof HAlign){
                switch ((HAlign)svv){
                    case TOP:return NOptional.of(new Double2(50,0));
                    case BOTTOM:return NOptional.of(new Double2(50,100));
                    case LEFT:return NOptional.of(new Double2(0,50));
                    case RIGHT:return NOptional.of(new Double2(100,50));
                    case TOP_LEFT:return NOptional.of(new Double2(0,0));
                    case CENTER:return NOptional.of(new Double2(50,50));
                    case TOP_RIGHT:return NOptional.of(new Double2(100,0));
                    case BOTTOM_RIGHT:return NOptional.of(new Double2(100,100));
                    case BOTTOM_LEFT:return NOptional.of(new Double2(0,100));
                    case NONE:{
                        break;
                    }
                }
            }
        }
        return NOptional.ofNamedEmpty(s.name());
    }

    protected Bounds2 selfBounds(HNode t, Double2 selfSize, HPartRendererContext ctx) {
        if (selfSize == null) {
            selfSize = size(t, ctx);
        }
        Bounds2 parentBounds = ctx.getBounds();
        Double2 pos = readStyleAsDouble2(t, HStyleType.POSITION,ctx).orElse(new Double2(50,50));
        Double2 origin = readStyleAsDouble2(t, HStyleType.ORIGIN,ctx).orElse(new Double2(50,50));
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();
        double sw = selfSize.getX();
        double sh = selfSize.getY();
        double x = pos.getX() / 100 * pw - origin.getX() / 100 * sw + parentBounds.getX();
        double y = pos.getY() / 100 * ph - origin.getY() / 100 * sh + parentBounds.getY();

        return new Bounds2(x, y, selfSize.getX(), selfSize.getY());
    }



    protected void applyForeground(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Paint foregroundColor = (Paint) ctx.getStyle(t, HStyleType.FOREGROUND_COLOR).orElse(HStyles.foregroundColor(Color.BLACK)).getValue();
        if (foregroundColor == null) {
            g.setPaint(Color.BLACK);
        } else if (foregroundColor instanceof Color) {
            g.setColor((Color) foregroundColor);
        } else {
            g.setPaint(foregroundColor);
        }
    }

    public boolean applyBackgroundColor(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Paint backgroundColor = (Paint) ctx.getStyle(t, HStyleType.BACKGROUND_COLOR).orElse(HStyles.backgroundColor(null)).getValue();
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

    public boolean applyGridColor(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Paint backgroundColor = (Paint) ctx.getStyle(t, HStyleType.GRID_COLOR).orElse(HStyles.backgroundColor(null)).getValue();
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

    public boolean requireDrawContour(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Boolean b = (Boolean) ctx.getStyle(t, HStyleType.DRAW_CONTOUR).orElse(HStyles.drawContour(false)).getValue();
        if(b==null){
            return false;
        }
        return b;
    }
    public boolean requireDrawGrid(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Boolean b = (Boolean) ctx.getStyle(t, HStyleType.DRAW_GRID).orElse(HStyles.drawGrid(false)).getValue();
        if(b==null){
            return false;
        }
        return b;
    }
    public boolean requireFillBackground(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Boolean b = (Boolean) ctx.getStyle(t, HStyleType.FILL_BACKGROUND).orElse(HStyles.drawGrid(false)).getValue();
        if(b==null){
            return false;
        }
        return true;
    }

    public boolean applyLineColor(HNode t, Graphics2D g, HPartRendererContext ctx,boolean force) {
        Paint lineColor = (Paint) ctx.getStyle(t, HStyleType.LINE_COLOR).orElse(HStyles.lineColor(null)).getValue();
        if (lineColor != null) {
            if (lineColor instanceof Color) {
                g.setColor((Color) lineColor);
            } else {
                g.setPaint(lineColor);
            }
            return true;
        }
        if(force){
            //would resolve default color instead ?
            g.setPaint(Color.BLACK);
            return true;
        }
        return false;
    }

    protected void paintBorderLine(HNode t, HPartRendererContext ctx, Graphics2D g, Bounds2 a) {
        if (requireDrawContour(t, g, ctx)) {
            if (applyLineColor(t, g, ctx,true)) {
                g.drawRect(
                        HUtils.intOf(a.getMinX()), HUtils.intOf(a.getMinY()),
                        HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
                );
            }
        }
    }

    protected void paintBackground(HNode t, HPartRendererContext ctx, Graphics2D g, Bounds2 a) {
        if (requireFillBackground(t, g, ctx)) {
            if (applyBackgroundColor(t, g, ctx)) {
                g.fillRect(
                        HUtils.intOf(a.getMinX()), HUtils.intOf(a.getMinY()),
                        HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
                );
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
