package net.thevpc.halfa.engine.renderer.screen.renderers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.util.HUtils;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.Arrays;

public class HGraphicsImpl implements HGraphics {
    private Graphics2D g;
    private Color secondaryColor;

    public HGraphicsImpl(Graphics2D g) {
        this.g = g;
    }


    @Override
    public void setColor(Color c) {
        g.setColor(c);
    }

    @Override
    public void fillOval(double x, double y, double w, double h) {
        g.fillOval((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void drawOval(double x, double y, double w, double h) {
        g.drawOval((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void fillRect(double x, double y, double w, double h) {
        g.fillRect((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void drawRect(double x, double y, double w, double h) {
        g.drawRect((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public Rectangle2D getStringBounds(String str) {
        return getFontMetrics().getStringBounds(str == null ? "" : str, context());
    }

    @Override
    public FontMetrics getFontMetrics() {
        return g.getFontMetrics();
    }

    @Override
    public void fillRect(Bounds2 a) {
        fillRect(
                HUtils.doubleOf(a.getMinX()), HUtils.intOf(a.getMinY()),
                HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
        );
    }

    @Override
    public void drawRect(Bounds2 a) {
        drawRect(
                HUtils.doubleOf(a.getMinX()), HUtils.intOf(a.getMinY()),
                HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
        );
    }

    @Override
    public Graphics2D context() {
        return g;
    }

    @Override
    public void setPaint(Paint paint) {
        g.setPaint(paint);
    }

    @Override
    public void setFont(Font font) {
        g.setFont(font);
    }

    @Override
    public void drawString(String str, double x, double y) {
        g.drawString(str == null ? "" : str, (int) x, (int) y);
    }

    @Override
    public void debugString(String str, double x, double y) {
        Color c = g.getColor();
        Font of = g.getFont();
        g.setColor(Color.RED);
        Font arial = new Font("Courrier", Font.PLAIN, 16);
        g.setFont(arial);
        int debugRow=0;
        Rectangle2D fm = g.getFontMetrics(arial).getStringBounds("Abcdefghijklmnopqrstuvwxyz", g);
        for (String n : String.valueOf(str).trim().split("\n")) {
            g.drawString(n, (int) x, (int) (y + fm.getHeight() * debugRow));
            debugRow++;
        }
        g.setFont(of);
        g.setColor(c);
    }

    @Override
    public Color getColor() {
        return g.getColor();
    }

    @Override
    public void drawArc(double x, double y, double w, double h, double startAngle, double endAngle) {
        g.drawArc((int) x, (int) y, (int) w, (int) h, (int) startAngle, (int) endAngle);
    }

    @Override
    public void drawLine(double x1, double y1, double x2, double y2) {
        g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }

    @Override
    public void fillPolygon(double[] xx, double[] yy, int length) {
        g.fillPolygon(
                Arrays.stream(xx).mapToInt(d -> (int) d).toArray()
                , Arrays.stream(yy).mapToInt(d -> (int) d).toArray()
                , length
        );
    }

    @Override
    public void drawPolygon(double[] xx, double[] yy, int length) {
        g.drawPolygon(
                Arrays.stream(xx).mapToInt(d -> (int) d).toArray()
                , Arrays.stream(yy).mapToInt(d -> (int) d).toArray()
                , length
        );
    }

    @Override
    public void drawPolyline(double[] xx, double[] yy, int length) {
        g.drawPolyline(
                Arrays.stream(xx).mapToInt(d -> (int) d).toArray()
                , Arrays.stream(yy).mapToInt(d -> (int) d).toArray()
                , length
        );
    }

    @Override
    public void fillRoundRect(double x, double y, double w, double h, double cx, double cy) {
        g.fillRoundRect((int) x, (int) y, (int) w, (int) h, (int) cx, (int) cy);
    }

    @Override
    public void drawRoundRect(double x, double y, double w, double h, double cx, double cy) {
        g.drawRoundRect((int) x, (int) y, (int) w, (int) h, (int) cx, (int) cy);
    }

    @Override
    public void fill3DRect(double x, double y, double w, double h, boolean raised) {
        g.fill3DRect((int) x, (int) y, (int) w, (int) h, raised);
    }

    @Override
    public void draw3DRect(double x, double y, double w, double h, boolean raised) {
        g.fill3DRect((int) x, (int) y, (int) w, (int) h, raised);
    }

    @Override
    public void drawImage(Image image, double x, double y, ImageObserver o) {
        g.drawImage(image, (int) x, (int) y, o);
    }

    @Override
    public Color getSecondaryColor() {
        return secondaryColor;
    }

    @Override
    public void setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    @Override
    public void setComposite(Composite composite) {
        g.setComposite(composite);
    }

    @Override
    public void fill(Shape shape) {
        g.fill(shape);
    }

    @Override
    public void setStroke(Stroke stroke) {
        g.setStroke(stroke);
    }

    @Override
    public Stroke getStroke() {
        return g.getStroke();
    }

    @Override
    public void fillSphere(double x, double y, double w, double h, double lightAngle, float radius) {
        Color c = getColor();
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        int rgb = c.getRGB();
        float hue = (rgb) / 255f; // if you want to use 0-255 range
        double wh = Math.max(w, h);

        double x0 = x + w / 2;
        double y0 = y + h / 2;
        double radAngle = lightAngle / 180 * Math.PI;
        if (radius > 100) {
            radius = 100;
        } else if (radius < 0) {
            radius = 0;
        }

        int max = (wh <= 10) ? 10 : (wh <= 100) ? 30 : (wh <= 200) ? 50 : 100;
        for (int i = 0; i <= max; i++) {
            double r = i * 1.0 / max * radius;
            double lx = x0 + Math.cos(radAngle) * (r / 100) * w / 2;
            double ly = y0 + Math.sin(radAngle) * (r / 100) * h / 2;
            double lw = (radius - r) / 100 * w * 2;
            double lh = (radius - r) / 100 * h * 2;
            setColor(Color.getHSBColor(hsb[0], 1 - (i * 1.f / max), hsb[2]));
            fillOval(lx - (lw) / 2, ly - lh / 2, lw, lh);
        }
    }

}
