package net.thevpc.halfa.engine.renderer.screen.renderers;

import net.thevpc.halfa.api.model.HArrayHead;
import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.model.elem2d.primitives.HElement2DLine;
import net.thevpc.halfa.api.model.elem2d.primitives.HElement2DPolygon;
import net.thevpc.halfa.api.model.elem2d.primitives.HElement2DPolyline;
import net.thevpc.halfa.api.model.elem3d.*;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DTriangle;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DArc;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DLine;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DPolygon;
import net.thevpc.halfa.api.model.elem3d.primitives.Element3DPolyline;
import net.thevpc.halfa.api.util.Colors;
import net.thevpc.halfa.engine.renderer.screen.renderers.elem2d.Element2DUIFactory;
import net.thevpc.halfa.engine.renderer.screen.renderers.elem3d.Element3DUIFactory;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.util.HUtils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;

public class HGraphicsImpl implements HGraphics {

    private Graphics2D g;
    private Color secondaryColor;
    private Projection3D projection3D = new Projection3D(1000);
    private Matrix3D transform3D = Matrix3D.identity();
    private Light3DImpl light3D = new Light3DImpl();
    private Element2DUIFactory element2DUIFactory = new Element2DUIFactory();
    private Element3DUIFactory element3DUIFactory = new Element3DUIFactory();
    private RenderState3D state = new RenderState3D() {
        @Override
        public HVector3D lightOrientation() {
            return light3D.orientation();
        }

        @Override
        public HElement3DPrimitive[] toPrimitives(HElement3D e) {
            return element3DUIFactory.toPrimitives(e, this);
        }
    };

    @Override
    public Font getFont() {
        return g.getFont();
    }

    public HGraphicsImpl(Graphics2D g) {
        this.g = g;
    }

    @Override
    public Light3D getLight3D() {
        return light3D;
    }

    @Override
    public Light3D setLight3D(Light3D light3D) {
        return light3D;
    }

    @Override
    public void setColor(Color c) {
        g.setColor(c);

    }

    @Override
    public AffineTransform getTransform(){
        return g.getTransform();
    }

    @Override
    public void setTransform(AffineTransform tx){
        g.setTransform(tx);
    }
    @Override
    public void transform(AffineTransform tx){
        g.transform(tx);
    }
    @Override
    public void scale(double sx,double sy){
        g.scale(sx,sy);
    }
    @Override
    public void rotate(double theta,double x,double y){
        g.rotate(theta,x,y);
    }
    @Override
    public void rotate(double theta){
        g.rotate(theta);
    }


    @Override
    public void setPaint(Paint c) {
        g.setPaint(c);
    }

    @Override
    public void drawArrayHead(HPoint2D origin, Vector2D direction, double arrowWidth, double arrowHeight, HArrayHead head) {
        if (head == null) {
            return;
        }
        int[] xPoints;
        int[] yPoints;
        switch (head) {
            case NONE: {
                return;
            }
            case ARROW: {
                xPoints = new int[]{0, -(int) arrowWidth, -(int) arrowWidth};
                yPoints = new int[]{0, -(int) arrowHeight, (int) arrowHeight};
                break;
            }
            default: {
                xPoints = new int[]{0, -(int) arrowWidth, -(int) arrowWidth};
                yPoints = new int[]{0, -(int) arrowHeight, (int) arrowHeight};
            }
        }
        double angle = Math.atan2(direction.y, direction.x);
        Polygon arrowHead = new Polygon(xPoints, yPoints, xPoints.length);
        AffineTransform originalTransform = g.getTransform();
        g.translate(origin.x, origin.y);
        g.rotate(angle);
        g.fill(arrowHead);
        g.setTransform(originalTransform);
    }

