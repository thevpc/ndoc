package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocArrow;
import net.thevpc.ndoc.api.document.NDocArrowType;
import net.thevpc.ndoc.api.document.elem3d.primitives.NDocElement3DTriangle;
import net.thevpc.ndoc.api.document.elem3d.primitives.NDocElement3DArc;
import net.thevpc.ndoc.api.document.elem3d.primitives.NDocElement3DLine;
import net.thevpc.ndoc.api.document.elem3d.primitives.NDocElement3DPolygon;
import net.thevpc.ndoc.api.document.elem3d.primitives.NDocElement3DPolyline;
import net.thevpc.ndoc.api.util.Colors;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.renderer.elem2d.Element2DUIFactory;
import net.thevpc.ndoc.engine.renderer.elem2d.ShapeFactory;
import net.thevpc.ndoc.engine.renderer.elem3d.NDocLight3DImpl;
import net.thevpc.ndoc.engine.renderer.elem2d.strokes.CompositeStroke;
import net.thevpc.ndoc.engine.renderer.elem2d.strokes.StrokeFactory;
import net.thevpc.ndoc.engine.renderer.elem3d.Element3DUIFactory;
import net.thevpc.ndoc.api.renderer.NDocImageTypeRendererFactory;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocGraphicsImageDrawer;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.ndoc.api.document.elem2d.primitives.*;
import net.thevpc.ndoc.api.document.elem2d.*;
import net.thevpc.ndoc.api.document.elem3d.*;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

public class NDocGraphicsImpl implements NDocGraphics {

    private Graphics2D g;
    private NDocEngine engine;
    private Color secondaryColor;
    private NDocProjection3D projection3D = new NDocProjection3D(1000);
    private NDocMatrix3D transform3D = NDocMatrix3D.identity();
    private NDocLight3DImpl light3D = new NDocLight3DImpl();
    private Element2DUIFactory element2DUIFactory = new Element2DUIFactory();
    private Element3DUIFactory element3DUIFactory = new Element3DUIFactory();
    private List<NDocImageTypeRendererFactory> imageTypeRendererFactories = new ArrayList<>();
    private Map<Object, NDocGraphicsImageDrawer> imageCache = new HashMap<>();

    private RenderState3D state = new RenderState3D() {
        @Override
        public NDocVector3D lightOrientation() {
            return light3D.orientation();
        }

        @Override
        public NDocElement3DPrimitive[] toPrimitives(NDocElement3D e) {
            return element3DUIFactory.toPrimitives(e, this);
        }
    };

    public NDocGraphicsImpl(Graphics2D g,NDocEngine engine) {
        this.g = g;
        this.engine = engine;
        ServiceLoader<NDocImageTypeRendererFactory> sl = ServiceLoader.load(NDocImageTypeRendererFactory.class);
        for (NDocImageTypeRendererFactory s : sl) {
            imageTypeRendererFactories.add(s);
        }
    }

    public NDocGraphicsImpl(Graphics2D g, NDocImageTypeRendererFactory[] factories,NDocEngine engine) {
        this.g = g;
        this.engine = engine;
        this.imageTypeRendererFactories.addAll(Arrays.asList(factories));
    }

    @Override
    public NDocEngine engine() {
        return engine;
    }

    @Override
    public NDocGraphics copy() {
        NDocGraphicsImpl hGraphics = new NDocGraphicsImpl((Graphics2D) g.create(),
                imageTypeRendererFactories.toArray(new NDocImageTypeRendererFactory[0]),
                engine
        );
        hGraphics.transform3D = transform3D;
        hGraphics.light3D = light3D;
        hGraphics.projection3D = projection3D;
        return hGraphics;
    }

    @Override
    public Font getFont() {
        return g.getFont();
    }


    @Override
    public NDocLight3D getLight3D() {
        return light3D;
    }

    @Override
    public NDocLight3D setLight3D(NDocLight3D light3D) {
        return light3D;
    }

    @Override
    public void setColor(Color c) {
        g.setColor(c);

    }

    @Override
    public AffineTransform getTransform() {
        return g.getTransform();
    }

    @Override
    public void setTransform(AffineTransform tx) {
        g.setTransform(tx);
    }

    @Override
    public void transform(AffineTransform tx) {
        g.transform(tx);
    }

    @Override
    public void scale(double sx, double sy) {
        g.scale(sx, sy);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        g.rotate(theta, x, y);
    }

    @Override
    public void rotate(double theta) {
        g.rotate(theta);
    }

    @Override
    public void dispose() {
        g.dispose();
    }


    public Shape createShape(NElement e) {
        return ShapeFactory.createShape(e);
    }

