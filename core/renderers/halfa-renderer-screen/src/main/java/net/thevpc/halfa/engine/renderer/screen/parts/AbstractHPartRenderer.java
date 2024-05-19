package net.thevpc.halfa.engine.renderer.screen.parts;

import net.thevpc.halfa.api.HStyles;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.PageView;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class AbstractHPartRenderer {


    public abstract Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx);


    protected SizeD mapDim(SizeD d, SizeD base) {
        return new SizeD(
                d.getWidth() / 100 * base.getWidth(),
                d.getHeight() / 100 * base.getHeight()
        );
    }

    protected double resolveFontSize(HPagePart t, Graphics2D g, HPartRendererContext ctx) {
        Rectangle2D.Double size = ctx.getBounds();
        double fontSize = (double) ctx.getStyle(t,HStyleType.FONT_SIZE).orElse(HStyles.fontSize(40.0)).getValue();
        fontSize = fontSize / Math.max(PageView.REF_SIZE.width, PageView.REF_SIZE.height) * Math.max(size.width, size.height);
        return fontSize;

    }

    protected void applyFont(HPagePart t, Graphics2D g, HPartRendererContext ctx) {
        Rectangle2D.Double size = ctx.getBounds();
        double fontSize = resolveFontSize(t, g,ctx);
        boolean fontItalic = (boolean) ctx.getStyle(t,HStyleType.FONT_ITALIC).orElse(HStyles.fontItalic(false)).getValue();
        boolean fontBold = (boolean) ctx.getStyle(t,HStyleType.FONT_BOLD).orElse(HStyles.fontItalic(false)).getValue();
        String fontFamily = (String) ctx.getStyle(t,HStyleType.FONT_FAMILY).orElse(HStyles.fontFamily("Arial")).getValue();
        g.setFont(new Font(NStringUtils.firstNonBlank(fontFamily, "Arial").trim(), Font.PLAIN | (fontItalic ? Font.ITALIC : 0) | (fontBold ? Font.BOLD : 0), (int) fontSize));
    }

    protected SizeD mapDim(double w, double h, HPartRendererContext ctx) {
        Rectangle2D.Double size = ctx.getBounds();
        return new SizeD(w / 100 * size.width, w / 100 * size.height);
    }

    protected Point2D.Double roundCornerArcs(HPagePart t, HPartRendererContext ctx) {
        return (Point2D.Double) ctx.getStyle(t,HStyleType.ROUND_CORNER).orElse(HStyles.roundCorner(null)).getValue();
    }

    protected Boolean get3D(HPagePart t, HPartRendererContext ctx) {
        return (Boolean) ctx.getStyle(t,HStyleType.THEED).orElse(HStyles.threeD(false)).getValue();
    }

    protected Boolean getRaised(HPagePart t, HPartRendererContext ctx) {
        return (Boolean) ctx.getStyle(t,HStyleType.RAISED).orElse(HStyles.raised(null)).getValue();
    }
    protected HLayout getLayout(HPagePart t, HPartRendererContext ctx) {
        return (HLayout) ctx.getStyle(t,HStyleType.LAYOUT).orElse(HStyles.layout(null)).getValue();
    }

    protected Point2D.Double size(HPagePart t, HPartRendererContext ctx) {
        Point2D.Double size = (Point2D.Double) ctx.getStyle(t,HStyleType.SIZE).orElse(HStyles.position(40, 40)).getValue();
        if (size == null) {
            size = new Point2D.Double(40, 40);
        }
        return new Point2D.Double(
                size.getX() / 100 * ctx.getBounds().width,
                size.getY() / 100 * ctx.getBounds().height
        );
    }

    protected Rectangle2D.Double bounds(HPagePart t, HPartRendererContext ctx) {
        Point2D.Double size = (Point2D.Double) ctx.getStyle(t,HStyleType.SIZE).orElse(HStyles.position(40, 40)).getValue();
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

    protected Point2D.Double pos(HPagePart t, Rectangle2D bound, HPartRendererContext ctx) {
        Point2D.Double pos = (Point2D.Double) ctx.getStyle(t,HStyleType.POSITION).orElse(HStyles.position(40, 40)).getValue();
        double x = pos.getX();
        double y = pos.getY();
        x = x / 100 * ctx.getBounds().width;
        y = y / 100 * ctx.getBounds().height;
        HAnchor posAnchor = (HAnchor) ctx.getStyle(t,HStyleType.POSITION_ANCHOR).orElse(HStyles.anchor(HAnchor.NORTH_WEST)).getValue();
        if (posAnchor == null) {
            posAnchor = HAnchor.NORTH_WEST;
        }


        double bw = bound.getWidth();
        double bh = bound.getHeight();
        double bminY = bound.getMinY();

        switch (posAnchor) {
            case NORTH_WEST: {
                break;
            }
            case NORTH_CENTER: {
                x -= bw / 2;
                break;
            }
            case CENTER: {
                x -= bw / 2;
                y -= bh / 2;
                break;
            }
        }
        return new Point2D.Double(x, y);
    }

    protected void applyForeground(HPagePart t, Graphics2D g, HPartRendererContext ctx) {
        Paint foregroundColor = (Paint) ctx.getStyle(t,HStyleType.FOREGROUND_COLOR).orElse(HStyles.foregroundColor(Color.BLACK)).getValue();
        if (foregroundColor == null) {
            g.setPaint(Color.BLACK);
        } else if (foregroundColor instanceof Color) {
            g.setColor((Color) foregroundColor);
        } else {
            g.setPaint(foregroundColor);
        }
    }

    public boolean applyBackgroundColor(HPagePart t, Graphics2D g, HPartRendererContext ctx) {
        Paint backgroundColor = (Paint) ctx.getStyle(t,HStyleType.BACKGROUND_COLOR).orElse(HStyles.backgroundColor(null)).getValue();
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

    public boolean applyLineColor(HPagePart t, Graphics2D g, HPartRendererContext ctx) {
        Paint lineColor = (Paint) ctx.getStyle(t,HStyleType.LINE_COLOR).orElse(HStyles.lineColor(null)).getValue();
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

    protected void paintBorderLine(HPagePart t, HPartRendererContext ctx, Graphics2D g, Rectangle2D.Double a) {
        if (applyLineColor(t, g, ctx)) {
            g.drawRect((int) a.getMinX(), (int) a.getMinY(),
                    (int) a.getWidth(), (int) a.getHeight());
        }
    }

    protected void paintBackground(HPagePart t, HPartRendererContext ctx, Graphics2D g, Rectangle2D.Double a) {
        if (applyBackgroundColor(t, g, ctx)) {
            g.fillRect((int) a.getMinX(), (int) a.getMinY(),
                    (int) a.getWidth(), (int) a.getHeight());
        }
    }


}