    @Override
    public void draw2D(HElement2D element2D) {
        Element2DPrimitive[] primitives = element2DUIFactory.toPrimitives(element2D);
        for (Element2DPrimitive primitive : primitives) {
            switch (primitive.type()) {
                case LINE: {
                    HElement2DLine pr = (HElement2DLine) primitive;
                    HPoint2D a = pr.getFrom();
                    HPoint2D b = pr.getTo();
                    Paint oldPaint = g.getPaint();
                    Stroke oldStroke = g.getStroke();
                    if (pr.getLinePaint() != null) {
                        setPaint(pr.getLinePaint());
                    }
                    if (pr.getLineStroke() != null) {
                        setStroke(pr.getLineStroke());
                    }
                    g.drawLine(
                            (int) a.x,
                            (int) a.y,
                            (int) b.x,
                            (int) b.y
                    );
                    //restore default stroke?
                    setStroke(oldStroke);
                    drawArrayHead(a, a.minus(b).asVector(), 5, 5, pr.getStartType());
                    drawArrayHead(b, b.minus(a).asVector(), 5, 5, pr.getEndType());
                    setPaint(oldPaint);
                    setStroke(oldStroke);
                    break;
                }
                case POLYLINE: {
                    HElement2DPolyline pr = (HElement2DPolyline) primitive;
                    Paint oldPaint = g.getPaint();
                    Stroke oldStroke = g.getStroke();
                    if (pr.getLinePaint() != null) {
                        setPaint(pr.getLinePaint());
                    }
                    if (pr.getLineStroke() != null) {
                        setStroke(pr.getLineStroke());
                    }
                    HPoint2D[] nodes = pr.getNodes();
                    int[] xx = new int[nodes.length];
                    int[] yy = new int[nodes.length];
                    for (int i = 0; i < xx.length; i++) {
                        HPoint2D pp = nodes[i];
                        xx[i] = (int) (pp.x);
                        yy[i] = (int) (pp.y);
                    }

                    g.drawPolyline(xx, yy, xx.length);
                    //restore default stroke?
                    setStroke(oldStroke);
//                    drawArrayHead(a, a.minus(b).asVector(), 5, 5, pr.getStartType());
//                    drawArrayHead(b, b.minus(a).asVector(), 5, 5, pr.getEndType());
                    setPaint(oldPaint);
                    setStroke(oldStroke);
                    break;
                }
                case POLYGON: {
                    HElement2DPolygon pr = (HElement2DPolygon) primitive;
                    Paint oldPaint = g.getPaint();
                    Stroke oldStroke = g.getStroke();
                    if (pr.getLinePaint() != null) {
                        setPaint(pr.getLinePaint());
                    }
                    if (pr.getLineStroke() != null) {
                        setStroke(pr.getLineStroke());
                    }
                    HPoint2D[] nodes = pr.getNodes();
                    int[] xx = new int[nodes.length];
                    int[] yy = new int[nodes.length];
                    for (int i = 0; i < xx.length; i++) {
                        HPoint2D pp = nodes[i];
                        xx[i] = (int) (pp.x);
                        yy[i] = (int) (pp.y);
                    }

                    if (pr.isFill()) {
                        Composite oldComposite = g.getComposite();

                        if (pr.getLinePaint() != null) {
                            setPaint(pr.getLinePaint());
                        }
                        if (pr.getLineStroke() != null) {
                            setStroke(pr.getLineStroke());
                        }

                        Paint bg = pr.getBackgroundPaint();
                        if (bg != null) {
                            setPaint(bg);
                        }
                        if (pr.getComposite() != null) {
                            setComposite(pr.getComposite());
                        }
                        g.fillPolygon(xx, yy, xx.length);
                        setPaint(oldPaint);
                        setComposite(oldComposite);
                    }
                    if (pr.isContour()) {
                        if (pr.getLinePaint() != null) {
                            setPaint(pr.getLinePaint());
                        }
                        if (pr.getLineStroke() != null) {
                            setStroke(pr.getLineStroke());
                        }
                        g.drawPolygon(xx, yy, xx.length);
                        setPaint(oldPaint);
                        setStroke(oldStroke);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void draw3D(HElement3D element3D, HPoint2D origin) {
        HElement3DPrimitive[] primitives = element3DUIFactory.toPrimitives(element3D, state);
        double x = origin.x;
        double y = origin.y;
        for (HElement3DPrimitive primitive : primitives) {
            switch (primitive.type()) {
                case LINE: {
                    Element3DLine pr = (Element3DLine) primitive;
                    HPoint3D p1 = pr.getFrom().transform(transform3D);
                    HPoint3D p2 = pr.getTo().transform(transform3D);

                    HPoint2D point1 = projection3D.project(p1).plus(origin);
                    HPoint2D point2 = projection3D.project(p2).plus(origin);

                    draw2D(
                            new HElement2DLine(point1, point2)
                                    .setStartType(pr.getStartType())
                                    .setEndType(pr.getEndType())
                                    .setComposite(pr.getComposite())
                                    .setBackgroundPaint(pr.getBackgroundPaint())
                                    .setLinePaint(pr.getLinePaint())
                                    .setLineStroke(pr.getLineStroke())
                    );

                    break;
                }
                case ARC: {
                    Element3DArc pr = (Element3DArc) primitive;
                    HPoint3D p1 = pr.getFrom().transform(transform3D);
                    HPoint3D p2 = pr.getTo().transform(transform3D);

                    HPoint2D point1 = projection3D.project(p1);
                    HPoint2D point2 = projection3D.project(p2);
                    double xmin = Math.min(point1.x + x, point2.x + x);
                    double w = Math.abs(point1.x - point2.x);
                    double ymin = Math.min(point1.y + y, point2.y + y);
                    double h = Math.abs(point1.y - point2.y);
                    Paint oldPaint = g.getPaint();
                    Stroke oldStroke = g.getStroke();
                    if (pr.getLinePaint() != null) {
                        setPaint(pr.getLinePaint());
                    }
                    if (pr.getLineStroke() != null) {
                        setStroke(pr.getLineStroke());
                    }
                    drawArc(
                            xmin, ymin,
                            w,
                            h,
                            pr.getStartAngle(),
                            pr.getEndAngle()
                    );
                    setPaint(oldPaint);
                    setStroke(oldStroke);
                    break;
                }
                case POLYGON: {
                    Element3DPolygon pr = (Element3DPolygon) primitive;
                    HPoint3D[] nodes = pr.getNodes();
                    double[] xx = new double[nodes.length];
                    double[] yy = new double[nodes.length];
                    for (int i = 0; i < xx.length; i++) {
                        HPoint3D p = nodes[i].transform(transform3D);
                        HPoint2D pp = projection3D.project(p);
                        xx[i] = (pp.x + x);
                        yy[i] = (pp.y + y);
                    }
                    double d = D3Utils.surfaceNormal(nodes[0], nodes[1], nodes[2]).dot(getLight3D().orientation());
                    if (pr.isFill()) {

                        Paint oldPaint = g.getPaint();
                        Composite oldComposite = g.getComposite();

                        if (pr.getLinePaint() != null) {
                            setPaint(pr.getLinePaint());
                        }
                        if (pr.getLineStroke() != null) {
                            setStroke(pr.getLineStroke());
                        }

                        Paint bg = pr.getBackgroundPaint();
                        if (bg == null) {
                            bg = getColor();
                        }
                        if (bg instanceof Color) {
                            setColor(Colors.withB((Color) bg //                                        , Math.abs(1 - (float) d)
                                    ,
                                    Math.abs((float) d)
                                    //, Math.abs(Math.abs((float) d))
                            ));
                        } else {
                            setPaint(bg);
                        }
                        if (pr.getComposite() != null) {
                            setComposite(pr.getComposite());
                        }
                        fillPolygon(xx, yy, xx.length);
                        setPaint(oldPaint);
                        setComposite(oldComposite);
                    }
                    if (pr.isContour()) {
                        Paint oldPaint = g.getPaint();
                        Stroke oldStroke = g.getStroke();
                        if (pr.getLinePaint() != null) {
                            setPaint(pr.getLinePaint());
                        }
                        if (pr.getLineStroke() != null) {
                            setStroke(pr.getLineStroke());
                        }
                        drawPolygon(xx, yy, xx.length);
                        setPaint(oldPaint);
                        setStroke(oldStroke);
                    }
                    break;
                }

                case POLYLINE: {
                    Element3DPolyline pr = (Element3DPolyline) primitive;
                    HPoint3D[] nodes = pr.getNodes();
                    double[] xx = new double[nodes.length];
                    double[] yy = new double[nodes.length];
                    for (int i = 0; i < xx.length; i++) {
                        HPoint3D p = nodes[i].transform(transform3D);
                        HPoint2D pp = projection3D.project(p);
                        xx[i] = (pp.x + x);
                        yy[i] = (pp.y + y);
                    }
                    Paint oldPaint = g.getPaint();
                    Stroke oldStroke = g.getStroke();
                    if (pr.getLinePaint() != null) {
                        setPaint(pr.getLinePaint());
                    }
                    if (pr.getLineStroke() != null) {
                        setStroke(pr.getLineStroke());
                    }
                    drawPolyline(xx, yy, xx.length);
                    setPaint(oldPaint);
                    setStroke(oldStroke);
                    break;
                }
                case TRIANGLE: {
                    Element3DTriangle pr = (Element3DTriangle) primitive;
                    double[] xx = new double[3];
                    double[] yy = new double[3];

                    HPoint3D p1 = pr.getP1();
                    HPoint2D pp1 = projection3D.project(p1.transform(transform3D));
                    xx[0] = (pp1.x + x);
                    yy[0] = (pp1.y + y);

                    HPoint3D p2 = pr.getP2();
                    HPoint2D pp2 = projection3D.project(p2.transform(transform3D));
                    xx[1] = (pp2.x + x);
                    yy[1] = (pp2.y + y);

                    HPoint3D p3 = pr.getP3();
                    HPoint2D pp3 = projection3D.project(p3.transform(transform3D));
                    xx[2] = (pp3.x + x);
                    yy[2] = (pp3.y + y);

                    double d = D3Utils.surfaceNormal(p1, p2, p3).dot(getLight3D().orientation());
                    if (true/*d < 0*/) {
                        if (pr.isFill()) {
                            Paint oldPaint = g.getPaint();
                            Composite oldComposite = g.getComposite();

                            if (pr.getLinePaint() != null) {
                                setPaint(pr.getLinePaint());
                            }
                            if (pr.getLineStroke() != null) {
                                setStroke(pr.getLineStroke());
                            }

                            Paint bg = pr.getBackgroundPaint();
                            if (bg == null) {
                                bg = getColor();
                            }
                            if (bg instanceof Color) {
                                setColor(Colors.withB((Color) bg //                                        , Math.abs(1 - (float) d)
                                        ,
                                        Math.abs((float) d)
                                        //, Math.abs(Math.abs((float) d))
                                ));
                            } else {
                                setPaint(bg);
                            }
                            if (pr.getComposite() != null) {
                                setComposite(pr.getComposite());
                            }
                            fillPolygon(xx, yy, xx.length);
                            setPaint(oldPaint);
                            setComposite(oldComposite);
                        }
                        if (false && pr.isContour()) {
                            Paint oldPaint = g.getPaint();
                            Stroke oldStroke = g.getStroke();
                            if (pr.getLinePaint() != null) {
                                setPaint(pr.getLinePaint());
                            }
                            if (pr.getLineStroke() != null) {
                                setStroke(pr.getLineStroke());
                            }
                            drawPolygon(xx, yy, xx.length);
                            setPaint(oldPaint);
                            setStroke(oldStroke);
                        }
                    }
                    break;
                }
            }
        }
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
    public Rectangle2D getStringBounds(AttributedCharacterIterator iterator) {
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout textLayout = new TextLayout(iterator, frc);
        return textLayout.getBounds();
    }

    @Override
    public FontMetrics getFontMetrics() {
        return g.getFontMetrics();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return g.getFontMetrics(f);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return g.getFontRenderContext();
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
    public void setFont(Font font) {
        g.setFont(font);
    }

    @Override
    public void drawString(String str, double x, double y) {
        g.drawString(str == null ? "" : str, (int) x, (int) y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, double x, double y) {
        g.drawString(iterator, (float) x, (float) y);
    }

    @Override
    public void debugString(String str, double x, double y) {
        Color c = g.getColor();
        Font of = g.getFont();
        g.setColor(Color.RED);
        Font arial = new Font("Courrier", Font.PLAIN, 16);
        g.setFont(arial);
        int debugRow = 0;
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
                Arrays.stream(xx).mapToInt(d -> (int) d).toArray(),
                Arrays.stream(yy).mapToInt(d -> (int) d).toArray(),
                length
        );
    }

    @Override
    public void drawPolygon(double[] xx, double[] yy, int length) {
        g.drawPolygon(
                Arrays.stream(xx).mapToInt(d -> (int) d).toArray(),
                Arrays.stream(yy).mapToInt(d -> (int) d).toArray(),
                length
        );
    }

    @Override
    public void drawPolyline(double[] xx, double[] yy, int length) {
        g.drawPolyline(
                Arrays.stream(xx).mapToInt(d -> (int) d).toArray(),
                Arrays.stream(yy).mapToInt(d -> (int) d).toArray(),
                length
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





    public void transform3D(Matrix3D transform3D) {
        if (transform3D != null) {
            this.transform3D = this.transform3D.multiply(transform3D);
        }
    }

    @Override
    public void project3D(Projection3D projection3D) {
        if (projection3D != null) {
            this.projection3D = projection3D;
        }
    }

    public void shear(double shx,double shy){
        g.shear(shx,shy);
    }


}
