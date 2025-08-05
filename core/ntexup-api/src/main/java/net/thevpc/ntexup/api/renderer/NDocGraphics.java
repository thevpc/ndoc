package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.NDocArrow;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NTxLight3D;
import net.thevpc.ntexup.api.document.elem3d.NTxMatrix3D;
import net.thevpc.ntexup.api.document.elem3d.NTxProjection3D;
import net.thevpc.ntexup.api.renderer.text.NDocTextOptions;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

import net.thevpc.ntexup.api.document.elem2d.*;

public interface NDocGraphics {

    NTxEngine engine();

    NDocGraphics copy();

    NTxLight3D getLight3D();

    NTxLight3D setLight3D(NTxLight3D light3D);

    void drawArrayHead(NTxPoint2D origin, NTxVector2D direction, NDocArrow arrow);

    AffineTransform getTransform();

    void setTransform(AffineTransform t);

    void setPaint(Paint red);

    void setColor(Color red);

    void fillOval(double x, double y, double w, double h);

    void drawOval(double x, double y, double w, double h);

    void fillRect(double x, double y, double w, double h);

    void drawRect(double x, double y, double w, double h);


    void fillRect(NTxBounds2 b);

    void drawRect(NTxBounds2 a);

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

    void drawImage(NPath image, double x, double y, NTxImageOptions options);

    Color getSecondaryColor();

    void setSecondaryColor(Color secondaryColor);

    void fillSphere(double x, double y, double w, double h, double lightAngle, float radius);

    void setComposite(Composite composite);

    void fill(Shape shape);

    void draw(Shape shape);

    void setStroke(Stroke stroke);

    Stroke getStroke();

    void draw3D(NtxElement3D element3D, NTxPoint2D origin);

    void draw2D(NtxElement2D element2D);

    void transform3D(NTxMatrix3D transform3D);

    void project3D(NTxProjection3D projection3D);

    void shear(double shx, double shy);

    void transform(AffineTransform tx);

    void scale(double sx, double sy);

    void rotate(double theta, double x, double y);

    void rotate(double theta);

    void dispose();

    Shape createShape(NElement e);

    Stroke createStroke(NElement strokeElem);

}
