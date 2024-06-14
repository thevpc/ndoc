package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.model.HArrayHead;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HElement2D;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem2d.Vector2D;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.Light3D;
import net.thevpc.halfa.api.model.elem3d.Matrix3D;
import net.thevpc.halfa.api.model.elem3d.Projection3D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public interface HGraphics {

    Light3D getLight3D();

    Light3D setLight3D(Light3D light3D);

    void drawArrayHead(HPoint2D origin, Vector2D direction,
                       double arrowWidth, double arrowHeight,
                       HArrayHead head
                       );

    void setColor(Color red);

    void fillOval(double x, double y, double w, double h);

    void drawOval(double x, double y, double w, double h);

    void fillRect(double x, double y, double w, double h);

    void drawRect(double x, double y, double w, double h);

    void fillRect(Bounds2 b);

    void drawRect(Bounds2 a);

    Rectangle2D getStringBounds(String str);

    FontMetrics getFontMetrics();

    Graphics2D context();

    void setPaint(Paint paint);

    void setFont(Font font);

    void drawString(String str, double x, double i);

    void debugString(String str, double x, double i);

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

    Color getSecondaryColor();

    void setSecondaryColor(Color secondaryColor);

    void fillSphere(double x, double y, double w, double h, double lightAngle, float radius);

    void setComposite(Composite composite);

    void fill(Shape shape);

    void setStroke(Stroke stroke);

    Stroke getStroke();

    void draw3D(HElement3D element3D, HPoint2D origin);

    void draw2D(HElement2D element2D);

    void transform3D(Matrix3D transform3D);

    void project3D(Projection3D projection3D);
}
