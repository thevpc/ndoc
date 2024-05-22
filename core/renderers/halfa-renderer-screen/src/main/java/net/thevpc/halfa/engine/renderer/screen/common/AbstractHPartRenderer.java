package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.PageView;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class AbstractHPartRenderer {

    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        Rectangle2D.Double b = bounds(p, ctx);
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

    public abstract Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx);


    protected SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    protected double resolveFontSize(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Rectangle2D.Double size = ctx.getBounds();
        double fontSize = (double) ctx.getStyle(t, HStyleType.FONT_SIZE).orElse(HStyles.fontSize(40.0)).getValue();
        fontSize = fontSize / Math.max(PageView.REF_SIZE.width, PageView.REF_SIZE.height) * Math.max(size.width, size.height);
        return fontSize;

    }

    protected void applyFont(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Rectangle2D.Double size = ctx.getBounds();
        double fontSize = resolveFontSize(t, g, ctx);
        boolean fontItalic = (boolean) ctx.getStyle(t, HStyleType.FONT_ITALIC).orElse(HStyles.fontItalic(false)).getValue();
        boolean fontBold = (boolean) ctx.getStyle(t, HStyleType.FONT_BOLD).orElse(HStyles.fontItalic(false)).getValue();
        String fontFamily = (String) ctx.getStyle(t, HStyleType.FONT_FAMILY).orElse(HStyles.fontFamily("Arial")).getValue();
        g.setFont(new Font(NStringUtils.firstNonBlank(fontFamily, "Arial").trim(), Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), (int) fontSize));
    }

    protected SizeD mapDim(double w, double h, HPartRendererContext ctx) {
        Rectangle2D.Double size = ctx.getBounds();
        return new SizeD(w / 100 * size.width, w / 100 * size.height);
    }

    protected Point2D.Double roundCornerArcs(HNode t, HPartRendererContext ctx) {
        return (Point2D.Double) ctx.getStyle(t, HStyleType.ROUND_CORNER).orElse(HStyles.roundCorner(null)).getValue();
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

    protected Point2D.Double size(HNode t, HPartRendererContext ctx) {
        Point2D.Double size = (Point2D.Double) ctx.getStyle(t, HStyleType.SIZE).orElse(HStyles.position(40, 40)).getValue();
        if (size == null) {
            size = new Point2D.Double(40, 40);
        }
        boolean shapeRatio = preserveShapeRatio(t, ctx);
        double ww = ctx.getBounds().width;
        double hh = ctx.getBounds().height;
        //ratio depends on the smallest
        if (shapeRatio) {
            if (ww < hh) {
                hh = ww;
            }
            if (hh < ww) {
                ww = hh;
            }
            return new Point2D.Double(
                    size.getX() / 100 * ww,
                    size.getY() / 100 * hh
            );
        }
        return new Point2D.Double(
                size.getX() / 100 * ww,
                size.getY() / 100 * hh
        );
    }

    protected Rectangle2D.Double bounds(HNode t, HPartRendererContext ctx) {
        Point2D.Double size = (Point2D.Double) ctx.getStyle(t, HStyleType.SIZE).orElse(HStyles.position(40, 40)).getValue();
        if (size == null) {
            size = new Point2D.Double(40, 40);
        }
        return new Rectangle2D.Double(
                ctx.getBounds().getX(),
                ctx.getBounds().getY(),
                size.getX() / 100 * ctx.getBounds().width,
                size.getY() / 100 * ctx.getBounds().height
        );
    }

    protected Rectangle2D.Double selfBounds(HNode t, HPartRendererContext ctx) {
        return selfBounds(t, null, ctx);
    }

    protected NOptional<Point2D.Double> readStyleAsPoint2D(HNode t, HStyleType s, HPartRendererContext ctx) {
        HStyle sv = ctx.getStyle(t, s).orNull();
        if(sv!=null){
            Object svv = sv.getValue();
            if(svv instanceof Point2D.Double){
                return NOptional.of((Point2D.Double) svv);
            }
            if(svv instanceof HAlign){
                switch ((HAlign)svv){
                    case TOP:return NOptional.of(new Point2D.Double(50,0));
                    case BOTTOM:return NOptional.of(new Point2D.Double(50,100));
                    case LEFT:return NOptional.of(new Point2D.Double(0,50));
                    case RIGHT:return NOptional.of(new Point2D.Double(100,50));
                    case TOP_LEFT:return NOptional.of(new Point2D.Double(0,0));
                    case CENTER:return NOptional.of(new Point2D.Double(50,50));
                    case TOP_RIGHT:return NOptional.of(new Point2D.Double(100,0));
                    case BOTTOM_RIGHT:return NOptional.of(new Point2D.Double(100,100));
                    case BOTTOM_LEFT:return NOptional.of(new Point2D.Double(0,100));
                    case NONE:{
                        break;
                    }
                }
            }
        }
        return NOptional.ofNamedEmpty(s.name());
    }

    protected Rectangle2D.Double selfBounds(HNode t, Point2D.Double selfSize, HPartRendererContext ctx) {
        if (selfSize == null) {
            selfSize = size(t, ctx);
        }
        Rectangle2D.Double parentBounds = ctx.getBounds();
        Point2D.Double pos = readStyleAsPoint2D(t, HStyleType.POSITION,ctx).orElse(new Point2D.Double(50,50));
        Point2D.Double origin = readStyleAsPoint2D(t, HStyleType.ORIGIN,ctx).orElse(new Point2D.Double(50,50));
        double pw = parentBounds.getWidth();
        double ph = parentBounds.getHeight();
        double sw = selfSize.getX();
        double sh = selfSize.getY();
        double x = pos.getX() / 100 * pw - origin.getX() / 100 * sw + parentBounds.getX();
        double y = pos.getY() / 100 * ph - origin.getY() / 100 * sh + parentBounds.getY();

        return new Rectangle2D.Double(x, y, selfSize.getX(), selfSize.getY());
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
        return true;
    }
    public boolean requireDrawGrid(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Boolean b = (Boolean) ctx.getStyle(t, HStyleType.DRAW_GRID).orElse(HStyles.drawGrid(false)).getValue();
        if(b==null){
            return false;
        }
        return true;
    }
    public boolean requireFillBackground(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Boolean b = (Boolean) ctx.getStyle(t, HStyleType.FILL_BACKGROUND).orElse(HStyles.drawGrid(false)).getValue();
        if(b==null){
            return false;
        }
        return true;
    }

    public boolean applyLineColor(HNode t, Graphics2D g, HPartRendererContext ctx) {
        Paint lineColor = (Paint) ctx.getStyle(t, HStyleType.LINE_COLOR).orElse(HStyles.lineColor(null)).getValue();
        if (lineColor != null) {
            if (lineColor instanceof Color) {
                g.setColor((Color) lineColor);
            } else {
                g.setPaint(lineColor);
            }
            return true;
        }
        return false;
    }

    protected void paintBorderLine(HNode t, HPartRendererContext ctx, Graphics2D g, Rectangle2D.Double a) {
        if (requireDrawContour(t, g, ctx)) {
            if (applyLineColor(t, g, ctx)) {
                g.drawRect((int) a.getMinX(), (int) a.getMinY(),
                        (int) a.getWidth(), (int) a.getHeight());
            }
        }
    }

    protected void paintBackground(HNode t, HPartRendererContext ctx, Graphics2D g, Rectangle2D.Double a) {
        if (requireFillBackground(t, g, ctx)) {
            if (applyBackgroundColor(t, g, ctx)) {
                g.fillRect((int) a.getMinX(), (int) a.getMinY(),
                        (int) a.getWidth(), (int) a.getHeight());
            }
        }
    }

    protected Rectangle2D.Double expand(Rectangle2D.Double a, Rectangle2D.Double b) {
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        Rectangle2D.Double r1 = a;
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
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

}