    public Stroke createStroke(NElement e) {
        if (e == null || e.isNull()) {
            return null;
        }
        NDocValue o = NDocValue.of(e);
        if (o.name() != null) {
            return StrokeFactory.createStroke(o.name(), e, this);
        }
        switch (e.type()) {
            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY:
            {
                return CompositeStroke.of(e, this);
            }
            case UPLET:
            case NAMED_UPLET:
            {
                return StrokeFactory.createBasic(o);
            }
            case NAME:
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                return StrokeFactory.createStroke(o.asStringOrName().get(), e, this);
            }
        }
        {
            NOptional<Float> d = o.asFloat();
            if (d.isPresent()) {
                return new BasicStroke(d.get());
            }
        }
        return new BasicStroke();
    }

    @Override
    public void setPaint(Paint c) {
        g.setPaint(c);
    }

    @Override
    public void drawArrayHead(NDocPoint2D origin, Vector2D direction, NDocArrow arrow) {
        if (arrow == null || arrow.getType() == null) {
            return;
        }
        int[] xPoints;
        int[] yPoints;
        NDocArrowType type = arrow.getType();
        if (type == NDocArrowType.DEFAULT) {
            type = NDocArrowType.SIMPLE;
        }
        double arrowWidth = arrow.getWidth();
        double arrowHeight = arrow.getHeight();
        if (arrowWidth <= 0) {
            arrowWidth = arrowHeight;
        }
        if (arrowHeight <= 0) {
            arrowHeight = arrowWidth;
        }

        double angle = Math.atan2(direction.y, direction.x);
        AffineTransform originalTransform = g.getTransform();
        g.translate(origin.x, origin.y);
        g.rotate(angle);
        switch (type) {
            case SIMPLE: {
                if (arrowHeight <= 0) {
                    arrowHeight = 10;
                }
                if (arrowWidth <= 0) {
                    arrowWidth = 10;
                }
                xPoints = new int[]{-(int) arrowWidth, 0, -(int) arrowWidth};
                yPoints = new int[]{-(int) arrowHeight, 0, (int) arrowHeight};
                g.drawPolyline(xPoints, yPoints, xPoints.length);
                break;
            }
            case TRIANGLE_FULL:
            case TRIANGLE: {
                if (arrowHeight <= 0) {
                    arrowHeight = 10;
                }
                if (arrowWidth <= 0) {
                    arrowWidth = 10;
                }
                xPoints = new int[]{0, -(int) arrowWidth, -(int) arrowWidth};
                yPoints = new int[]{0, -(int) arrowHeight, (int) arrowHeight};
                Polygon arrowHead = new Polygon(xPoints, yPoints, xPoints.length);
                if (type == NDocArrowType.TRIANGLE_FULL) {
                    g.fill(arrowHead);
                } else {
                    g.draw(arrowHead);
                }
                break;
            }
            case DIAMOND_FULL:
            case DIAMOND: {
                if (arrowHeight <= 0) {
                    arrowHeight = 50;
                }
                if (arrowWidth <= 0) {
                    arrowWidth = 50;
                }
                double xmin = -arrowWidth / 2;
                double ymin = -arrowHeight / 2 + arrowHeight / 4;
                double xlen = arrowWidth / 2;
                double ylen = arrowHeight / 2;
                xPoints = new int[]{(int) (xmin + xlen / 2), (int) (xmin + xlen), (int) (xmin + xlen / 2), (int) (xmin)};
                yPoints = new int[]{(int) (ymin), (int) (ymin + ylen / 2), (int) (ymin + ylen), (int) (ymin + ylen / 2)};
                Polygon arrowHead = new Polygon(xPoints, yPoints, xPoints.length);
                if (type == NDocArrowType.DIAMOND_FULL) {
                    g.fill(arrowHead);
                } else {
                    g.draw(arrowHead);
                }
                break;
            }
            case OVAL_FULL:
            case OVAL: {
                if (arrowHeight <= 0) {
                    arrowHeight = 30;
                }
                if (arrowWidth <= 0) {
                    arrowWidth = 30;
                }
                double xmin = -arrowWidth / 2;
                double ymin = -arrowHeight / 2 + arrowHeight / 4;
                double xlen = arrowWidth / 2;
                double ylen = arrowHeight / 2;
                if (type == NDocArrowType.OVAL_FULL) {
                    fillOval(xmin, ymin, xlen, ylen);
                } else {
                    drawOval(xmin, ymin, xlen, ylen);
                }
                break;
            }
            case RECTANGLE:
            case RECTANGLE_FULL: {
                if (arrowHeight <= 0) {
                    arrowHeight = 30;
                }
                if (arrowWidth <= 0) {
                    arrowWidth = 30;
                }
                double xmin = -arrowWidth / 2;
                double ymin = -arrowHeight / 2 + arrowHeight / 4;
                double xlen = arrowWidth / 2;
                double ylen = arrowHeight / 2;
                if (type == NDocArrowType.RECTANGLE_FULL) {
                    fillRect(xmin, ymin, xlen, ylen);
                } else {
                    drawRect(xmin, ymin, xlen, ylen);
                }
                break;
            }
            default: {
                //unsupported arrow type
                break;
            }
        }
        g.setTransform(originalTransform);
    }

    @Override
    public void draw2D(NDocElement2D element2D) {
        NDocElement2DPrimitive[] primitives = element2DUIFactory.toPrimitives(element2D);
        for (NDocElement2DPrimitive primitive : primitives) {
            switch (primitive.type()) {
                case LINE: {
                    draw2DHElement2DLine((NDocElement2DLine) primitive);
                    break;
                }
                case QUAD_CURVE: {
                    draw2DHElement2DQuadCurve((NDocElement2DQuadCurve) primitive);
                    break;
                }
                case CUBIC_CURVE: {
                    draw2DHElement2DCubicCurve((NDocElement2DCubicCurve) primitive);
                    break;
                }
                case POLYLINE: {
                    draw2DHElement2DPolyline((NDocElement2DPolyline) primitive);
                    break;
                }
                case POLYGON: {
                    draw2DHElement2DPolygon((NDocElement2DPolygon) primitive);
                    break;
                }
            }
        }
    }

    @Override
    public void draw3D(NDocElement3D element3D, NDocPoint2D origin) {
        NDocElement3DPrimitive[] primitives = element3DUIFactory.toPrimitives(element3D, state);
        double x = origin.x;
        double y = origin.y;
        for (NDocElement3DPrimitive primitive : primitives) {
            switch (primitive.type()) {
                case LINE: {
                    draw3DElement3DLine((NDocElement3DLine) primitive, origin);
                    break;
                }
                case ARC: {
                    draw3DElement3DArc((NDocElement3DArc) primitive, origin);
                    break;
                }
                case POLYGON: {
                    draw3DElement3DPolygon((NDocElement3DPolygon) primitive, origin);
                    break;
                }

                case POLYLINE: {
                    draw3DElement3DPolyline((NDocElement3DPolyline) primitive, origin);
                    break;
                }
                case TRIANGLE: {
                    draw3DElement3DTriangle((NDocElement3DTriangle) primitive, origin);
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
        return getFontMetrics().getStringBounds(str == null ? "" : str, graphics2D());
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
    public void fillRect(NDocBounds2 a) {
        fillRect(
                NDocUtils.doubleOf(a.getMinX()), NDocUtils.intOf(a.getMinY()),
                NDocUtils.intOf(a.getWidth()), NDocUtils.intOf(a.getHeight())
        );
    }

    @Override
    public void drawRect(NDocBounds2 a) {
        drawRect(
                NDocUtils.doubleOf(a.getMinX()), NDocUtils.intOf(a.getMinY()),
                NDocUtils.intOf(a.getWidth()), NDocUtils.intOf(a.getHeight())
        );
    }

    @Override
    public Graphics2D graphics2D() {
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
    public void drawString(String str, double x, double y, NDocTextOptions options) {
        if (str == null || str.length() == 0) {
            return;
        }
        Paint oldPaint = g.getPaint();
        Font oldFont = g.getFont();
        Stroke oldStroke = g.getStroke();
        if (options.getStroke() != null) {
            g.setStroke(options.getStroke());
        }
        if (options.getFont() != null) {
            g.setFont(options.getFont());
        }
        NDocPoint2D sht = options.getShadowTranslation();
        if (options.getShadowColor() != null && sht != null && !sht.equals(new NDocPoint2D(0, 0))) {
            if (options.isStyled()) {
                AttributedString attrStr = options.createShadowAttributedString(str, g);
                g.drawString(attrStr.getIterator(), (float) (x + sht.getX()), (float) (y + sht.getY()));
            } else {
                g.setPaint(options.getShadowColor());
                g.drawString(str, (float) (x + sht.getX()), (float) (y + sht.getY()));
            }
        }
        {
            if (options.isStyled()) {
                AttributedString attrStr = options.createAttributedString(str, g);
                g.drawString(attrStr.getIterator(), (float) (x), (float) (y));
            } else {
                g.setPaint(options.getForegroundColor());
                g.drawString(str, (float) (x), (float) (y));
            }
        }
        g.setPaint(oldPaint);
        g.setFont(oldFont);
        g.setStroke(oldStroke);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, double x, double y) {
        if (iterator == null) {
            return;
        }
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
        g.draw3DRect((int) x, (int) y, (int) w, (int) h, raised);
    }

    @Override
    public void drawImage(Image image, double x, double y, ImageObserver o) {
        g.drawImage(image, (int) x, (int) y, o);
    }

    @Override
    public void drawImage(NPath nPath, double x, double y, NDocImageOptions options) {
        NDocGraphicsImageDrawer o = imageCache.get(nPath);
        if (o == null) {
            NOptional<NDocGraphicsImageDrawer> tr = NCallableSupport.resolve(
                            imageTypeRendererFactories.stream()
                                    .map(n -> n.resolveRenderer(nPath, options, NDocGraphicsImpl.this)),
                            () -> NMsg.ofC("support for image %s ", nPath))
                    .toOptional();
            if (tr.isPresent()) {
                putCache(new Object[]{nPath}, o = tr.get());
            }
        }
        if (o != null) {
            o.drawImage(x, y, options, this);
        } else {
            NDocGraphicsImageDrawerByPath d = new NDocGraphicsImageDrawerByPath(nPath);
            d.drawImage(x, y, options, this);
        }
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
    public void draw(Shape shape) {
        g.draw(shape);
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


    public void transform3D(NDocMatrix3D transform3D) {
        if (transform3D != null) {
            this.transform3D = this.transform3D.multiply(transform3D);
        }
    }

    @Override
    public void project3D(NDocProjection3D projection3D) {
        if (projection3D != null) {
            this.projection3D = projection3D;
        }
    }

    public void shear(double shx, double shy) {
        g.shear(shx, shy);
    }


    /// ////////////////////////////////////////////////////


    private void draw3DElement3DLine(NDocElement3DLine pr, NDocPoint2D origin) {
        NDocPoint3D p1 = pr.getFrom().transform(transform3D);
        NDocPoint3D p2 = pr.getTo().transform(transform3D);

        NDocPoint2D point1 = projection3D.project(p1).plus(origin);
        NDocPoint2D point2 = projection3D.project(p2).plus(origin);

        draw2D(
                new NDocElement2DLine(point1, point2)
                        .setStartArrow(pr.getStartArrow())
                        .setEndArrow(pr.getEndArrow())
                        .setComposite(pr.getComposite())
                        .setBackgroundPaint(pr.getBackgroundPaint())
                        .setLinePaint(pr.getLinePaint())
                        .setLineStroke(pr.getLineStroke())
        );
    }

    private void draw3DElement3DArc(NDocElement3DArc pr, NDocPoint2D origin) {
        double x = origin.x;
        double y = origin.y;
        NDocPoint3D p1 = pr.getFrom().transform(transform3D);
        NDocPoint3D p2 = pr.getTo().transform(transform3D);

        NDocPoint2D point1 = projection3D.project(p1);
        NDocPoint2D point2 = projection3D.project(p2);
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
    }

    private void draw3DElement3DPolygon(NDocElement3DPolygon pr, NDocPoint2D origin) {
        double x = origin.x;
        double y = origin.y;
        NDocPoint3D[] nodes = pr.getNodes();
        double[] xx = new double[nodes.length];
        double[] yy = new double[nodes.length];
        for (int i = 0; i < xx.length; i++) {
            NDocPoint3D p = nodes[i].transform(transform3D);
            NDocPoint2D pp = projection3D.project(p);
            xx[i] = (pp.x + x);
            yy[i] = (pp.y + y);
        }
        double d = NDocD3Utils.surfaceNormal(nodes[0], nodes[1], nodes[2]).dot(getLight3D().orientation());
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
    }

    private void draw3DElement3DPolyline(NDocElement3DPolyline pr, NDocPoint2D origin) {
        double x = origin.x;
        double y = origin.y;
        NDocPoint3D[] nodes = pr.getNodes();
        double[] xx = new double[nodes.length];
        double[] yy = new double[nodes.length];
        for (int i = 0; i < xx.length; i++) {
            NDocPoint3D p = nodes[i].transform(transform3D);
            NDocPoint2D pp = projection3D.project(p);
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
    }

    private void draw3DElement3DTriangle(NDocElement3DTriangle pr, NDocPoint2D origin) {
        double x = origin.x;
        double y = origin.y;
        double[] xx = new double[3];
        double[] yy = new double[3];

        NDocPoint3D p1 = pr.getP1();
        NDocPoint2D pp1 = projection3D.project(p1.transform(transform3D));
        xx[0] = (pp1.x + x);
        yy[0] = (pp1.y + y);

        NDocPoint3D p2 = pr.getP2();
        NDocPoint2D pp2 = projection3D.project(p2.transform(transform3D));
        xx[1] = (pp2.x + x);
        yy[1] = (pp2.y + y);

        NDocPoint3D p3 = pr.getP3();
        NDocPoint2D pp3 = projection3D.project(p3.transform(transform3D));
        xx[2] = (pp3.x + x);
        yy[2] = (pp3.y + y);

        double d = NDocD3Utils.surfaceNormal(p1, p2, p3).dot(getLight3D().orientation());
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
    }


    private void draw2DHElement2DLine(NDocElement2DLine pr) {
        NDocPoint2D a = pr.getFrom();
        NDocPoint2D b = pr.getTo();
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
        drawArrayHead(a, a.minus(b).asVector(), pr.getStartArrow());
        drawArrayHead(b, b.minus(a).asVector(), pr.getEndArrow());
        setPaint(oldPaint);
        setStroke(oldStroke);
    }

    private void draw2DHElement2DQuadCurve(NDocElement2DQuadCurve pr) {
        NDocPoint2D a = pr.getFrom();
        NDocPoint2D b = pr.getTo();
        NDocPoint2D c = pr.getCtrl();
        Paint oldPaint = g.getPaint();
        Stroke oldStroke = g.getStroke();
        if (pr.getLinePaint() != null) {
            setPaint(pr.getLinePaint());
        }
        if (pr.getLineStroke() != null) {
            setStroke(pr.getLineStroke());
        }
        QuadCurve2D q = new QuadCurve2D.Double();

        q.setCurve(a.x, a.y, c.x, c.y, b.x, b.y);

        g.draw(q);

        //restore default stroke?
        setStroke(oldStroke);
        drawArrayHead(a, a.minus(b).asVector(), pr.getStartArrow());
        drawArrayHead(b, b.minus(a).asVector(), pr.getEndArrow());
        setPaint(oldPaint);
        setStroke(oldStroke);
    }

    private void draw2DHElement2DCubicCurve(NDocElement2DCubicCurve pr) {
        NDocPoint2D a = pr.getFrom();
        NDocPoint2D b = pr.getTo();
        NDocPoint2D c1 = pr.getCtrl1();
        NDocPoint2D c2 = pr.getCtrl2();
        Paint oldPaint = g.getPaint();
        Stroke oldStroke = g.getStroke();
        if (pr.getLinePaint() != null) {
            setPaint(pr.getLinePaint());
        }
        if (pr.getLineStroke() != null) {
            setStroke(pr.getLineStroke());
        }
        CubicCurve2D q = new CubicCurve2D.Double();

        q.setCurve(a.x, a.y,
                c1.x, c1.y,
                c2.x, c2.y,
                b.x, b.y);

        g.draw(q);

        //restore default stroke?
        setStroke(oldStroke);
        drawArrayHead(a, a.minus(b).asVector(), pr.getStartArrow());
        drawArrayHead(b, b.minus(a).asVector(), pr.getEndArrow());
        setPaint(oldPaint);
        setStroke(oldStroke);
    }

    private void draw2DHElement2DPolyline(NDocElement2DPolyline pr) {
        Paint oldPaint = g.getPaint();
        Stroke oldStroke = g.getStroke();
        if (pr.getLinePaint() != null) {
            setPaint(pr.getLinePaint());
        }
        if (pr.getLineStroke() != null) {
            setStroke(pr.getLineStroke());
        }
        NDocPoint2D[] nodes = pr.getNodes();
        int[] xx = new int[nodes.length];
        int[] yy = new int[nodes.length];
        for (int i = 0; i < xx.length; i++) {
            NDocPoint2D pp = nodes[i];
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
    }

    private void draw2DHElement2DPolygon(NDocElement2DPolygon pr) {
        Paint oldPaint = g.getPaint();
        Stroke oldStroke = g.getStroke();
        if (pr.getLinePaint() != null) {
            setPaint(pr.getLinePaint());
        }
        if (pr.getLineStroke() != null) {
            setStroke(pr.getLineStroke());
        }
        NDocPoint2D[] nodes = pr.getNodes();
        int[] xx = new int[nodes.length];
        int[] yy = new int[nodes.length];
        for (int i = 0; i < xx.length; i++) {
            NDocPoint2D pp = nodes[i];
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
    }


    private NDocGraphicsImageDrawer putCache(Object[] keys, NDocGraphicsImageDrawer value) {
        for (Object key : keys) {
            imageCache.put(key, value);
        }
        return value;
    }

}
