package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocArrow;
import net.thevpc.ndoc.api.document.elem3d.NDocElement3D;
import net.thevpc.ndoc.api.document.elem3d.NDocLight3D;
import net.thevpc.ndoc.api.document.elem3d.NDocMatrix3D;
import net.thevpc.ndoc.api.document.elem3d.NDocProjection3D;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

import net.thevpc.ndoc.api.document.elem2d.*;

public interface NDocGraphics {

    NDocEngine engine();

    NDocGraphics copy();

    NDocLight3D getLight3D();

    NDocLight3D setLight3D(NDocLight3D light3D);

    void drawArrayHead(NDocPoint2D origin, Vector2D direction, NDocArrow arrow);

    AffineTransform getTransform();

    void setTransform(AffineTransform t);

    void setPaint(Paint red);

    void setColor(Color red);

    void fillOval(double x, double y, double w, double h);

    void drawOval(double x, double y, double w, double h);

    void fillRect(double x, double y, double w, double h);

    void drawRect(double x, double y, double w, double h);


    void fillRect(NDocBounds2 b);

    void drawRect(NDocBounds2 a);

    Rectangle2D getStringBounds(String str);

    Rectangle2D getStringBounds(AttributedCharacterIterator iterator);

    FontMetrics getFontMetrics();

    FontMetrics getFontMetrics(Font f);

    FontRenderContext getFontRenderContext();

    Font getFont();

    Graphics2D graphics2D();

    void setFont(Font font);

    void drawString(AttributedCharacterIterator iterator, double x, double y);

    void drawString(String str, double x, double y);

    void drawString(String str, double x, double y, NDocTextOptions options);

    void debugString(String str, double x, double y);

    Color getColor();

    void drawArc(double x, double y, double w, double h, double startAngle, double endAngle);

    void drawLine(double x1, double y1, double x2, double y2);

    void fillPolygon(double[] xx, double[] yy, int length);

    void drawPolygon(double[] xx, double[] yy, int length);

    void drawPolyline(double[] xx, double[] yy, int length);

    void fillRoundRect(double x, double y, double w, double h, double cx, double cy);

    void drawRoundRect(double x, double y, double w, double h, double cx, double cy);

    void fill3DRect(double x, double y, double w, double h, boolean raised);

    void draw3DRect(double x, double y, double w, double h, boolean raised);

    void drawImage(Image image, double x, double y, ImageObserver o);

    void drawImage(NPath image, double x, double y, NDocImageOptions options);

    Color getSecondaryColor();

    void setSecondaryColor(Color secondaryColor);

    void fillSphere(double x, double y, double w, double h, double lightAngle, float radius);

    void setComposite(Composite composite);

    void fill(Shape shape);

    void draw(Shape shape);

    void setStroke(Stroke stroke);

    Stroke getStroke();

    void draw3D(NDocElement3D element3D, NDocPoint2D origin);

    void draw2D(NDocElement2D element2D);

    void transform3D(NDocMatrix3D transform3D);

    void project3D(NDocProjection3D projection3D);

    void shear(double shx, double shy);

    void transform(AffineTransform tx);

    void scale(double sx, double sy);

    void rotate(double theta, double x, double y);

    void rotate(double theta);

    void dispose();

    Shape createShape(NElement e);

    Stroke createStroke(NElement strokeElem);

}
